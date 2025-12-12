package com.caffeinatedr4t.tamanbacaan.activities

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.Constants
import kotlinx.coroutines.launch

/**
 * Activity untuk Admin/Pengelola dalam mengedit detail buku yang ada.
 * Menerima Book ID melalui Intent dan memuat data buku untuk diedit.
 */
class EditBookActivity : AppCompatActivity() {

    // Deklarasi View untuk Input Form
    private lateinit var etTitle: EditText // Input untuk Judul Buku
    private lateinit var etAuthor: EditText // Input untuk Penulis
    private lateinit var etDescription: EditText // Input untuk Deskripsi/Sinopsis
    private lateinit var etCategory: EditText // Input untuk Kategori
    private lateinit var etIsbn: EditText // Input untuk ISBN
    private lateinit var etPublicationYear: EditText // Input untuk Tahun Publikasi
    private lateinit var etCoverUrl: EditText // Input untuk URL Cover Buku
    private lateinit var cbIsAvailable: CheckBox // CheckBox untuk status Ketersediaan
    private lateinit var cbIsBorrowed: CheckBox // CheckBox untuk status Dipinjam
    private lateinit var btnSave: Button // Tombol untuk menyimpan perubahan

    // Variabel state
    private var bookId: String? = null // ID buku yang sedang diedit (diambil dari Intent)
    private var currentBook: Book? = null // Objek Book yang sedang dimuat/diedit

    /**
     * Dipanggil saat Activity pertama kali dibuat.
     * Mengatur layout, menginisialisasi View, listener, dan memuat data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        initializeViews()
        setupListeners()
        loadBookData()
    }

    /**
     * Menginisialisasi semua elemen View dari layout `activity_edit_book`.
     */
    private fun initializeViews() {
        etTitle = findViewById(R.id.etEditTitle)
        etAuthor = findViewById(R.id.etEditAuthor)
        etDescription = findViewById(R.id.etEditDescription)
        etCategory = findViewById(R.id.etEditCategory)
        etIsbn = findViewById(R.id.etEditIsbn)
        etPublicationYear = findViewById(R.id.etEditPublicationYear)
        etCoverUrl = findViewById(R.id.etEditCoverUrl)
        cbIsAvailable = findViewById(R.id.cbIsAvailable)
        cbIsBorrowed = findViewById(R.id.cbIsBorrowed)
        btnSave = findViewById(R.id.btnSaveEditBook)
    }

    /**
     * Menyiapkan listener untuk tombol dan CheckBox.
     */
    private fun setupListeners() {

        // Listener untuk tombol Simpan
        btnSave.setOnClickListener {
            saveBook()
        }

        // Logic bisnis: Jika buku ditandai 'Sedang dipinjam', maka otomatis 'Tidak Tersedia'
        // dan CheckBox-nya dinonaktifkan untuk mencegah kesalahan data.
        cbIsBorrowed.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbIsAvailable.isChecked = false
                cbIsAvailable.isEnabled = false // Memastikan ketersediaan tidak dapat diubah saat dipinjam
            } else {
                cbIsAvailable.isEnabled = true // Mengaktifkan kembali ketersediaan saat tidak dipinjam
            }
        }
    }

    /**
     * Memuat data buku dari Intent menggunakan BookRepository (API) dan mengisi form.
     */
    private fun loadBookData() {
        // Ambil ID buku (EXTRA_BOOK_ID) dari Intent
        bookId = intent.getStringExtra(Constants.EXTRA_BOOK_ID)

        // Validasi: Cek apakah Book ID ada
        if (bookId == null) {
            Toast.makeText(this, "Book ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Fetch book from API
        lifecycleScope.launch {
            try {
                // Cari buku di Repository (API) berdasarkan ID yang ditemukan
                currentBook = BookRepository.getBookById(bookId!!)

                // Validasi: Cek apakah buku ditemukan
                if (currentBook == null) {
                    Toast.makeText(this@EditBookActivity, "Buku tidak ditemukan", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                // Isi form dengan data buku yang dimuat
                currentBook?.let { book ->
                    etTitle.setText(book.title)
                    etAuthor.setText(book.author)
                    etDescription.setText(book.description)
                    etCategory.setText(book.category)
                    etIsbn.setText(book.isbn)
                    // Tampilkan tahun publikasi jika valid
                    etPublicationYear.setText(if (book.publicationYear > 0) book.publicationYear.toString() else "")
                    etCoverUrl.setText(book.coverUrl)
                    cbIsAvailable.isChecked = book.isAvailable
                    cbIsBorrowed.isChecked = book.isBorrowed

                    // Terapkan logika nonaktifkan checkbox ketersediaan jika buku sedang dipinjam
                    if (book.isBorrowed) {
                        cbIsAvailable.isEnabled = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@EditBookActivity, "Error loading book: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Mengambil data dari form, melakukan validasi, dan menyimpan perubahan ke BookRepository.
     */
    private fun saveBook() {
        // Mengambil nilai input dari form
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val isbn = etIsbn.text.toString().trim()
        val publicationYearStr = etPublicationYear.text.toString().trim()
        val coverUrl = etCoverUrl.text.toString().trim()
        // Mengambil status dari CheckBoxes
        val isAvailable = cbIsAvailable.isChecked
        val isBorrowed = cbIsBorrowed.isChecked

        // Validasi wajib: Judul
        if (title.isEmpty()) {
            Toast.makeText(this, "Judul buku harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Validasi wajib: Penulis
        if (author.isEmpty()) {
            Toast.makeText(this, "Penulis harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Konversi tahun publikasi ke Int, default 0 jika gagal
        val publicationYear = publicationYearStr.toIntOrNull() ?: 0

        // Validasi tahun publikasi (batasan 1000-2100)
        if (publicationYear > 0 && (publicationYear < 1000 || publicationYear > 2100)) {
            Toast.makeText(this, "Tahun publikasi tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Membuat objek Book baru yang diperbarui
        val updatedBook = currentBook!!.copy(
            title = title,
            author = author,
            description = description,
            category = category,
            isbn = isbn,
            publicationYear = publicationYear,
            coverUrl = coverUrl,
            isAvailable = isAvailable,
            isBorrowed = isBorrowed
        )

        // Memanggil fungsi update di Repository
        val success = BookRepository.updateBook(updatedBook)

        // Memberikan feedback dan menutup Activity
        if (success) {
            Toast.makeText(this, "Buku berhasil diupdate!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK) // Menandai bahwa operasi berhasil
            finish()
        } else {
            Toast.makeText(this, "Gagal mengupdate buku", Toast.LENGTH_SHORT).show()
        }
    }
}