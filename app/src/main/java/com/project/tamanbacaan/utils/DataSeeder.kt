package com.caffeinatedr4t.tamanbacaan.utils

import android.content.Context
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.api.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Utility class for seeding test data into MongoDB
 */
object DataSeeder {
    
    interface SeedCallback {
        fun onProgress(message: String)
        fun onComplete(success: Boolean, message: String)
    }
    
    /**
     * Seed all test data (books, users, events) to MongoDB
     */
    fun seedAllData(context: Context, callback: SeedCallback) {
        val apiService = ApiConfig.getApiService(context)
        var totalItems = 0
        var completedItems = 0
        var hasError = false
        
        // Count total items
        val books = getSampleBooks()
        val users = getSampleUsers()
        val events = getSampleEvents()
        totalItems = books.size + users.size + events.size
        
        callback.onProgress("Memulai proses seeding data...")
        
        // Seed books
        books.forEach { book ->
            apiService.createBook(book).enqueue(object : Callback<BookResponse> {
                override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                    completedItems++
                    if (response.isSuccessful) {
                        callback.onProgress("Buku ditambahkan: ${book.title} ($completedItems/$totalItems)")
                    } else {
                        hasError = true
                        callback.onProgress("Gagal menambahkan: ${book.title}")
                    }
                    checkCompletion(completedItems, totalItems, hasError, callback)
                }
                
                override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                    completedItems++
                    hasError = true
                    callback.onProgress("Error: ${book.title} - ${t.message}")
                    checkCompletion(completedItems, totalItems, hasError, callback)
                }
            })
        }
        
        // Seed users
        users.forEach { user ->
            apiService.createUser(user).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    completedItems++
                    if (response.isSuccessful) {
                        callback.onProgress("User ditambahkan: ${user.fullName} ($completedItems/$totalItems)")
                    } else {
                        hasError = true
                        callback.onProgress("Gagal menambahkan: ${user.fullName}")
                    }
                    checkCompletion(completedItems, totalItems, hasError, callback)
                }
                
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    completedItems++
                    hasError = true
                    callback.onProgress("Error: ${user.fullName} - ${t.message}")
                    checkCompletion(completedItems, totalItems, hasError, callback)
                }
            })
        }
        
        // Seed events
        events.forEach { event ->
            apiService.createEvent(event).enqueue(object : Callback<EventResponse> {
                override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                    completedItems++
                    if (response.isSuccessful) {
                        callback.onProgress("Event ditambahkan: ${event.title} ($completedItems/$totalItems)")
                    } else {
                        hasError = true
                        callback.onProgress("Gagal menambahkan: ${event.title}")
                    }
                    checkCompletion(completedItems, totalItems, hasError, callback)
                }
                
                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    completedItems++
                    hasError = true
                    callback.onProgress("Error: ${event.title} - ${t.message}")
                    checkCompletion(completedItems, totalItems, hasError, callback)
                }
            })
        }
    }
    
    private fun checkCompletion(completed: Int, total: Int, hasError: Boolean, callback: SeedCallback) {
        if (completed == total) {
            if (hasError) {
                callback.onComplete(false, "Seeding selesai dengan beberapa error")
            } else {
                callback.onComplete(true, "✅ Data berhasil ditambahkan ke MongoDB!")
            }
        }
    }
    
    /**
     * Get sample books data
     */
    private fun getSampleBooks(): List<CreateBookRequest> {
        return listOf(
            CreateBookRequest(
                title = "Laskar Pelangi",
                author = "Andrea Hirata",
                category = "Fiksi",
                publisher = "Bentang Pustaka",
                year = 2005,
                isbn = "9789793062792",
                stock = 5,
                totalCopies = 5,
                description = "Novel tentang perjuangan anak-anak Belitung dalam menempuh pendidikan"
            ),
            CreateBookRequest(
                title = "Bumi Manusia",
                author = "Pramoedya Ananta Toer",
                category = "Fiksi",
                publisher = "Hasta Mitra",
                year = 1980,
                isbn = "9789799731234",
                stock = 3,
                totalCopies = 3,
                description = "Novel sejarah tentang kehidupan di Indonesia pada masa kolonial"
            ),
            CreateBookRequest(
                title = "Perahu Kertas",
                author = "Dee Lestari",
                category = "Fiksi",
                publisher = "Bentang Pustaka",
                year = 2009,
                isbn = "9789793062808",
                stock = 4,
                totalCopies = 4,
                description = "Kisah cinta dan perjuangan dua anak muda mengejar mimpi"
            ),
            CreateBookRequest(
                title = "Harry Potter and the Philosopher's Stone",
                author = "J.K. Rowling",
                category = "Fiksi",
                publisher = "Bloomsbury",
                year = 1997,
                isbn = "9780747532699",
                stock = 6,
                totalCopies = 6,
                description = "Petualangan Harry Potter di Hogwarts"
            ),
            CreateBookRequest(
                title = "To Kill a Mockingbird",
                author = "Harper Lee",
                category = "Fiksi",
                publisher = "J.B. Lippincott & Co.",
                year = 1960,
                isbn = "9780061120084",
                stock = 3,
                totalCopies = 3,
                description = "Novel klasik tentang rasisme dan keadilan"
            ),
            CreateBookRequest(
                title = "1984",
                author = "George Orwell",
                category = "Fiksi",
                publisher = "Secker & Warburg",
                year = 1949,
                isbn = "9780451524935",
                stock = 4,
                totalCopies = 4,
                description = "Dystopian novel tentang totalitarianisme"
            ),
            CreateBookRequest(
                title = "The Great Gatsby",
                author = "F. Scott Fitzgerald",
                category = "Fiksi",
                publisher = "Charles Scribner's Sons",
                year = 1925,
                isbn = "9780743273565",
                stock = 3,
                totalCopies = 3,
                description = "Kisah tentang American Dream di era 1920-an"
            ),
            CreateBookRequest(
                title = "Pulang",
                author = "Tere Liye",
                category = "Fiksi",
                publisher = "Republika",
                year = 2015,
                isbn = "9786021318717",
                stock = 5,
                totalCopies = 5,
                description = "Novel tentang pencarian jati diri"
            ),
            CreateBookRequest(
                title = "Ayat-Ayat Cinta",
                author = "Habiburrahman El Shirazy",
                category = "Fiksi",
                publisher = "Republika",
                year = 2004,
                isbn = "9789797694104",
                stock = 4,
                totalCopies = 4,
                description = "Kisah cinta seorang pemuda Indonesia di Mesir"
            ),
            CreateBookRequest(
                title = "Negeri 5 Menara",
                author = "Ahmad Fuadi",
                category = "Fiksi",
                publisher = "Gramedia",
                year = 2009,
                isbn = "9789792248074",
                stock = 5,
                totalCopies = 5,
                description = "Kisah inspiratif tentang perjuangan santri"
            ),
            CreateBookRequest(
                title = "Sapiens",
                author = "Yuval Noah Harari",
                category = "Non-Fiksi",
                publisher = "Harper",
                year = 2011,
                isbn = "9780062316097",
                stock = 4,
                totalCopies = 4,
                description = "Sejarah singkat umat manusia"
            ),
            CreateBookRequest(
                title = "Educated",
                author = "Tara Westover",
                category = "Non-Fiksi",
                publisher = "Random House",
                year = 2018,
                isbn = "9780399590504",
                stock = 3,
                totalCopies = 3,
                description = "Memoir tentang pendidikan dan keluarga"
            ),
            CreateBookRequest(
                title = "The Alchemist",
                author = "Paulo Coelho",
                category = "Fiksi",
                publisher = "HarperCollins",
                year = 1988,
                isbn = "9780061122415",
                stock = 5,
                totalCopies = 5,
                description = "Perjalanan spiritual mencari harta karun"
            ),
            CreateBookRequest(
                title = "Atomic Habits",
                author = "James Clear",
                category = "Non-Fiksi",
                publisher = "Avery",
                year = 2018,
                isbn = "9780735211292",
                stock = 6,
                totalCopies = 6,
                description = "Panduan membentuk kebiasaan baik"
            ),
            CreateBookRequest(
                title = "Filosofi Teras",
                author = "Henry Manampiring",
                category = "Non-Fiksi",
                publisher = "Kompas",
                year = 2018,
                isbn = "9786024246945",
                stock = 5,
                totalCopies = 5,
                description = "Filsafat Stoicism untuk kehidupan modern"
            )
        )
    }
    
    /**
     * Get sample users data
     */
    private fun getSampleUsers(): List<CreateUserRequest> {
        return listOf(
            CreateUserRequest(
                fullName = "Budi Santoso",
                email = "budi.santoso@example.com",
                password = "password123",
                nik = "3171010101850001",
                addressRtRw = "RT 003/RW 005",
                addressKelurahan = "Menteng",
                addressKecamatan = "Menteng",
                phoneNumber = "081234567890",
                isVerified = true
            ),
            CreateUserRequest(
                fullName = "Siti Nurhaliza",
                email = "siti.nurhaliza@example.com",
                password = "password123",
                nik = "3171010202900002",
                addressRtRw = "RT 002/RW 004",
                addressKelurahan = "Kebayoran",
                addressKecamatan = "Kebayoran Baru",
                phoneNumber = "081234567891",
                isVerified = false
            ),
            CreateUserRequest(
                fullName = "Ahmad Rizki",
                email = "ahmad.rizki@example.com",
                password = "password123",
                nik = "3171010303120003",
                addressRtRw = "RT 003/RW 005",
                addressKelurahan = "Menteng",
                addressKecamatan = "Menteng",
                phoneNumber = "081234567892",
                isChild = true,
                parentName = "Budi Santoso"
            ),
            CreateUserRequest(
                fullName = "Dewi Lestari",
                email = "dewi.lestari@example.com",
                password = "password123",
                nik = "3171010404880004",
                addressRtRw = "RT 001/RW 003",
                addressKelurahan = "Kuningan",
                addressKecamatan = "Setiabudi",
                phoneNumber = "081234567893",
                isVerified = true
            ),
            CreateUserRequest(
                fullName = "Eko Prasetyo",
                email = "eko.prasetyo@example.com",
                password = "password123",
                nik = "3171010505920005",
                addressRtRw = "RT 004/RW 006",
                addressKelurahan = "Senayan",
                addressKecamatan = "Tanah Abang",
                phoneNumber = "081234567894",
                isVerified = false
            )
        )
    }
    
    /**
     * Get sample events data
     */
    private fun getSampleEvents(): List<CreateEventRequest> {
        return listOf(
            CreateEventRequest(
                title = "Lomba Baca Puisi Anak",
                message = "Lomba baca puisi untuk anak-anak usia 7-12 tahun. Akan diadakan pada 15 Juni 2025. Hadiah menarik menanti!",
                createdBy = "admin"
            ),
            CreateEventRequest(
                title = "Pelatihan Literasi Digital",
                message = "Workshop literasi digital untuk remaja dan dewasa. Belajar cara menggunakan internet dengan bijak. Tanggal: 1 Juli 2025",
                createdBy = "admin"
            ),
            CreateEventRequest(
                title = "Donasi Buku Gratis",
                message = "Program donasi buku berkelanjutan. Terima kasih untuk semua donatur yang telah menyumbang buku untuk taman bacaan kami!",
                createdBy = "admin"
            )
        )
    }
}
