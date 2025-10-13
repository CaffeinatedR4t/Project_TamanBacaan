package com.caffeinatedr4t.tamanbacaan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.data.RegistrationRequest

class RegistrationRequestAdapter(
    private val requests: MutableList<RegistrationRequest>,
    private val onApprove: (RegistrationRequest) -> Unit,
    private val onReject: (RegistrationRequest) -> Unit
) : RecyclerView.Adapter<RegistrationRequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvRequestBookTitle) // Reusing ID for Name
        val tvNik: TextView = itemView.findViewById(R.id.tvRequestMember) // Reusing ID for NIK/Details
        val btnApprove: Button = itemView.findViewById(R.id.btnApprove)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        // Menggunakan item_pending_request.xml karena memiliki struktur 2 tombol yang sama
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]

        val memberType = if (request.isChild) "Anak (${request.parentName})" else "Dewasa"
        val detailText = "NIK: ${request.nik}\nAlamat: ${request.addressRtRw}"

        holder.tvName.text = "${request.fullName} (${memberType})"
        holder.tvNik.text = detailText

        // Ganti teks header agar lebih spesifik (walaupun layoutnya sama)
        val header = holder.itemView.findViewById<TextView>(R.id.tvRequestBookTitle).rootView.findViewById<TextView>(R.id.tvRequestBookTitle)
        header.text = "Permintaan Registrasi Baru"
        header.setTextColor(holder.itemView.context.resources.getColor(R.color.error_red)) // Warna merah untuk perhatian

        holder.btnApprove.setOnClickListener {
            onApprove(request)
        }

        holder.btnReject.setOnClickListener {
            onReject(request)
        }
    }

    override fun getItemCount(): Int = requests.size

    fun updateData(newRequests: List<RegistrationRequest>) {
        requests.clear()
        requests.addAll(newRequests)
        notifyDataSetChanged()
    }
}