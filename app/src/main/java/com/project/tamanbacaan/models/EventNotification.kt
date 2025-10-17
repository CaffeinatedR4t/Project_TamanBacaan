package com.caffeinatedr4t.tamanbacaan.models

/**
 * Data class yang merepresentasikan model untuk sebuah notifikasi acara (event).
 * Objek dari kelas ini menyimpan semua informasi yang terkait dengan satu notifikasi,
 * seperti judul acara, pesan, dan tanggalnya.
 */
data class EventNotification(
    // ID unik untuk setiap notifikasi, biasanya dari database atau sumber data.
    val id: String,

    // Judul notifikasi atau nama acara. Contoh: "Workshop Menulis Cerpen".
    val title: String,

    // Isi pesan atau deskripsi singkat dari notifikasi/acara.
    val message: String,

    // Tanggal kapan notifikasi atau acara ini relevan atau dikirim.
    val date: String
)
