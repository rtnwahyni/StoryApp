package com.retno.storyapp.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import com.retno.storyapp.api.ApiService
import com.retno.storyapp.helper.SessionManager
import com.retno.storyapp.models.AddNewStoryResponse
import com.retno.storyapp.models.StoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val context: Context
) {

    suspend fun getStories(): StoryResponse {
        val token = SessionManager.getAuthTokenSync(context) ?: throw Exception("Authentication token is missing")

        return withContext(Dispatchers.IO) {
            apiService.getStories("Bearer $token")
        }
    }

    suspend fun addNewStory(photoUri: Uri, description: String): AddNewStoryResponse {
        val token = SessionManager.getAuthToken(context).first() // Ambil token terbaru
            ?: throw Exception("Authentication token is missing")

        return withContext(Dispatchers.IO) {
            val photoFile = uriToFile(photoUri, context) ?: return@withContext AddNewStoryResponse(error = true, message = "Invalid photo URI")
            Log.d("StoryRepository", "File path: ${photoFile.absolutePath}")

            val requestFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

            try {
                val response = apiService.addNewStory(
                    "Bearer $token",
                    photoPart,
                    descriptionBody
                )
                Log.d("StoryRepository", "API Response: ${response.message}")
                return@withContext response
            } catch (e: Exception) {
                Log.e("StoryRepository", "Error uploading story: ${e.localizedMessage}")
                return@withContext AddNewStoryResponse(error = true, message = "Failed to upload story")
            }
        }
    }



    private fun uriToFile(uri: Uri, context: Context): File? {
        val contentResolver: ContentResolver = context.contentResolver
        var file: File? = null

        try {
            val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return null

            val tempFile = File(context.cacheDir,
                "temp_image_${System.currentTimeMillis()}.jpg")

            val outputStream: OutputStream = FileOutputStream(tempFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            inputStream.close()
            outputStream.close()

            file = tempFile
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(apiService: ApiService, context: Context): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: StoryRepository(apiService, context)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun getStoriesWithLocation(): StoryResponse {
        val token = SessionManager.getAuthTokenSync(context) ?: throw Exception("Authentication token is missing")

        return withContext(Dispatchers.IO) {
            apiService.getStoriesWithLocation("Bearer $token")
        }
    }
}
