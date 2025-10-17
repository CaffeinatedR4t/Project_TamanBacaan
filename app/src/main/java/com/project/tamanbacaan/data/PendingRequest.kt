package com.caffeinatedr4t.tamanbacaan.data

import com.caffeinatedr4t.tamanbacaan.models.Book

/**
 * Data class yang merepresentasikan model untuk sebuah permintaan peminjaman buku yang tertunda (pending).
 * Objek dari kelas ini digunakan di sisi Admin (Pengelola) untuk menampilkan daftar
 * permintaan peminjaman yang perlu ditinjau dan disetujui atau ditolak.
 */
data class PendingRequest(
    // ID unik untuk setiap permintaan peminjaman.
    val requestId: String,

    // Objek 'Book' yang lengkap, berisi semua detail buku yang ingin dipinjam.
    val book: Book,

    // Nama anggota yang mengajukan permintaan peminjaman.
    val memberName: String,

    // ID unik dari anggota yang mengajukan permintaan.
    val memberId: String,

    // Tanggal kapan permintaan peminjaman ini dibuat oleh anggota.
    val requestDate: String
)
