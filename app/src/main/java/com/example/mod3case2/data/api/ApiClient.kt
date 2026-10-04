package com.example.mod3case2.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    
    // Default emulator local address
    private var baseUrl: String = "http://10.0.2.2:8000/"
    
    private var apiServiceInstance: ApiService? = null

    fun getBaseUrl(): String = baseUrl

    fun setBaseUrl(newUrl: String) {
        val formatted = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        if (baseUrl != formatted) {
            baseUrl = formatted
            apiServiceInstance = null // Invalidate cached instance
        }
    }

    fun getService(): ApiService {
        if (apiServiceInstance == null) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiServiceInstance = retrofit.create(ApiService::class.java)
        }
        return apiServiceInstance!!
    }
}
