package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.MemberAdapter
import com.caffeinatedr4t.tamanbacaan.models.User
import com.caffeinatedr4t.tamanbacaan.state.MemberManagementState
import com.caffeinatedr4t.tamanbacaan.viewmodels.MemberManagementViewModel

/**
 * Fragment untuk Manajemen Anggota dengan pola MVVM.
 */
class MemberManagementFragment : Fragment() {

    // UI Components
    private lateinit var recyclerView: RecyclerView
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var tvListTitle: TextView

    // Data
    private val memberList = mutableListOf<User>()

    // ViewModel
    private val viewModel: MemberManagementViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_member_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View
        recyclerView = view.findViewById(R.id.recyclerViewMembers)
        tvListTitle = view.findViewById(R.id.tvListTitle)

        setupRecyclerView()

        // Observe ViewModel State
        observeViewModel()

        // Load data awal
        viewModel.loadMembers()
    }

    private fun setupRecyclerView() {
        memberAdapter = MemberAdapter(memberList,
            onVerifyToggle = { user ->
                // Delegasikan ke ViewModel
                viewModel.toggleVerification(user)
            },
            onRemove = { user ->
                // Delegasikan ke ViewModel
                viewModel.deleteMember(user)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = memberAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MemberManagementState.Loading -> {
                    // Tampilkan indikator loading jika perlu (misal ProgressBar)
                    // tvListTitle.text = "Memuat data..."
                }
                is MemberManagementState.SuccessLoad -> {
                    val members = state.members
                    memberAdapter.updateData(members)
                    tvListTitle.text = "Daftar Anggota Aktif (${members.size} Pengguna)"
                }
                is MemberManagementState.SuccessOperation -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    // List akan otomatis ter-refresh karena di ViewModel memanggil loadMembers() setelah sukses
                }
                is MemberManagementState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                is MemberManagementState.Idle -> {
                    // Do nothing
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Opsional: Refresh data saat kembali ke layar ini
        viewModel.loadMembers()
    }
}