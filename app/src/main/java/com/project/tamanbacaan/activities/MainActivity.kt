package com.project.tamanbacaan.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.fragments.HomeFragment
import com.caffeinatedr4t.tamanbacaan.fragments.SearchFragment
import com.caffeinatedr4t.tamanbacaan.fragments.BookmarkFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load home fragment by default
        loadFragment(HomeFragment())

        // Set up bottom navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.btnHome).setOnClickListener {
            loadFragment(HomeFragment())
        }
        findViewById<View>(R.id.btnSearch).setOnClickListener {
            loadFragment(SearchFragment())
        }
        findViewById<View>(R.id.btnBookmark).setOnClickListener {
            loadFragment(BookmarkFragment())
        }
        findViewById<View>(R.id.btnExplore).setOnClickListener {
            loadFragment(SearchFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
    }
}