package com.caffeinatedr4t.tamanbacaan.utils

import android.content.Context
import android.util.Log
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.api.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Utility class untuk seeding data testing ke MongoDB.
 * Digunakan oleh Admin untuk menambah data sample untuk keperluan testing aplikasi.
 */
object DataSeeder {
    private const val TAG = "DataSeeder"
    
    /**
     * Seed semua data (books, users, events) ke backend
     */
    fun seedAllData(context: Context, callback: (String) -> Unit) {
        var successCount = 0
        var failCount = 0
        val totalOperations = 3
        
        seedBooks(context) { booksResult ->
            if (booksResult.startsWith("Berhasil")) successCount++ else failCount++
            
            seedUsers(context) { usersResult ->
                if (usersResult.startsWith("Berhasil")) successCount++ else failCount++
                
                seedEvents(context) { eventsResult ->
                    if (eventsResult.startsWith("Berhasil")) successCount++ else failCount++
                    
                    val finalResult = "Selesai!\nBerhasil: $successCount\nGagal: $failCount\n\n" +
                                      "Buku: $booksResult\n" +
                                      "Users: $usersResult\n" +
                                      "Events: $eventsResult"
                    callback(finalResult)
                }
            }
        }
    }
    
    /**
     * Seed sample books ke backend
     */
    fun seedBooks(context: Context, callback: (String) -> Unit) {
        val apiService = ApiConfig.getApiService(context)
        val books = getSampleBooks()
        
        var successCount = 0
        var failCount = 0
        var completedCount = 0
        
        if (books.isEmpty()) {
            callback("Tidak ada buku untuk ditambahkan")
            return
        }
        
        books.forEach { book ->
            apiService.createBook(book).enqueue(object : Callback<BookResponse> {
                override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                    completedCount++
                    if (response.isSuccessful) {
                        successCount++
                        Log.d(TAG, "Book created: ${book.title}")
                    } else {
                        failCount++
                        Log.e(TAG, "Failed to create book: ${book.title}, code: ${response.code()}")
                    }
                    
                    if (completedCount == books.size) {
                        callback("Berhasil: $successCount, Gagal: $failCount dari ${books.size} buku")
                    }
                }
                
                override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                    completedCount++
                    failCount++
                    Log.e(TAG, "Error creating book: ${book.title}", t)
                    
                    if (completedCount == books.size) {
                        callback("Berhasil: $successCount, Gagal: $failCount dari ${books.size} buku")
                    }
                }
            })
        }
    }
    
    /**
     * Seed sample users ke backend
     * Note: Uses non-authenticated API service because registration is a public endpoint
     */
    fun seedUsers(context: Context, callback: (String) -> Unit) {
        val apiService = ApiConfig.getApiService() // No auth needed for registration
        val users = getSampleUsers()
        
        var successCount = 0
        var failCount = 0
        var completedCount = 0
        
        if (users.isEmpty()) {
            callback("Tidak ada user untuk ditambahkan")
            return
        }
        
        users.forEach { user ->
            apiService.register(user).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    completedCount++
                    if (response.isSuccessful) {
                        successCount++
                        Log.d(TAG, "User created: ${user.fullName}")
                    } else {
                        failCount++
                        Log.e(TAG, "Failed to create user: ${user.fullName}, code: ${response.code()}")
                    }
                    
                    if (completedCount == users.size) {
                        callback("Berhasil: $successCount, Gagal: $failCount dari ${users.size} user")
                    }
                }
                
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    completedCount++
                    failCount++
                    Log.e(TAG, "Error creating user: ${user.fullName}", t)
                    
                    if (completedCount == users.size) {
                        callback("Berhasil: $successCount, Gagal: $failCount dari ${users.size} user")
                    }
                }
            })
        }
    }
    
    /**
     * Seed sample events ke backend
     */
    fun seedEvents(context: Context, callback: (String) -> Unit) {
        val apiService = ApiConfig.getApiService(context)
        val events = getSampleEvents()
        
        var successCount = 0
        var failCount = 0
        var completedCount = 0
        
        if (events.isEmpty()) {
            callback("Tidak ada event untuk ditambahkan")
            return
        }
        
        events.forEach { event ->
            apiService.createEvent(event).enqueue(object : Callback<EventResponse> {
                override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                    completedCount++
                    if (response.isSuccessful) {
                        successCount++
                        Log.d(TAG, "Event created: ${event.title}")
                    } else {
                        failCount++
                        Log.e(TAG, "Failed to create event: ${event.title}, code: ${response.code()}")
                    }
                    
                    if (completedCount == events.size) {
                        callback("Berhasil: $successCount, Gagal: $failCount dari ${events.size} event")
                    }
                }
                
                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    completedCount++
                    failCount++
                    Log.e(TAG, "Error creating event: ${event.title}", t)
                    
                    if (completedCount == events.size) {
                        callback("Berhasil: $successCount, Gagal: $failCount dari ${events.size} event")
                    }
                }
            })
        }
    }
    
    /**
     * Data sample untuk buku (15 buku dari berbagai kategori)
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
                stock = 3,
                totalCopies = 3,
                description = "Novel tentang perjuangan 10 anak dari keluarga miskin untuk bersekolah di SD Muhammadiyah di Belitung."
            ),
            CreateBookRequest(
                title = "Bumi Manusia",
                author = "Pramoedya Ananta Toer",
                category = "Sejarah",
                publisher = "Hasta Mitra",
                year = 1980,
                isbn = "9789799731234",
                stock = 2,
                totalCopies = 2,
                description = "Novel pertama dari Tetralogi Buru yang mengisahkan kehidupan Minke, seorang pemuda pribumi di masa kolonial Belanda."
            ),
            CreateBookRequest(
                title = "Sapiens: A Brief History of Humankind",
                author = "Yuval Noah Harari",
                category = "Non-Fiksi",
                publisher = "Harper",
                year = 2011,
                isbn = "9780062316097",
                stock = 4,
                totalCopies = 4,
                description = "Sejarah singkat umat manusia dari masa berburu hingga era modern."
            ),
            CreateBookRequest(
                title = "Harry Potter and the Sorcerer's Stone",
                author = "J.K. Rowling",
                category = "Fantasy",
                publisher = "Bloomsbury",
                year = 1997,
                isbn = "9780747532699",
                stock = 5,
                totalCopies = 5,
                description = "Petualangan Harry Potter, seorang anak penyihir yang belajar di Hogwarts School of Witchcraft and Wizardry."
            ),
            CreateBookRequest(
                title = "To Kill a Mockingbird",
                author = "Harper Lee",
                category = "Klasik",
                publisher = "J.B. Lippincott & Co.",
                year = 1960,
                isbn = "9780061120084",
                stock = 2,
                totalCopies = 2,
                description = "Novel klasik tentang ketidakadilan rasial di Amerika Selatan tahun 1930-an."
            ),
            CreateBookRequest(
                title = "1984",
                author = "George Orwell",
                category = "Dystopia",
                publisher = "Secker & Warburg",
                year = 1949,
                isbn = "9780451524935",
                stock = 3,
                totalCopies = 3,
                description = "Novel dystopia tentang totalitarianisme dan pengawasan massal di masa depan."
            ),
            CreateBookRequest(
                title = "The Hobbit",
                author = "J.R.R. Tolkien",
                category = "Fantasy",
                publisher = "George Allen & Unwin",
                year = 1937,
                isbn = "9780547928227",
                stock = 3,
                totalCopies = 3,
                description = "Petualangan Bilbo Baggins mencari harta karun yang dijaga naga Smaug."
            ),
            CreateBookRequest(
                title = "Atomic Habits",
                author = "James Clear",
                category = "Self-Help",
                publisher = "Avery",
                year = 2018,
                isbn = "9780735211292",
                stock = 4,
                totalCopies = 4,
                description = "Panduan praktis untuk membangun kebiasaan baik dan menghilangkan kebiasaan buruk."
            ),
            CreateBookRequest(
                title = "Educated",
                author = "Tara Westover",
                category = "Memoir",
                publisher = "Random House",
                year = 2018,
                isbn = "9780399590504",
                stock = 2,
                totalCopies = 2,
                description = "Memoir tentang seorang wanita yang tumbuh di keluarga Mormon survivalis dan perjuangannya untuk mendapatkan pendidikan."
            ),
            CreateBookRequest(
                title = "The Da Vinci Code",
                author = "Dan Brown",
                category = "Mystery",
                publisher = "Doubleday",
                year = 2003,
                isbn = "9780385504201",
                stock = 3,
                totalCopies = 3,
                description = "Thriller misteri tentang simbolog Robert Langdon yang mengejar petunjuk rahasia dalam karya seni Leonardo da Vinci."
            ),
            CreateBookRequest(
                title = "Negeri 5 Menara",
                author = "Ahmad Fuadi",
                category = "Fiksi",
                publisher = "Gramedia Pustaka Utama",
                year = 2009,
                isbn = "9789792221701",
                stock = 3,
                totalCopies = 3,
                description = "Kisah inspiratif santri di Pondok Madani Ponorogo yang bermimpi melanjutkan kuliah ke luar negeri."
            ),
            CreateBookRequest(
                title = "Ronggeng Dukuh Paruk",
                author = "Ahmad Tohari",
                category = "Fiksi",
                publisher = "Gramedia Pustaka Utama",
                year = 1982,
                isbn = "9789792202373",
                stock = 2,
                totalCopies = 2,
                description = "Novel tentang Srintil, seorang ronggeng di desa Dukuh Paruk yang kehidupannya penuh tragedi."
            ),
            CreateBookRequest(
                title = "Cantik Itu Luka",
                author = "Eka Kurniawan",
                category = "Fiksi",
                publisher = "Jendela",
                year = 2002,
                isbn = "9789799101198",
                stock = 2,
                totalCopies = 2,
                description = "Kisah epik keluarga yang menyentuh sejarah Indonesia melalui tokoh Dewi Ayu dan keluarganya."
            ),
            CreateBookRequest(
                title = "Perahu Kertas",
                author = "Dee Lestari",
                category = "Romance",
                publisher = "Bentang Pustaka",
                year = 2009,
                isbn = "9789793062976",
                stock = 4,
                totalCopies = 4,
                description = "Kisah cinta Kugy dan Keenan yang terhubung melalui mimpi dan seni."
            ),
            CreateBookRequest(
                title = "5 cm",
                author = "Donny Dhirgantoro",
                category = "Petualangan",
                publisher = "Gramedia Pustaka Utama",
                year = 2005,
                isbn = "9789792214444",
                stock = 3,
                totalCopies = 3,
                description = "Petualangan lima sahabat mendaki Gunung Semeru sambil merenungkan makna persahabatan."
            )
        )
    }
    
    /**
     * Data sample untuk users (5 users dengan data RT/RW)
     */
    private fun getSampleUsers(): List<RegisterRequest> {
        return listOf(
            RegisterRequest(
                fullName = "Budi Santoso",
                email = "budi.santoso@test.com",
                password = "password123",
                nik = "3275010101850001",
                addressRtRw = "RT 001/RW 005",
                addressKelurahan = "Cibubur",
                addressKecamatan = "Ciracas",
                phoneNumber = "081234567801",
                isChild = false,
                parentName = null
            ),
            RegisterRequest(
                fullName = "Siti Aminah",
                email = "siti.aminah@test.com",
                password = "password123",
                nik = "3275010202900002",
                addressRtRw = "RT 002/RW 005",
                addressKelurahan = "Cibubur",
                addressKecamatan = "Ciracas",
                phoneNumber = "081234567802",
                isChild = false,
                parentName = null
            ),
            RegisterRequest(
                fullName = "Ahmad Hidayat",
                email = "ahmad.hidayat@test.com",
                password = "password123",
                nik = "3275010303950003",
                addressRtRw = "RT 003/RW 005",
                addressKelurahan = "Cibubur",
                addressKecamatan = "Ciracas",
                phoneNumber = "081234567803",
                isChild = false,
                parentName = null
            ),
            RegisterRequest(
                fullName = "Dewi Lestari",
                email = "dewi.lestari@test.com",
                password = "password123",
                nik = "3275010404880004",
                addressRtRw = "RT 001/RW 006",
                addressKelurahan = "Cibubur",
                addressKecamatan = "Ciracas",
                phoneNumber = "081234567804",
                isChild = false,
                parentName = null
            ),
            RegisterRequest(
                fullName = "Andi Wijaya",
                email = "andi.wijaya@test.com",
                password = "password123",
                nik = "3275010505100005",
                addressRtRw = "RT 001/RW 006",
                addressKelurahan = "Cibubur",
                addressKecamatan = "Ciracas",
                phoneNumber = "081234567805",
                isChild = true,
                parentName = "Dewi Lestari"
            )
        )
    }
    
    /**
     * Data sample untuk events (5 events)
     */
    private fun getSampleEvents(): List<CreateEventRequest> {
        return listOf(
            CreateEventRequest(
                title = "Donasi Buku Bulan Ini",
                message = "Taman Bacaan membuka donasi buku untuk bulan ini. Silakan datang ke lokasi atau hubungi kami untuk penjemputan buku.",
                createdBy = "Admin TBM"
            ),
            CreateEventRequest(
                title = "Lomba Menulis Cerpen",
                message = "Ikuti lomba menulis cerpen tema 'Persahabatan' untuk anak-anak usia 10-15 tahun. Hadiah menarik menanti!",
                createdBy = "Admin TBM"
            ),
            CreateEventRequest(
                title = "Baca Bersama Setiap Sabtu",
                message = "Kegiatan baca bersama diadakan setiap hari Sabtu pukul 09.00-11.00 WIB. Mari bergabung dan nikmati suasana membaca yang menyenangkan!",
                createdBy = "Admin TBM"
            ),
            CreateEventRequest(
                title = "Perpanjangan Batas Waktu Pengembalian",
                message = "Informasi: Batas waktu pengembalian buku diperpanjang menjadi 14 hari. Berlaku mulai hari ini.",
                createdBy = "Admin TBM"
            ),
            CreateEventRequest(
                title = "Pengadaan Buku Baru",
                message = "100 buku baru telah tiba! Tersedia berbagai kategori: fiksi, non-fiksi, anak-anak, dan komik. Yuk datang dan pinjam!",
                createdBy = "Admin TBM"
            )
        )
    }
}
