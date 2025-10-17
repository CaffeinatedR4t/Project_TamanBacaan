package com.caffeinatedr4t.tamanbacaan.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caffeinatedr4t.tamanbacaan.R

/**
 * Activity yang digunakan oleh Admin (Pengelola) untuk melakukan verifikasi
 * pendaftaran anggota baru. Halaman ini akan menampilkan detail calon anggota
 * dan memberikan opsi untuk 'Terima' atau 'Tolak' pendaftaran.
 */
class VerificationActivity : AppCompatActivity() {

    /**
     * Fungsi yang dipanggil saat Activity pertama kali dibuat.
     * Bertanggung jawab untuk inisialisasi UI dan mengatur listener untuk tombol aksi.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menetapkan layout XML untuk activity ini.
        setContentView(R.layout.activity_verification)

        // --- Inisialisasi Komponen UI ---
        // Di sini, Anda akan mengambil data pendaftar (misalnya, dari Intent yang dikirim oleh AdminActivity)
        // dan menampilkannya di TextViews.
        // Contoh:
        // val nik = intent.getStringExtra("USER_NIK")
        // val name = intent.getStringExtra("USER_NAME")
        // findViewById<TextView>(R.id.tvVerificationNik).text = nik
        // findViewById<TextView>(R.id.tvVerificationMemberName).text = name
        // ... dan seterusnya untuk data lain seperti alamat.

        // Menemukan tombol 'Terima' dan 'Tolak' dari layout.
        val btnAccept: Button = findViewById(R.id.btnVerifyAccept)
        val btnReject: Button = findViewById(R.id.btnVerifyReject)
        // Anda juga perlu menginisialisasi TextViews lainnya di sini jika ingin memanipulasinya.
        // val tvNik: TextView = findViewById(R.id.tvVerificationNik)
        // val tvName: TextView = findViewById(R.id.tvVerificationMemberName)
        // val tvAddress: TextView = findViewById(R.id.tvVerificationAddress)

        // --- Logika Aksi Pengelola ---

        // Mengatur aksi yang akan dijalankan saat tombol 'Terima' ditekan.
        btnAccept.setOnClickListener {
            // Di aplikasi nyata, di sinilah Anda akan memanggil API untuk mengubah status
            // keanggotaan pengguna menjadi 'disetujui' di database.
            // Setelah berhasil, tampilkan pesan konfirmasi.
            Toast.makeText(this, "Anggota berhasil Diverifikasi dan Disetujui!", Toast.LENGTH_LONG).show()

            // Menutup activity ini untuk kembali ke daftar verifikasi (AdminActivity).
            finish()
        }

        // Mengatur aksi yang akan dijalankan saat tombol 'Tolak' ditekan.
        btnReject.setOnClickListener {
            // Di aplikasi nyata, di sinilah Anda akan memanggil API untuk menolak
            // pendaftaran pengguna atau menghapusnya dari daftar verifikasi.
            // Setelah berhasil, tampilkan pesan konfirmasi.
            Toast.makeText(this, "Pendaftaran Anggota Ditolak.", Toast.LENGTH_LONG).show()

            // Menutup activity ini untuk kembali ke daftar verifikasi (AdminActivity).
            finish()
        }
    }
}
