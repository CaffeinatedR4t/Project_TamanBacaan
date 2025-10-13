package com.caffeinatedr4t.tamanbacaan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.data.PendingRequest

class PendingRequestAdapter(
    private val requests: MutableList<PendingRequest>,
    private val onApprove: (PendingRequest) -> Unit,
    private val onReject: (PendingRequest) -> Unit
) : RecyclerView.Adapter<PendingRequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBookTitle: TextView = itemView.findViewById(R.id.tvRequestBookTitle)
        val tvMember: TextView = itemView.findViewById(R.id.tvRequestMember)
        val btnApprove: Button = itemView.findViewById(R.id.btnApprove)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]

        holder.tvBookTitle.text = request.book.title
        holder.tvMember.text = "Oleh: ${request.memberName} (ID ${request.memberId})"

        holder.btnApprove.setOnClickListener {
            onApprove(request)
        }

        holder.btnReject.setOnClickListener {
            onReject(request)
        }
    }

    override fun getItemCount(): Int = requests.size

    fun updateData(newRequests: List<PendingRequest>) {
        requests.clear()
        requests.addAll(newRequests)
        notifyDataSetChanged()
    }
}