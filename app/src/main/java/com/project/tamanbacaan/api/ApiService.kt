package com.caffeinatedr4t.tamanbacaan.api

import com.caffeinatedr4t.tamanbacaan.api.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Authentication
    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Books
    @GET("books")
    fun getAllBooks(): Call<List<BookResponse>>

    @GET("books/{id}")
    fun getBookById(@Path("id") id: String): Call<BookResponse>

    @POST("books")
    fun createBook(@Body book: CreateBookRequest): Call<BookResponse>

    // Users
    @GET("users")
    fun getAllUsers(): Call<List<UserResponse>>

    @POST("users")
    fun createUser(@Body user: CreateUserRequest): Call<UserResponse>

    // Events
    @GET("events")
    fun getEvents(): Call<List<EventResponse>>

    @POST("events")
    fun createEvent(@Body event: CreateEventRequest): Call<EventResponse>
}