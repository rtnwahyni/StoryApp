package com.retno.storyapp.ui

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.retno.storyapp.StoryViewModelFactory
import com.retno.storyapp.api.Injection
import com.retno.storyapp.databinding.ActivityAddStoryBinding
import com.retno.storyapp.viemodel.StoryViewModel

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var storyViewModel: StoryViewModel

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                storyViewModel.setPhotoUri(storyViewModel.photoUri.value)
            } else {
                Toast.makeText(this, "Camera capture failed", Toast.LENGTH_SHORT).show()
            }
        }

    private val photoPickerResult = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            storyViewModel.setPhotoUri(uri)
            Log.d("AddStoryActivity", "Selected photo URI: $uri")
        } else {
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                openAppSettings()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyRepository = Injection.provideRepository(this)
        val viewModelFactory = StoryViewModelFactory(storyRepository)
        storyViewModel = ViewModelProvider(this, viewModelFactory)[StoryViewModel::class.java]

        storyViewModel.photoUri.observe(this) { uri ->
            if (uri != null) {
                binding.ivPhoto.setImageURI(uri)
            }
        }

        binding.buttonCamera.setOnClickListener {
            checkAndRequestCameraPermission()
        }

        binding.buttonGallery.setOnClickListener {
            openGallery()
        }

        binding.buttonAdd.setOnClickListener {
            val description = binding.edAddDescription.text.toString().trim()

            if (storyViewModel.photoUri.value != null && description.isNotEmpty()) {
                showLoading(true)
                storyViewModel.addStory(storyViewModel.photoUri.value!!, description)
            } else {
                Toast.makeText(this, "Please select a photo and enter a description", Toast.LENGTH_SHORT).show()
            }
        }

        storyViewModel.addStorySuccess.observe(this) { isSuccess ->
            showLoading(false)
            if (isSuccess) {
                Toast.makeText(this, "Story added successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add story", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonAdd.isEnabled = !isLoading
    }

    private fun checkAndRequestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "photo_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        storyViewModel.setPhotoUri(contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues))

        storyViewModel.photoUri.value?.let {
            cameraResult.launch(it)
        } ?: Toast.makeText(this, "Failed to create photo URI", Toast.LENGTH_SHORT).show()
    }

    private fun openGallery() {
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

        photoPickerResult.launch(pickVisualMediaRequest)
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}
