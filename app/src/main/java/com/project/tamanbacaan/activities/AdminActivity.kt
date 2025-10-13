package com.caffeinatedr4t.tamanbacaan.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.fragments.admin.BookManagementFragment
import com.caffeinatedr4t.tamanbacaan.fragments.admin.ReportFragment
import com.caffeinatedr4t.tamanbacaan.fragments.admin.TransactionManagementFragment
import com.caffeinatedr4t.tamanbacaan.fragments.admin.MemberManagementFragment // Member Management digunakan untuk Verifikasi RT/RW
// REMOVED: import com.caffeinatedr4t.tamanbacaan.fragments.admin.VerificationRequestsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)


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

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_admin_books
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.admin_nav_host_fragment, fragment)
            .commit()
    }
}