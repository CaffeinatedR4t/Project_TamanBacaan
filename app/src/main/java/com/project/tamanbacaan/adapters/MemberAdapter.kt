package com.caffeinatedr4t.tamanbacaan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R

class MemberAdapter(private val members: List<String>) :
    RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    // View Holder menggunakan layout Android standar (simple_list_item_1)
    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memberText: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        // Menggunakan layout Android standar simple_list_item_1 untuk menampilkan satu baris teks
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.memberText.text = members[position]
    }

    override fun getItemCount(): Int = members.size
}