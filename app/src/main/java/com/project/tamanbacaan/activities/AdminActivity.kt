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

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Tampilkan fragment Home pertama kali
        if (savedInstanceState == null) {
            loadFragment(BookManagementFragment())
        }
        // Setup topNavigation
        setupTopNavigation()
        // Setup topNavigation
        setupBottomNavigation()
    }

    private fun setupTopNavigation() {
        val btnProfile = findViewById<ImageView>(R.id.btnAdminProfile)
        val btnNotification = findViewById<ImageView>(R.id.btnNotification)
        // Tombol Profil di TopBar
        btnProfile.setOnClickListener {
            loadFragment(AdminProfileFragment())
        }
        // Tombol Notifikasi di TopBar
        btnNotification.setOnClickListener {
            loadFragment(NotificationFragment())
        }
    }
    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.adminBottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_books -> {
                    loadFragment(BookManagementFragment())
                    true
                }
                R.id.nav_admin_members -> { // Manajemen Anggota (CRUD + Verifikasi RT/RW)
                    loadFragment(MemberManagementFragment())
                    true
                }
                R.id.nav_admin_events -> {
                    loadFragment(EventManagementFragment())
                    true
                }
                R.id.nav_admin_requests -> { // Permintaan Pinjaman (Transaksi)
                    loadFragment(TransactionManagementFragment())
                    true
                }
                // REMOVED: nav_admin_verification
                R.id.nav_admin_reports -> {
                    loadFragment(ReportFragment())
                    true
                }
                else -> false
            }
        }
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.admin_nav_host_fragment, fragment)
            .commit()
    }
}