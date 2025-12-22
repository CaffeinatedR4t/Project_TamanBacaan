package com.caffeinatedr4t.tamanbacaan.state

import com.caffeinatedr4t.tamanbacaan.models.Book

sealed class BookManagementState {
    // Status default/diam
    object Idle : BookManagementState()

    // Status sedang memuat (loading)
    object Loading : BookManagementState()

    // Sukses memuat data buku (untuk ditampilkan di RecyclerView)
    data class SuccessLoad(val books: List<Book>) : BookManagementState()

    // Sukses melakukan operasi tambah/hapus (untuk menampilkan Toast & refresh)
    data class SuccessOperation(val message: String) : BookManagementState()

    // Terjadi error
    data class Error(val message: String) : BookManagementState()
}