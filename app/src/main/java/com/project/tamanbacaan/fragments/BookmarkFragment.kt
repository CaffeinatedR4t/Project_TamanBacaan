package com.caffeinatedr4t.tamanbacaan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.MyBooksPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Fragment utama untuk bagian "My Library" atau "Bookmark".
 * Menggunakan TabLayout dan ViewPager2 untuk menampung sub-fragment:
 * 1. BorrowedBooksFragment (Pinjaman Buku)
 * 2. BookmarkedFragment (Bookmark Saya)
 */
class BookmarkFragment : Fragment() {

    /**
     * Membuat dan mengembalikan hierarki tampilan fragmen.
     * Menggunakan layout yang berisi TabLayout dan ViewPager2.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    /**
     * Dipanggil setelah `onCreateView()` dan memastikan view sudah dibuat.
     * Menginisialisasi ViewPager2 dan menghubungkannya dengan TabLayout.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayoutMyBooks) // Komponen tab navigasi
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPagerMyBooks) // Komponen swipeable view container

        // Adapter untuk ViewPager2 yang akan mengelola fragmen anak
        val pagerAdapter = MyBooksPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = pagerAdapter

        // Hubungkan TabLayout dengan ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Menetapkan teks untuk setiap tab berdasarkan posisi
            tab.text = when (position) {
                0 -> "Pinjaman Buku" // Tab 1: Daftar buku yang sedang dipinjam
                1 -> "Bookmark Saya" // Tab 2: Daftar buku yang di-bookmark
                else -> ""
            }
        }.attach() // Melampirkan mediator ke TabLayout dan ViewPager2
    }
}