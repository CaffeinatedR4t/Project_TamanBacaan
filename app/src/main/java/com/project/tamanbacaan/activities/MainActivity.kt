package com.caffeinatedr4t.tamanbacaan.activities

import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    private var userName: String? = null
    private var userEmail: String? = null
    private var userNik: String? = null
    private var userAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationHelper.createNotificationChannel(this)

        //  Ambil data user dari Intent LoginActivity
        userName = intent.getStringExtra("USER_NAME")
        userEmail = intent.getStringExtra("USER_EMAIL")
        userNik = intent.getStringExtra("USER_NIK")
        userAddress = intent.getStringExtra("USER_ADDRESS")

        // Tampilkan fragment Home pertama kali
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Setup navigasi bawah BARU
        setupBottomNavigation()

        // Setup tombol profil di top bar
        setupTopNavigation()
    }

    private fun setupBottomNavigation() {
        // Mengganti logic manual dengan BottomNavigationView
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
            findViewById<BottomNavigationView>(R.id.bottomNavigation).menu.setGroupCheckable(0, false, true)
        }

        // Tombol notifikasi
        val btnNotification: ImageView = findViewById(R.id.btnNotification)
        btnNotification.setOnClickListener {
            // Muat Fragment Notifikasi
            loadFragment(NotificationFragment())
            // Hilangkan highlight BottomNav saat membuka Notifikasi
            findViewById<BottomNavigationView>(R.id.bottomNavigation).menu.setGroupCheckable(0, false, true)
        }
    }

    fun showLogoutConfirmation() {
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