package com.retno.storyapp

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.retno.storyapp.api.Injection
import com.retno.storyapp.models.ListStoryItem
import kotlinx.coroutines.runBlocking

class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = mutableListOf<ListStoryItem>()

    override fun onCreate() {
        loadStories()
    }

    override fun onDataSetChanged() {

        mWidgetItems.clear()
        loadStories()
    }

    private fun loadStories() {
        val repository = Injection.provideRepository(mContext)
        runBlocking {
            try {
                val response = repository.getStories()
                if (!response.error!!) {
                    mWidgetItems.addAll(response.listStory?.filterNotNull()?.take(4) ?: emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        mWidgetItems.clear()
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)

        if (position < mWidgetItems.size) {
            val story = mWidgetItems[position]

            rv.setTextViewText(R.id.textView, story.name)
            val bitmap = Glide.with(mContext)
                .asBitmap()
                .load(story.photoUrl)
                .submit()
                .get()
            rv.setImageViewBitmap(R.id.imageView, bitmap)

            val extras = Intent().apply {
                putExtra(ImagesBannerWidget.EXTRA_ITEM, position)
            }
            rv.setOnClickFillInIntent(R.id.imageView, extras)
        }

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
