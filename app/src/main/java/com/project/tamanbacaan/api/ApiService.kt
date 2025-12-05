package com.caffeinatedr4t.tamanbacaan.api

import com.caffeinatedr4t.tamanbacaan.api.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Authentication
    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Books
    @GET("api/books")
    fun getAllBooks(): Call<List<BookResponse>>

    @GET("api/books/{id}")
    fun getBookById(@Path("id") id: String): Call<BookResponse>

    // Users
    @GET("api/users")
    fun getAllUsers(): Call<List<UserResponse>>

    // Events
    @GET("api/events")
    fun getEvents(): Call<List<EventResponse>>
}