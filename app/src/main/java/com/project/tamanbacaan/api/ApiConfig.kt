package com.caffeinatedr4t.tamanbacaan.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson. GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    // CHANGE THIS TO YOUR COMPUTER'S IP ADDRESS
    // For emulator: use "10.0.2.2"
    // For real device: use your computer's IP (e.g., "192.168.1.100")
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    // If testing on real device, change to:
    // private const val BASE_URL = "http://192.168.1.100:3000/api/"
    // (Replace with YOUR IP from ipconfig)

    fun getApiService(): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit. SECONDS)
            .readTimeout(30, TimeUnit. SECONDS)
            .writeTimeout(30, TimeUnit. SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}