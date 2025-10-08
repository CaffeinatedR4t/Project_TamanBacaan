package com.caffeinatedr4t.tamanbacaan.activities // KOREKSI PACKAGE

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.caffeinatedr4t.tamanbacaan.R // R class sudah benar
import com.caffeinatedr4t.tamanbacaan.fragments.BookmarkFragment
import com.caffeinatedr4t.tamanbacaan.fragments.HomeFragment
import com.caffeinatedr4t.tamanbacaan.fragments.SearchFragment
import com.caffeinatedr4t.tamanbacaan.utils.NotificationHelper

class MainActivity : AppCompatActivity() {

    private var currentTabIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- BARU: Inisialisasi Channel Notifikasi (Req. 9) ---
        NotificationHelper.createNotificationChannel(this)
        // Jika Anda ingin mengarahkan pengguna ke LoginActivity dulu:
        // startActivity(Intent(this, LoginActivity::class.java))
        // finish()
        // -----------------------------------------------------

        // Load home fragment by default
        loadFragment(HomeFragment())
        updateTabSelection(0)

        // Set up bottom navigation
        setupBottomNavigation()
    }
    // ... (setupBottomNavigation, loadFragment, updateTabSelection, setTabActive, setTabInactive tetap sama)
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

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
    }

    private fun updateTabSelection(selectedIndex: Int) {
        // Reset all tabs to inactive first
        setTabInactive(R.id.btnHome, R.id.homeTabIcon, R.id.homeTabText)
        setTabInactive(R.id.btnSearch, R.id.searchTabIcon, R.id.searchTabText)
        setTabInactive(R.id.btnBookmark, R.id.bookmarkTabIcon, R.id.bookmarkTabText)

        // Set selected tab to active
        when (selectedIndex) {
            0 -> setTabActive(R.id.btnHome, R.id.homeTabIcon, R.id.homeTabText)
            1 -> setTabActive(R.id.btnSearch, R.id.searchTabIcon, R.id.searchTabText)
            2 -> setTabActive(R.id.btnBookmark, R.id.bookmarkTabIcon, R.id.bookmarkTabText)
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