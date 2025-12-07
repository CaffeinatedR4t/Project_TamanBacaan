package com.caffeinatedr4t.tamanbacaan.api

import android.content.Context
import android.util.Log
import com.caffeinatedr4t.tamanbacaan.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    private const val TAG = "ApiConfig"
    
    // Environment configuration
    // Set USE_PRODUCTION = true to use production HTTPS endpoint
    // Set USE_PRODUCTION = false to use development HTTP endpoint
    private const val USE_PRODUCTION = false
    
    // CHANGE THIS TO YOUR COMPUTER'S IP ADDRESS when testing on physical device
    // For emulator: use "10.0.2.2"
    // For real device: use your computer's IP (e.g., "192.168.1.100")
    private const val DEV_HOST = "10.0.2.2:3000"
    
    // Base URL selection based on environment
    private val BASE_URL: String
        get() = if (USE_PRODUCTION) {
            BuildConfig.BASE_URL_PROD
        } else {
            "http://$DEV_HOST/api/"
        }
    
    init {
        Log.i(TAG, "API Config initialized")
        Log.i(TAG, "Environment: ${if (USE_PRODUCTION) "PRODUCTION" else "DEVELOPMENT"}")
        Log.i(TAG, "Base URL: $BASE_URL")
        Log.i(TAG, "Logging enabled: ${BuildConfig.ENABLE_LOGGING}")
    }
    
    /**
     * Create HTTP logging interceptor with custom logger
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.ENABLE_LOGGING) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            logger = object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("HTTP", message)
                }
            }
        }
    }
    
    /**
     * Create request/response logging interceptor
     * Only logs if BuildConfig.ENABLE_LOGGING is true
     */
    private fun createRequestResponseInterceptor() = okhttp3.Interceptor { chain ->
        val request = chain.request()
        if (BuildConfig.ENABLE_LOGGING) {
            Log.d(TAG, "→ ${request.method} ${request.url}")
        }
        val response = chain.proceed(request)
        if (BuildConfig.ENABLE_LOGGING) {
            Log.d(TAG, "← ${response.code} ${request.url}")
        }
        response
    }

    /**
     * Get API service without authentication (for login/register)
     */
    fun getApiService(): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(createRequestResponseInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    /**
     * Get API service with authentication token (for authenticated requests)
     */
    fun getApiService(context: Context): ApiService {
        val authInterceptor = AuthInterceptor(context)

        val client = OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(authInterceptor) // Add auth token to requests
            .addInterceptor(createRequestResponseInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
    
    /**
     * Get current base URL for debugging purposes
     */
    fun getBaseUrl(): String = BASE_URL
    
    /**
     * Check if production mode is enabled
     */
    fun isProduction(): Boolean = USE_PRODUCTION
}
