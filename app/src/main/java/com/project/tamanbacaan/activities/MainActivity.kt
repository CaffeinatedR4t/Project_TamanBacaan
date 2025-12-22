package com.caffeinatedr4t.tamanbacaan.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.fragments.BookmarkFragment
import com.caffeinatedr4t.tamanbacaan.fragments.HomeFragment
import com.caffeinatedr4t.tamanbacaan.fragments.NotificationFragment
import com.caffeinatedr4t.tamanbacaan.fragments.ProfileFragment
import com.caffeinatedr4t.tamanbacaan.fragments.SearchFragment
import com.caffeinatedr4t.tamanbacaan.utils.NotificationHelper
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

/**
 * Activity utama aplikasi yang menjadi host untuk semua fragment utama (Home, Search, Bookmark, Profile).
 * Activity ini juga mengatur navigasi atas dan bawah.
 */
class MainActivity : AppCompatActivity() {

    // Variabel untuk menyimpan informasi pengguna yang login.
    private var userName: String? = null
    private var userEmail: String? = null
    private var userNik: String? = null
    private var userAddress: String? = null

    override fun onResume() {
        super.onResume()
        checkAccountStatus()
    }

    private fun checkAccountStatus() {
        val sharedPrefsManager = SharedPrefsManager(this)

        val userId = sharedPrefsManager.getUser()?.id

        if (!userId.isNullOrEmpty()) {
            lifecycleScope.launch {
                // Panggil repository untuk cek status terbaru
                val isValid = BookRepository.checkUserStatus(userId)

                if (!isValid) {
                    // Jika tidak valid (Unverified/Deleted), Force Logout
                    Toast.makeText(
                        this@MainActivity,
                        "Sesi berakhir atau akun belum diverifikasi. Silakan login kembali.",
                        Toast.LENGTH_LONG
                    ).show()

                    showLogoutConfirmation() // Panggil fungsi logout yang sudah ada
                }
            }
        }
    }

    /**
     * Fungsi yang dipanggil saat Activity pertama kali dibuat.
     * Bertanggung jawab untuk inisialisasi layout, data, dan navigasi.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // [BARU] Inisialisasi Repository saat MainActivity dibuat
        // Ini mencegah "User ID not found" jika aplikasi di-restart atau memori dibersihkan
        val sharedPrefsManager = SharedPrefsManager(this)
        val currentUser = sharedPrefsManager.getUser()
        if (currentUser != null && !currentUser.id.isNullOrEmpty()) {
            BookRepository.setUserId(currentUser.id)
        }
        // ---------------------------------------------------------

        // Membuat channel notifikasi yang diperlukan untuk Android Oreo (API 26) ke atas.
        NotificationHelper.createNotificationChannel(this)

        // Mengambil data pengguna yang dikirim dari LoginActivity melalui Intent.
        userName = intent.getStringExtra("USER_NAME")
        userEmail = intent.getStringExtra("USER_EMAIL")
        userNik = intent.getStringExtra("USER_NIK")
        userAddress = intent.getStringExtra("USER_ADDRESS")

        // Memuat HomeFragment sebagai tampilan awal saat aplikasi pertama kali dibuka.
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Mengatur listener dan aksi untuk BottomNavigationView.
        setupBottomNavigation()

        // Mengatur listener dan aksi untuk ikon di toolbar atas (Profile & Notification).
        setupTopNavigation()
    }

    /**
     * Mengatur logika untuk Bottom Navigation View.
     */
    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnHome -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.btnSearch -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.btnBookmark -> {
                    loadFragment(BookmarkFragment())
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Mengatur logika untuk tombol navigasi di bagian atas (top bar).
     */
    private fun setupTopNavigation() {
        val btnProfile = findViewById<ImageView>(R.id.btnProfile)
        btnProfile.setOnClickListener {
            val fragment = ProfileFragment()
            val bundle = Bundle().apply {
                putString("USER_NAME", userName)
                putString("USER_EMAIL", userEmail)
                putString("USER_NIK", userNik)
                putString("USER_ADDRESS", userAddress)
            }
            fragment.arguments = bundle
            loadFragment(fragment)
        }

        val btnNotification: ImageView = findViewById(R.id.btnNotification)
        btnNotification.setOnClickListener {
            loadFragment(NotificationFragment())
        }
    }

    /**
     * Fungsi publik untuk logout.
     */
    fun showLogoutConfirmation() {
        val sharedPrefsManager = SharedPrefsManager(this)
        sharedPrefsManager.clearSession()
        Toast.makeText(this, "Logout berhasil!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
    }
}