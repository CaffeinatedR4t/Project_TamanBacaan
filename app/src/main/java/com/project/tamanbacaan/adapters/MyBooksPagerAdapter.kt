package com.caffeinatedr4t.tamanbacaan.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.caffeinatedr4t.tamanbacaan.fragments.BookmarkedFragment
import com.caffeinatedr4t.tamanbacaan.fragments.BorrowedBooksFragment

class MyBooksPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = listOf(
        BorrowedBooksFragment(),
        BookmarkedFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}