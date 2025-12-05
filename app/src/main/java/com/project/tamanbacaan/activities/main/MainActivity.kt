package com.caffeinatedr4t.tamanbacaan.activities.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.fragments.BookmarkFragment
import com.caffeinatedr4t.tamanbacaan.fragments.HomeFragment
import com.caffeinatedr4t.tamanbacaan.fragments.NotificationFragment
import com.caffeinatedr4t.tamanbacaan.fragments.ProfileFragment
import com.caffeinatedr4t.tamanbacaan.fragments.SearchFragment
import com.caffeinatedr4t.tamanbacaan.utils.NotificationHelper
import com.caffeinatedr4t.tamanbacaan.utils.SharedPreferencesHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.caffeinatedr4t.tamanbacaan.activities.auth.LoginActivity

/**
 * Activity utama aplikasi yang menjadi host untuk semua fragment utama (Home, Search, Bookmark, Profile).
 * Activity ini juga mengatur navigasi atas dan bawah.
 */
class MainActivity : AppCompatActivity() {

    // SharedPreferences helper untuk mengakses data pengguna yang login
    private lateinit var sharedPrefs: SharedPreferencesHelper

    // Variabel untuk menyimpan informasi pengguna yang login
    private var userName: String? = null
    private var userEmail: String? = null
    private var userNik: String? = null
    private var userRole: String? = null

    /**
     * Fungsi yang dipanggil saat Activity pertama kali dibuat.
     * Bertanggung jawab untuk inisialisasi layout, data, dan navigasi.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R. layout.activity_main)

        // Inisialisasi SharedPreferences
        sharedPrefs = SharedPreferencesHelper(this)

        // Cek apakah user sudah login
        if (!sharedPrefs.isLoggedIn()) {
            // Jika belum login, redirect ke LoginActivity
            navigateToLogin()
            return
        }

        // Membuat channel notifikasi yang diperlukan untuk Android Oreo (API 26) ke atas
        NotificationHelper.createNotificationChannel(this)

        // Mengambil data pengguna dari SharedPreferences
        loadUserData()

        // Memuat HomeFragment sebagai tampilan awal saat aplikasi pertama kali dibuka
        // `savedInstanceState == null` memastikan fragment tidak dimuat ulang saat activity dibuat kembali
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Mengatur listener dan aksi untuk BottomNavigationView
        setupBottomNavigation()

        // Mengatur listener dan aksi untuk ikon di toolbar atas (Profile & Notification)
        setupTopNavigation()
    }

    /**
     * Memuat data pengguna dari SharedPreferences
     */
    private fun loadUserData() {
        userName = sharedPrefs.getUserName()
        userEmail = sharedPrefs.getUserEmail()
        userNik = sharedPrefs.getUserId() // getUserId bisa digunakan sebagai NIK
        userRole = sharedPrefs.getUserRole()

        // Log untuk debugging (opsional, bisa dihapus di production)
        Log.d("MainActivity", "User: $userName, Email: $userEmail, Role: $userRole")
    }

    /**
     * Mengatur logika untuk Bottom Navigation View.
     * Mengganti fragment yang ditampilkan berdasarkan item menu yang dipilih.
     */
    private fun setupBottomNavigation() {
        // Menemukan komponen BottomNavigationView dari layout
        val bottomNav = findViewById<BottomNavigationView>(R. id.bottomNavigation)

        // Menetapkan listener untuk item yang dipilih
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnHome -> {
                    loadFragment(HomeFragment())
                    true // Mengembalikan true menandakan event telah ditangani
                }
                R.id.btnSearch -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.btnBookmark -> {
                    loadFragment(BookmarkFragment())
                    true
                }
                else -> false // Jika item tidak dikenali, event tidak ditangani
            }
        }
    }

    /**
     * Mengatur logika untuk tombol navigasi di bagian atas (top bar).
     * Termasuk tombol Profile dan Notification.
     */
    private fun setupTopNavigation() {
        // Menemukan tombol Profile dan mengatur aksi klik
        val btnProfile = findViewById<ImageView>(R.id.btnProfile)
        btnProfile.setOnClickListener {
            // Membuat instance dari ProfileFragment
            val fragment = ProfileFragment()

            // Membuat Bundle untuk mengirim data pengguna ke ProfileFragment
            val bundle = Bundle().apply {
                putString("USER_NAME", userName)
                putString("USER_EMAIL", userEmail)
                putString("USER_NIK", userNik)
                putString("USER_ROLE", userRole)
            }
            // Menetapkan bundle sebagai arguments untuk fragment
            fragment.arguments = bundle

            // Memuat ProfileFragment ke dalam container
            loadFragment(fragment)
        }

        // Menemukan tombol Notifikasi dan mengatur aksi klik
        val btnNotification: ImageView = findViewById(R.id.btnNotification)
        btnNotification.setOnClickListener {
            // Memuat NotificationFragment saat tombol ditekan
            loadFragment(NotificationFragment())
        }
    }

    /**
     * Fungsi publik yang dapat dipanggil dari fragment (misal: ProfileFragment) untuk memulai proses logout.
     */
    fun showLogoutConfirmation() {
        // Hapus data login dari SharedPreferences
        sharedPrefs.clearLoginData()

        Toast.makeText(this, "Logout berhasil!", Toast.LENGTH_SHORT).show()

        // Navigasi ke LoginActivity
        navigateToLogin()
    }

    /**
     * Navigasi ke LoginActivity dan clear activity stack
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        // Flags untuk membersihkan semua activity di atasnya dan membuat task baru
        // Ini mencegah pengguna kembali ke MainActivity dengan menekan tombol "back"
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Menutup MainActivity secara permanen
    }

    /**
     * Fungsi utilitas untuk memuat atau mengganti fragment di dalam container utama.
     * @param fragment Fragment yang akan ditampilkan.
     */
    private fun loadFragment(fragment: Fragment) {
        // Memulai transaksi fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        // Mengganti fragment yang ada di container `nav_host_fragment` dengan fragment yang baru
        transaction.replace(R.id.nav_host_fragment, fragment)
        // Menyelesaikan transaksi
        transaction.commit()
    }

    /**
     * Fungsi publik untuk mendapatkan data user (bisa dipanggil dari fragment)
     */
    fun getUserName(): String? = userName
    fun getUserEmail(): String?  = userEmail
    fun getUserRole(): String? = userRole
}