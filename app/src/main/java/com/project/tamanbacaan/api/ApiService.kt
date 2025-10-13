package com.caffeinatedr4t.tamanbacaan.api

import com.caffeinatedr4t.tamanbacaan.api.model.LoginRequest
import com.caffeinatedr4t.tamanbacaan.api.model.LoginResponse
import com.caffeinatedr4t.tamanbacaan.api.model.BookApi
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Endpoint untuk konfirmasi pengembalian (Req. Anggota: Online Confirmation)
    @POST("transactions/return")
    fun confirmReturn(@Body transactionId: String): Call<LoginResponse>

    // Tambahkan endpoint API lainnya di sini untuk ADMIN
    // @POST("books/add") fun addBook(@Body book: BookApi): Call<BookApi>
}