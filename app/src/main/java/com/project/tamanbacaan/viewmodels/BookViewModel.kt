package com.caffeinatedr4t.tamanbacaan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.Book
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    // LiveData untuk List Buku
    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    // LiveData untuk Loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // [FIX] Mengembalikan variable error yang hilang
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // LiveData untuk Pesan Toast (Feedback user)
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // Inisiasi fitur machine learning
    private val _recommendationBooks = MutableLiveData<List<Book>>()
    val recommendationBooks: LiveData<List<Book>> = _recommendationBooks

    // Ambil buku + status transaksi user
    fun fetchBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Panggil fungsi Repository yang mengambil buku + status peminjaman
                _books.value = BookRepository.getAllBooksWithStatus()
                _error.value = null // Reset error jika sukses
            } catch (e: Exception) {
                _error.value = e.message // Set pesan error jika gagal
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi Request Pinjam
    fun requestBorrow(book: Book) {
        viewModelScope.launch {
            val userId = BookRepository.currentUserId
            if (userId == null) {
                _toastMessage.value = "Silakan login ulang."
                return@launch
            }

            _isLoading.value = true
            try {
                val success = BookRepository.requestBorrowBook(book, userId)
                if (success) {
                    _toastMessage.value = "Permintaan berhasil! Menunggu persetujuan Admin."
                    // Refresh list agar tombol di UI berubah jadi 'Pending'
                    fetchBooks()
                } else {
                    _toastMessage.value = "Gagal mengajukan pinjaman."
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchRecommendations() {
        viewModelScope.launch {
            // Ambil ID user yang sedang login dari Repository
            val userId = BookRepository.currentUserId
            if (userId != null) {
                try {
                    val books = BookRepository.getRecommendations(userId)
                    _recommendationBooks.value = books
                } catch (e: Exception) {
                    // Handle error silent
                }
            }
        }
    }
}