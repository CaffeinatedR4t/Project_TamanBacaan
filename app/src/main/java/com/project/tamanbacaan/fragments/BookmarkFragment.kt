package com.caffeinatedr4t.tamanbacaan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.caffeinatedr4t.tamanbacaan.R

class BookmarkFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Create a simple layout for now
        return inflater.inflate(android.R.layout.simple_list_item_1, container, false).apply {
            findViewById<android.widget.TextView>(android.R.id.text1).text = "Bookmark Fragment"
        }
    }
}