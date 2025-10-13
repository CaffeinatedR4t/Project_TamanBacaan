package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.data.BookRepository

class TransactionManagementFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    // Untuk demo cepat, kita gunakan ListView atau ArrayAdapter sederhana.
    private lateinit var listViewMembers: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_member_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menggunakan ListView untuk menampilkan daftar anggota
        listViewMembers = view.findViewById(R.id.recyclerViewMembers) as ListView

        // Ambil data anggota simulasi
        val memberList = BookRepository.getSampleMembers()

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            memberList
        )

        listViewMembers.adapter = adapter
    }
}