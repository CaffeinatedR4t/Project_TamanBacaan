package com.caffeinatedr4t.tamanbacaan.api

import com.caffeinatedr4t.tamanbacaan.api.model.LoginRequest
import com.caffeinatedr4t.tamanbacaan.api.model.LoginResponse
import com.caffeinatedr4t.tamanbacaan.api.model.RegisterRequest
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.models.EventNotification
import com.caffeinatedr4t.tamanbacaan.models.Transaction
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication Endpoints
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Book Endpoints
    @GET("books")
    suspend fun getAllBooks(): Response<List<Book>>

    @GET("books/{id}")
    suspend fun getBookById(@Path("id") id: String): Response<Book>

    // Event Endpoints
    @GET("events")
    suspend fun getAllEvents(): Response<List<EventNotification>>

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: String): Response<EventNotification>

    // Transaction Endpoints
    @GET("transactions/user/{userId}")
    suspend fun getUserTransactions(@Path("userId") userId: String): Response<List<Transaction>>

    @POST("transactions")
    suspend fun borrowBook(@Body transaction: Transaction): Response<Transaction>

    @PUT("transactions/{id}/return")
    suspend fun returnBook(@Path("id") transactionId: String): Response<Transaction>
}