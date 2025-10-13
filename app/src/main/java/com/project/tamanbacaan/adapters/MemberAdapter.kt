package com.caffeinatedr4t.tamanbacaan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.models.User

class MemberAdapter(
    private val members: MutableList<User>,
    private val onVerifyToggle: (User) -> Unit, // Ganti onEdit menjadi onVerifyToggle
    private val onRemove: (User) -> Unit
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvMemberName)
        val tvDetails: TextView = itemView.findViewById(R.id.tvMemberDetails)
        val tvStatus: TextView = itemView.findViewById(R.id.tvVerificationStatus) // BARU
        val btnVerify: Button = itemView.findViewById(R.id.btnVerifyMember) // BARU: Tombol Verifikasi
        val btnRemove: Button = itemView.findViewById(R.id.btnRemoveMember)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member_management, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val user = members[position]

        // Tampilkan data dasar
        holder.tvName.text = "${user.fullName} (ID: ${user.id})"

        val details = StringBuilder()
        details.append("NIK: ${user.nik}\n")
        details.append("RT/RW: ${user.addressRtRw}\n")
        if (user.isChild) {
            details.append("Tipe: Anak (Ortu: ${user.parentName})")
        } else {
            details.append("Tipe: Dewasa")
        }
        holder.tvDetails.text = details.toString()

        // Tampilkan Status Verifikasi (Req: Dapat memverifikasi data anggota)
        if (user.isVerified) {
            holder.tvStatus.text = "VERIFIED"
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.success_green))
            holder.btnVerify.text = "Batalkan Verifikasi"
            holder.btnVerify.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.warning_orange))
        } else {
            holder.tvStatus.text = "UNVERIFIED"
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.error_red))
            holder.btnVerify.text = "Verifikasi Warga"
            holder.btnVerify.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_blue))
        }

        // Listeners
        holder.btnVerify.setOnClickListener { onVerifyToggle(user) }
        holder.btnRemove.setOnClickListener { onRemove(user) }
    }

    override fun getItemCount(): Int = members.size

    fun updateData(newMembers: List<User>) {
        members.clear()
        members.addAll(newMembers)
        notifyDataSetChanged()
    }
}