package com.caffeinatedr4t.tamanbacaan.models

/**
 * Data class yang merepresentasikan model untuk seorang pengguna (User).
 * Objek dari kelas ini menyimpan semua informasi yang terkait dengan satu akun pengguna,
 * baik itu anggota biasa maupun admin.
 */
data class User(
    // ID unik untuk setiap pengguna, biasanya dari database.
    val id: String,

    // Nama lengkap pengguna.
    val fullName: String,

    // Alamat email pengguna, digunakan juga untuk login.
    val email: String,

    // Nomor Induk Kependudukan (NIK) pengguna, harus 16 digit.
    val nik: String,

    // Alamat lengkap pengguna, termasuk RT/RW.
    val addressRtRw: String,

    // Peran pengguna dalam sistem, default-nya adalah "member" (anggota). Bisa juga "admin".
    val role: String = "member",

    // Status keanggotaan pengguna, misalnya "Active", "Inactive", "Pending Verification".
    val status: String = "Active",

    // Status boolean, true jika anggota adalah seorang anak-anak.
    val isChild: Boolean = false,

    // Nama orang tua atau wali, nullable (bisa kosong) jika anggota bukan anak-anak.
    val parentName: String? = null,

    // BARU: Status Verifikasi RT/RW.
    // Status boolean, true jika pendaftaran pengguna telah diverifikasi oleh pengelola TBM.
    val isVerified: Boolean = false
)
