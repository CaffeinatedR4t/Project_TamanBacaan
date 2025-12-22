package com.caffeinatedr4t.tamanbacaan.api

import com.caffeinatedr4t.tamanbacaan.api.model.LoginRequest
import com.caffeinatedr4t.tamanbacaan.api.model.LoginResponse
import com.caffeinatedr4t.tamanbacaan.api.model.RegisterRequest
import com.caffeinatedr4t.tamanbacaan.data.CreateBookRequest
import com.caffeinatedr4t.tamanbacaan.data.UpdateProfileRequest
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.models.Transaction
import com.caffeinatedr4t.tamanbacaan.models.User
import com.caffeinatedr4t.tamanbacaan.api.model.EventResponse
import com.caffeinatedr4t.tamanbacaan.api.model.EventRequest
import com.caffeinatedr4t.tamanbacaan.models.RecommendationResponse
import com.project.tamanbacaan.api.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.*
import retrofit2.Call

interface ApiService {
    // Authentication Endpoints
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Book Endpoints
    @POST("books")
    suspend fun createBook(
        @Body request: CreateBookRequest
    ): Response<Book>

    @GET("books/{id}")
    suspend fun getBookById(
        @Path("id") id: String
    ): Response<Book>

    @GET("books")
    suspend fun getBooks(): Response<List<Book>>

    @PUT("books/{id}")
    suspend fun updateBook(@Path("id") id: String, @Body book: Book): Response<Book>

    @DELETE("books/{id}")
    suspend fun deleteBook(@Path("id") id: String): Response<Unit>

    @POST("events")
    fun addEvent(
        @Header("Authorization") token: String,
        @Body event: EventRequest
    ): Call<EventResponse>
    @GET("events")
    fun getEvents(): Call<List<EventResponse>>

    @DELETE("events/{id}")
    fun deleteEvent(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<Unit>

    // Transaction Endpoints
    @GET("transactions/user/{userId}")
    suspend fun getUserTransactions(@Path("userId") userId: String): Response<List<Transaction>>

    @POST("transactions")
    suspend fun borrowBook(@Body transaction: Transaction): Response<Transaction>

    @PUT("transactions/{id}/return")
    suspend fun returnBook(@Path("id") transactionId: String): Response<Transaction>

    // [TAMBAHKAN INI] Endpoint Rekomendasi
    @GET("books/recommendations/{userId}")
    suspend fun getRecommendations(
        @Path("userId") userId: String
    ): Response<RecommendationResponse>

    @GET("users")
    suspend fun getAllMembers(): Response<List<User>>

    @PUT("users/{id}/status")
    suspend fun updateUserStatus(@Path("id") id: String, @Body statusUpdate: Map<String, Boolean>): Response<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>

    // [BARU] Endpoint untuk Cek Status User (Force Logout)
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<User>

    // [BARU] Ambil profil user yang sedang login (butuh Token)
    @GET("auth/me")
    suspend fun getProfile(@Header("Authorization") token: String): Response<LoginResponse>

    @PUT("auth/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<LoginResponse>

    // [TAMBAHKAN INI] Untuk Admin mengambil semua transaksi
    @GET("transactions")
    suspend fun getAllTransactions(): Response<List<Transaction>>

    // [TAMBAHKAN INI] Endpoint Approve (Setuju)
    @PUT("transactions/{id}/approve")
    suspend fun approveTransaction(@Path("id") id: String): Response<Transaction>

    // [TAMBAHKAN INI] Endpoint Reject (Tolak)
    @PUT("transactions/{id}/reject")
    suspend fun rejectTransaction(@Path("id") id: String): Response<Transaction>

    // Toggle Bookmark
    @PUT("users/{id}/bookmark")
    suspend fun toggleBookmark(@Path("id") id: String, @Body body: Map<String, String>): Response<Map<String, Any>>

    @POST("books/{id}/review")
    suspend fun addReview(
        @Path("id") bookId: String,
        @Body review: com.caffeinatedr4t.tamanbacaan.api.model.ReviewRequest
    ): Response<Book>

}