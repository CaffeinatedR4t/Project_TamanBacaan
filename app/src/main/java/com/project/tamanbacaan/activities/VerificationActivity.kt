package com.caffeinatedr4t.tamanbacaan.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caffeinatedr4t.tamanbacaan.R

class VerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification) // MEMUAT LAYOUT XML BARU

        val btnAccept: Button = findViewById(R.id.btnVerifyAccept)
        val btnReject: Button = findViewById(R.id.btnVerifyReject)
        // TextViews lainnya (tvVerificationNik, tvVerificationMemberName, tvVerificationAddress)

        // Logika Aksi Pengelola
        btnAccept.setOnClickListener {
            // Logika panggil API Admin untuk menyetujui anggota
            Toast.makeText(this, "Anggota berhasil Diverifikasi dan Disetujui!", Toast.LENGTH_LONG).show()
            finish()
        }

        btnReject.setOnClickListener {
            // Logika panggil API Admin untuk menolak anggota
            Toast.makeText(this, "Pendaftaran Anggota Ditolak.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}