package com.caffeinatedr4t.tamanbacaan.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.caffeinatedr4t.tamanbacaan.fragments.BookmarkedFragment
import com.caffeinatedr4t.tamanbacaan.fragments.BorrowedBooksFragment

// Adapter untuk ViewPager2 yang menampilkan tab "Pinjaman Buku" dan "Bookmark".
class MyBooksPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    // Daftar fragment yang akan ditampilkan dalam ViewPager.
    private val fragments = listOf(
        BorrowedBooksFragment(),
        BookmarkedFragment()
    )

    // Mengembalikan jumlah total fragment (tab).
    override fun getItemCount(): Int = fragments.size

    // Membuat dan mengembalikan fragment pada posisi tertentu.
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}