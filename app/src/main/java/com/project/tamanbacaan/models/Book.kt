package com.caffeinatedr4t.tamanbacaan.models

/**
 * Data class yang merepresentasikan model untuk sebuah buku.
 * Objek dari kelas ini menyimpan semua informasi yang terkait dengan satu buku,
 * seperti judul, penulis, status ketersediaan, rating, dan lainnya.
 */
data class Book(
    // ID unik untuk setiap buku, biasanya dari database.
    val id: String,

    // Judul lengkap dari buku.
    val title: String,

    // Nama penulis atau pengarang buku.
    val author: String,

    // Deskripsi singkat atau blurb buku.
    val description: String,

    // URL atau path ke gambar sampul buku. Digunakan oleh Glide/Picasso.
    val coverUrl: String,

    // Kategori atau genre buku (misalnya: Fiksi, Non-Fiksi, Sejarah).
    val category: String,

    // Status boolean, true jika buku ini ditandai (bookmark) oleh pengguna.
    var isBookmarked: Boolean = false,

    // Status ketersediaan buku di perpustakaan. true jika tersedia untuk dipinjam.
    var isAvailable: Boolean = true,

    // Status boolean, true jika buku ini sedang dipinjam oleh pengguna saat ini.
    var isBorrowed: Boolean = false,

    // Nomor ISBN (International Standard Book Number) dari buku.
    val isbn: String = "",

    // Tahun buku ini diterbitkan.
    val publicationYear: Int = 0,

    // Tanggal kapan buku ini dipinjam (jika sedang dipinjam). Dibuat nullable.
    val borrowedDate: String? = null,

    // Tanggal jatuh tempo pengembalian buku (jika sedang dipinjam). Dibuat nullable.
    val dueDate: String? = null,

    // --- BARU UNTUK REQ. 4 & 8 ---
    // Rating rata-rata dari semua ulasan yang diberikan.
    val avgRating: Float = 0.0f,
    // Jumlah total ulasan yang telah diterima buku ini.
    val totalReviews: Int = 0,
    // Sinopsis atau ringkasan cerita yang lebih panjang dari deskripsi.
    val synopsis: String = description
    // ----------------------------
) {

    /**
     * Fungsi untuk mendapatkan teks status ketersediaan buku yang mudah dibaca.
     * @return String yang mendeskripsikan status buku ("Borrowed by you", "Not Available", "Recommended").
     */
    fun getAvailabilityStatus(): String {
        return when {
            isBorrowed -> "Borrowed by you"
            !isAvailable -> "Not Available"
            else -> "Recommended"
        }
    }

    /**
     * Fungsi untuk mendapatkan warna yang sesuai dengan status ketersediaan buku.
     * Berguna untuk mengubah warna teks atau background di UI.
     * @return Integer ID dari resource warna bawaan Android.
     */
    fun getStatusColor(): Int {
        return when {
            isBorrowed -> android.R.color.holo_orange_dark
            !isAvailable -> android.R.color.holo_red_dark
            else -> android.R.color.holo_green_dark
        }
    }
}
