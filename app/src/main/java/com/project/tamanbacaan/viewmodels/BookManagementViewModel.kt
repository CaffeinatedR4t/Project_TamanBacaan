package com.caffeinatedr4t.tamanbacaan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.data.CreateBookRequest
import com.caffeinatedr4t.tamanbacaan.state.BookManagementState
import kotlinx.coroutines.launch

class BookManagementViewModel : ViewModel() {

    private val _state = MutableLiveData<BookManagementState>(BookManagementState.Idle)
    val state: LiveData<BookManagementState> = _state

    /**
     * Mengambil daftar buku dari API
     */
    fun loadBooks() {
        _state.value = BookManagementState.Loading
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getBooks()
                if (response.isSuccessful) {
                    val books = response.body() ?: emptyList()
                    _state.value = BookManagementState.SuccessLoad(books)
                } else {
                    _state.value = BookManagementState.Error("Gagal memuat buku: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = BookManagementState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Menambahkan buku baru
     */
    fun createBook(request: CreateBookRequest) {
        _state.value = BookManagementState.Loading
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().createBook(request)
                if (response.isSuccessful) {
                    _state.value = BookManagementState.SuccessOperation("Buku berhasil ditambahkan!")
                    // Otomatis refresh list setelah sukses
                    loadBooks()
                } else {
                    _state.value = BookManagementState.Error("Gagal menambahkan buku: ${response.message()}")
                }
            } catch (e: Exception) {
                _state.value = BookManagementState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Menghapus buku berdasarkan ID
     */
    fun deleteBook(bookId: String) {
        _state.value = BookManagementState.Loading
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().deleteBook(bookId)
                if (response.isSuccessful) {
                    _state.value = BookManagementState.SuccessOperation("Buku berhasil dihapus.")
                    // Otomatis refresh list setelah sukses
                    loadBooks()
                } else {
                    _state.value = BookManagementState.Error("Gagal menghapus buku: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = BookManagementState.Error("Error: ${e.message}")
            }
        }
    }

    // Reset state ke Idle agar pesan sukses tidak muncul berulang saat rotasi layar
    fun resetState() {
        _state.value = BookManagementState.Idle
    }
}