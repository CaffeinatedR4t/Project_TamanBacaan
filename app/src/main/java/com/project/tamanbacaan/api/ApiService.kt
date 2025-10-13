package com.caffeinatedr4t.tamanbacaan.api

import com.caffeinatedr4t.tamanbacaan.api.model.LoginRequest
import com.caffeinatedr4t.tamanbacaan.api.model.LoginResponse
import com.caffeinatedr4t.tamanbacaan.api.model.BookApi
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // --- Authentication Endpoints (Masih Internal/Simulasi) ---
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("transactions/return")
    fun confirmReturn(@Body transactionId: String): Call<LoginResponse>

    // --- Public Book Endpoints (Menggunakan API yang diminta) ---

    // Endpoint utama yang mengembalikan daftar buku (diasumsikan)
    @GET("api")
    fun getBookList(): Call<List<BookApi>>

    // Endpoint untuk pencarian buku (Asumsi: API mendukung parameter query)
    @GET("api/search")
    fun searchBooks(@Query("query") query: String): Call<List<BookApi>>

    // Endpoint untuk mendapatkan detail buku
    @GET("api/detail")
    fun getBookDetail(@Query("id") id: String): Call<BookApi>
}