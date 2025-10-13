package com.caffeinatedr4t.tamanbacaan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.models.User // Menggunakan model User

class MemberAdapter(
    private val members: MutableList<User>,
    private val onEdit: (User) -> Unit,
    private val onRemove: (User) -> Unit
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvMemberName)
        val tvDetails: TextView = itemView.findViewById(R.id.tvMemberDetails)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditMember)
        val btnRemove: Button = itemView.findViewById(R.id.btnRemoveMember)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member_management, parent, false) // Menggunakan layout baru
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val user = members[position]

        holder.tvName.text = "${user.fullName} (ID: ${user.id})"

        val details = StringBuilder()
        details.append("NIK: ${user.nik}\n")
        details.append("Alamat: ${user.addressRtRw}\n")
        if (user.isChild) {
            details.append("Tipe: Anak (Ortu: ${user.parentName})")
        } else {
            details.append("Tipe: Dewasa")
        }

        holder.tvDetails.text = details.toString()

        holder.btnEdit.setOnClickListener { onEdit(user) }
        holder.btnRemove.setOnClickListener { onRemove(user) }
    }

    override fun getItemCount(): Int = members.size

    fun updateData(newMembers: List<User>) {
        members.clear()
        members.addAll(newMembers)
        notifyDataSetChanged()
    }
}