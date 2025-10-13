package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class TransactionManagementFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Ganti dengan layout yang berisi daftar transaksi dan detail anggota
        return TextView(context).apply { text = "ADMIN: Manajemen Transaksi & Daftar Anggota" }
    }
}