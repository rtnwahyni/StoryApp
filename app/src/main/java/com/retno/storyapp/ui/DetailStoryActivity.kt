package com.retno.storyapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.retno.storyapp.databinding.ActivityDetailStoryBinding
import com.bumptech.glide.Glide
import com.retno.storyapp.models.ListStoryItem

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyItem = intent.getParcelableExtra<ListStoryItem>("STORY")

        if (storyItem != null) {
            displayStoryDetails(storyItem)
        } else {
            Toast.makeText(this, "Story details are unavailable.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayStoryDetails(storyItem: ListStoryItem) {
        binding.tvStoryName.text = storyItem.name
        binding.tvStoryDescription.text = storyItem.description

        Glide.with(this)
            .load(storyItem.photoUrl)
            .into(binding.ivStoryImage)
    }
}
