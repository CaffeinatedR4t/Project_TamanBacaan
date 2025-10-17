package com.caffeinatedr4t.tamanbacaan.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.fragments.NotificationFragment
import com.caffeinatedr4t.tamanbacaan.fragments.admin.AdminProfileFragment
import com.caffeinatedr4t.tamanbacaan.fragments.admin.BookManagementFragment
import com.caffeinatedr4t.tamanbacaan.fragments.admin.ReportFragment
import com.caffeinatedr4t.tamanbacaan.fragments.admin.TransactionManagementFragment
import com.caffeinatedr4t.tamanbacaan.fragments.admin.MemberManagementFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.caffeinatedr4t.tamanbacaan.fragments.admin.EventManagementFragment

/**
 * Activity yang berfungsi sebagai dasbor utama untuk Admin (Pengelola).
 * Activity ini mengatur semua navigasi utama admin dan menjadi host untuk
 * semua fragment manajemen seperti manajemen buku, anggota, transaksi, dll.
 */
class AdminActivity : AppCompatActivity() {

    /**
     * Fungsi yang dipanggil saat Activity pertama kali dibuat.
     * Bertanggung jawab untuk inisialisasi layout dan mengatur navigasi.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Memuat fragment awal saat activity pertama kali dijalankan.
        // `savedInstanceState == null` memastikan fragment tidak dimuat ulang saat konfigurasi berubah (misal: rotasi layar).
        if (savedInstanceState == null) {
            // BookManagementFragment dipilih sebagai tampilan default untuk admin.
            loadFragment(BookManagementFragment())
        }
        // Memanggil fungsi untuk mengatur logika navigasi atas (top bar).
        setupTopNavigation()
        // Memanggil fungsi untuk mengatur logika navigasi bawah (bottom navigation).
        setupBottomNavigation()
    }

    /**
     * Mengatur logika untuk tombol navigasi di bagian atas (top bar),
     * yaitu tombol profil admin dan tombol notifikasi.
     */
    private fun setupTopNavigation() {
        // Inisialisasi komponen ImageView dari layout.
        val btnProfile = findViewById<ImageView>(R.id.btnAdminProfile)
        val btnNotification = findViewById<ImageView>(R.id.btnNotification)

        // Menetapkan aksi saat tombol profil admin di TopBar ditekan.
        btnProfile.setOnClickListener {
            loadFragment(AdminProfileFragment()) // Memuat fragment profil admin.
        }

        // Menetapkan aksi saat tombol notifikasi di TopBar ditekan.
        btnNotification.setOnClickListener {
            loadFragment(NotificationFragment()) // Memuat fragment notifikasi umum.
        }
    }

    /**
     * Mengatur logika untuk Bottom Navigation View khusus admin.
     * Mengganti fragment yang ditampilkan berdasarkan item menu yang dipilih.
     */
    private fun setupBottomNavigation() {
        // Inisialisasi komponen BottomNavigationView dari layout.
        val bottomNav = findViewById<BottomNavigationView>(R.id.adminBottomNav)

        // Menetapkan listener untuk setiap item yang dipilih di bottom navigation.
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // Ketika item menu 'Buku' dipilih.
                R.id.nav_admin_books -> {
                    loadFragment(BookManagementFragment())
                    true // Mengembalikan true menandakan event telah ditangani.
                }
                // Ketika item menu 'Anggota' dipilih (untuk verifikasi dan CRUD anggota).
                R.id.nav_admin_members -> {
                    loadFragment(MemberManagementFragment())
                    true
                }
                // Ketika item menu 'Acara' dipilih.
                R.id.nav_admin_events -> {
                    loadFragment(EventManagementFragment())
                    true
                }
                // Ketika item menu 'Permintaan' dipilih (untuk manajemen transaksi peminjaman).
                R.id.nav_admin_requests -> {
                    loadFragment(TransactionManagementFragment())
                    true
                }
                // Ketika item menu 'Laporan' dipilih.
                R.id.nav_admin_reports -> {
                    loadFragment(ReportFragment())
                    true
                }
                // Jika item tidak dikenali, event tidak ditangani.
                else -> false
            }
        }
    }

    /**
     * Fungsi utilitas untuk memuat atau mengganti fragment di dalam container utama admin.
     * @param fragment Fragment yang akan ditampilkan.
     */
    private fun loadFragment(fragment: Fragment) {
        // Memulai transaksi fragment manager.
        supportFragmentManager.beginTransaction()
            // Mengganti fragment yang ada di container `admin_nav_host_fragment` dengan fragment yang baru.
            .replace(R.id.admin_nav_host_fragment, fragment)
            // Menyelesaikan transaksi untuk menampilkan fragment.
            .commit()
    }
}
