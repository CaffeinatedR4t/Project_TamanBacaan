package com.caffeinatedr4t.tamanbacaan.api

import okhttp3.Interceptor
import okhttp3.Response
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager

class AuthInterceptor(
    private val prefs: SharedPrefsManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefs.getUserToken()

        val request = if (!token.isNullOrEmpty()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
