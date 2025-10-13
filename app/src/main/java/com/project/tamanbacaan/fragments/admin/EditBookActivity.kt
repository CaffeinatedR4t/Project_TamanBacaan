package com.caffeinatedr4t.tamanbacaan.activities

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.Constants

class EditBookActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etDescription: EditText
    private lateinit var etCategory: EditText
    private lateinit var etIsbn: EditText
    private lateinit var etPublicationYear: EditText
    private lateinit var etCoverUrl: EditText
    private lateinit var cbIsAvailable: CheckBox
    private lateinit var cbIsBorrowed: CheckBox
    private lateinit var btnSave: Button

    private var bookId: String? = null
    private var currentBook: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        initializeViews()
        setupListeners()
        loadBookData()
    }

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

    private fun setupListeners() {

        btnSave.setOnClickListener {
            saveBook()
        }

        // Logic: Jika buku dipinjam, otomatis tidak tersedia
        cbIsBorrowed.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbIsAvailable.isChecked = false
                cbIsAvailable.isEnabled = false
            } else {
                cbIsAvailable.isEnabled = true
            }
        }
    }

    private fun loadBookData() {
        bookId = intent.getStringExtra(Constants.EXTRA_BOOK_ID)

        if (bookId == null) {
            Toast.makeText(this, "Book ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentBook = BookRepository.getBookById(bookId!!)

        if (currentBook == null) {
            Toast.makeText(this, "Buku tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Populate form dengan data buku
        currentBook?.let { book ->
            etTitle.setText(book.title)
            etAuthor.setText(book.author)
            etDescription.setText(book.description)
            etCategory.setText(book.category)
            etIsbn.setText(book.isbn)
            etPublicationYear.setText(if (book.publicationYear > 0) book.publicationYear.toString() else "")
            etCoverUrl.setText(book.coverUrl)
            cbIsAvailable.isChecked = book.isAvailable
            cbIsBorrowed.isChecked = book.isBorrowed

            // Disable available checkbox jika buku sedang dipinjam
            if (book.isBorrowed) {
                cbIsAvailable.isEnabled = false
            }
        }
    }

    private fun saveBook() {
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val isbn = etIsbn.text.toString().trim()
        val publicationYearStr = etPublicationYear.text.toString().trim()
        val coverUrl = etCoverUrl.text.toString().trim()
        val isAvailable = cbIsAvailable.isChecked
        val isBorrowed = cbIsBorrowed.isChecked

        // Validasi
        if (title.isEmpty()) {
            Toast.makeText(this, "Judul buku harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (author.isEmpty()) {
            Toast.makeText(this, "Penulis harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val publicationYear = publicationYearStr.toIntOrNull() ?: 0

        // Validasi tahun publikasi
        if (publicationYear > 0 && (publicationYear < 1000 || publicationYear > 2100)) {
            Toast.makeText(this, "Tahun publikasi tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Update buku
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

        val success = BookRepository.updateBook(updatedBook)

        if (success) {
            Toast.makeText(this, "Buku berhasil diupdate!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Gagal mengupdate buku", Toast.LENGTH_SHORT).show()
        }
    }
}