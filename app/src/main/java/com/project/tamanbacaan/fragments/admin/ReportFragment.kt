package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.data.BookRepository

class ReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<LinearLayout>(R.id.containerTopBooks)
        displayTopBooks(container)
    }

    private fun displayTopBooks(container: LinearLayout) {
        val topBooks = BookRepository.getTopBooks().entries.sortedByDescending { it.value }
        val maxCount = topBooks.firstOrNull()?.value ?: 1 // Untuk skala bar

        topBooks.forEach { (title, count) ->
            val barContainer = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 8 }
            }

            // Text Judul
            val titleView = TextView(context).apply {
                text = title
                width = 0
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.4f // 40% lebar
                ).apply {
                    // Perlu menggunakan setMargins di sini karena kita menggunakan LayoutParams
                    setMargins(16, 4, 0, 4)
                }
                setTextColor(Color.parseColor("#212121"))
            }
            barContainer.addView(titleView)

            // Bar Visualisasi
            val barWidthWeight = count.toFloat() / maxCount.toFloat() * 0.6f // 60% sisa lebar
            val barView = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    24,
                    barWidthWeight
                ).apply {
                    setMargins(16, 4, 0, 4)
                }
                setBackgroundColor(resources.getColor(R.color.primary_blue))
            }
            barContainer.addView(barView)

            // Jumlah
            val countView = TextView(context).apply {
                text = count.toString()
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 4, 0, 4)
                }
                setTextColor(resources.getColor(R.color.primary_blue_dark))
                // FIX: Mengganti textStyle dengan setTypeface (Line 77)
                setTypeface(null, Typeface.BOLD)
            }
            barContainer.addView(countView)

            container.addView(barContainer)
        }
    }
}