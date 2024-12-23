package com.retno.storyapp.viemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retno.storyapp.repository.StoryRepository
import com.retno.storyapp.models.ListStoryItem
import kotlinx.coroutines.launch
import android.net.Uri


class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _addStorySuccess = MutableLiveData<Boolean>()
    val addStorySuccess: LiveData<Boolean> = _addStorySuccess

    private val _photoUri = MutableLiveData<Uri?>()
    val photoUri: LiveData<Uri?> = _photoUri

    fun setPhotoUri(uri: Uri?) {
        _photoUri.value = uri
    }

    fun fetchStories() {
        viewModelScope.launch {
            try {
                val response = storyRepository.getStories()
                if (response.error == false) {
                    _stories.value = response.listStory?.filterNotNull() ?: emptyList()
                } else {
                    _errorMessage.value = response.message ?: "Error fetching stories"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
            }
        }
    }

    fun addStory(photoUri: Uri, description: String) {
        viewModelScope.launch {
            try {
                val response = storyRepository.addNewStory(photoUri, description)
                if (response.error == false) {
                    _addStorySuccess.value = true
                    fetchStories()
                } else {
                    _errorMessage.value = response.message ?: "Failed to add story"
                    _addStorySuccess.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
                _addStorySuccess.value = false
            }
        }
    }

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val response = storyRepository.getStoriesWithLocation()
                if (response.error == false) {
                    _stories.value = response.listStory?.filterNotNull() ?: emptyList()
                } else {
                    _errorMessage.value = response.message ?: "Error fetching stories"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
            }
        }
    }
}

