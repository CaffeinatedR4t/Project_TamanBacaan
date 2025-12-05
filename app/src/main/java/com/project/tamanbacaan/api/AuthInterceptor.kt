package com.caffeinatedr4t.tamanbacaan.api

import android.content.Context
import com.caffeinatedr4t.tamanbacaan.utils.SharedPreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that automatically adds JWT authentication token to API requests.
 * This interceptor reads the token from SharedPreferencesHelper and adds it to the
 * Authorization header of every request.
 */
class AuthInterceptor(context: Context) : Interceptor {

    private val sharedPrefs = SharedPreferencesHelper(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Get the auth token from SharedPreferences
        val token = sharedPrefs.getToken()

        // If token exists, add it to the request header
        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
