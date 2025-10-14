package com.caffeinatedr4t.tamanbacaan.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AlertDialog
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.fragments.BookmarkFragment
import com.caffeinatedr4t.tamanbacaan.fragments.HomeFragment
import com.caffeinatedr4t.tamanbacaan.fragments.NotificationFragment
import com.caffeinatedr4t.tamanbacaan.fragments.ProfileFragment
import com.caffeinatedr4t.tamanbacaan.fragments.SearchFragment
import com.caffeinatedr4t.tamanbacaan.utils.NotificationHelper
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    private var currentTabIndex = 0
    private var userName: String? = null
    private var userEmail: String? = null
    private var userNik: String? = null
    private var userAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationHelper.createNotificationChannel(this)

        // 🔹 Ambil data user dari Intent LoginActivity
        userName = intent.getStringExtra("USER_NAME")
        userEmail = intent.getStringExtra("USER_EMAIL")
        userNik = intent.getStringExtra("USER_NIK")
        userAddress = intent.getStringExtra("USER_ADDRESS")

        // 🔹 Tampilkan fragment Home pertama kali
        loadFragment(HomeFragment())
        updateTabSelection(0)

        // 🔹 Setup navigasi bawah
        setupBottomNavigation()

        // 🔹 Setup tombol profil di top bar
        setupTopNavigation()
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.btnHome).setOnClickListener {
            if (currentTabIndex != 0) {
                loadFragment(HomeFragment())
                updateTabSelection(0)
            }
        }

        findViewById<View>(R.id.btnSearch).setOnClickListener {
            if (currentTabIndex != 1) {
                loadFragment(SearchFragment())
                updateTabSelection(1)
            }
        }

        findViewById<View>(R.id.btnBookmark).setOnClickListener {
            if (currentTabIndex != 2) {
                loadFragment(BookmarkFragment())
                updateTabSelection(2)
            }
        }
    }

    private fun setupTopNavigation() {
        // 🔹 Tombol profil
        val btnProfile = findViewById<ImageView>(R.id.btnProfile)
        btnProfile.setOnClickListener {
            val fragment = ProfileFragment()

            // 🔹 Kirim data user ke ProfileFragment
            val bundle = Bundle().apply {
                putString("USER_NAME", userName)
                putString("USER_EMAIL", userEmail)
                putString("USER_NIK", userNik)
                putString("USER_ADDRESS", userAddress)
            }
            fragment.arguments = bundle

            loadFragment(fragment)
            updateTabSelection(-1) // Tidak ada tab bawah yang aktif
        }

        // 🔹 Tombol notifikasi
        val btnNotification: ImageView = findViewById(R.id.btnNotification)
        btnNotification.setOnClickListener {
            loadFragment(NotificationFragment())
            updateTabSelection(-1) // Tidak ada tab bawah yang aktif
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

    private fun updateTabSelection(selectedIndex: Int) {
        setTabInactive(R.id.btnHome, R.id.homeTabIcon, R.id.homeTabText)
        setTabInactive(R.id.btnSearch, R.id.searchTabIcon, R.id.searchTabText)
        setTabInactive(R.id.btnBookmark, R.id.bookmarkTabIcon, R.id.bookmarkTabText)

        when (selectedIndex) {
            0 -> setTabActive(R.id.btnHome, R.id.homeTabIcon, R.id.homeTabText)
            1 -> setTabActive(R.id.btnSearch, R.id.searchTabIcon, R.id.searchTabText)
            2 -> setTabActive(R.id.btnBookmark, R.id.bookmarkTabIcon, R.id.bookmarkTabText)
            // -1 = profile, jadi tidak ubah tab bawah
        }

        currentTabIndex = selectedIndex
    }

    private fun setTabActive(tabId: Int, @IdRes iconId: Int, @IdRes textId: Int) {
        val tab = findViewById<View>(tabId)
        val icon = tab.findViewById<ImageView>(iconId)
        val text = tab.findViewById<TextView>(textId)

        icon?.setColorFilter(ContextCompat.getColor(this, R.color.primary_blue))
        text?.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
    }

    private fun setTabInactive(tabId: Int, @IdRes iconId: Int, @IdRes textId: Int) {
        val tab = findViewById<View>(tabId)
        val icon = tab.findViewById<ImageView>(iconId)
        val text = tab.findViewById<TextView>(textId)

        icon?.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary))
        text?.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
    }
}
