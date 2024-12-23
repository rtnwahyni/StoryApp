package com.retno.storyapp.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {

    private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

    fun getApiService(token: String = ""): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                var newRequest = chain.request()
                if (token.isNotEmpty()) {
                    newRequest = chain.request().newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                }
                chain.proceed(newRequest)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
