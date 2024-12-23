package com.retno.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.retno.storyapp.databinding.ItemStoryBinding
import com.retno.storyapp.models.ListStoryItem
import com.retno.storyapp.ui.DetailStoryActivity

class StoryAdapter : ListAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val storyItem = getItem(position)

        holder.binding.tvStoryName.text = storyItem.name
        holder.binding.tvStoryDescription.text = storyItem.description

        Glide.with(holder.itemView.context)
            .load(storyItem.photoUrl)
            .into(holder.binding.ivStoryImage)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailStoryActivity::class.java)
            intent.putExtra("STORY", storyItem)

            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context as Activity,
                Pair(holder.binding.ivStoryImage, "profile"),
                Pair(holder.binding.tvStoryName, "name"),
                Pair(holder.binding.tvStoryDescription, "description")
            )

            context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    class StoryViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)
}

class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
    override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem == newItem
    }
}
