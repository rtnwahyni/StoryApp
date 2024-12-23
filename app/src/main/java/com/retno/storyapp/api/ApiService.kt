package com.retno.storyapp.api

import com.retno.storyapp.models.AddNewStoryResponse
import com.retno.storyapp.models.LoginRequest
import com.retno.storyapp.models.LoginResponse
import com.retno.storyapp.models.RegisterResponse
import com.retno.storyapp.models.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") authorization: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): AddNewStoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int? = 1
    ): StoryResponse
}
