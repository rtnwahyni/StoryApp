package com.retno.storyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.retno.storyapp.repository.StoryRepository
import com.retno.storyapp.viemodel.StoryViewModel

class StoryViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}