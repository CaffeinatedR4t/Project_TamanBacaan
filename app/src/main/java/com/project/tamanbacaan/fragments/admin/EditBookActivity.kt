package com.caffeinatedr4t.tamanbacaan.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL

class EditBookActivity : AppCompatActivity() {

    // View declarations
    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etDescription: EditText
    private lateinit var etCategory: EditText
    private lateinit var etIsbn: EditText
    private lateinit var etPublicationYear: EditText
    private lateinit var etStock: EditText // [BARU] Deklarasi EditText Stok

    private lateinit var ivBookCoverPreview: ImageView
    private lateinit var btnPickGallery: Button
    private lateinit var btnOpenCamera: Button

    private lateinit var cbIsAvailable: CheckBox
    private lateinit var cbIsBorrowed: CheckBox
    private lateinit var btnSave: Button

    private var bookId: String? = null
    private var currentBook: Book? = null
    private var newCoverBase64: String? = null

    // Launcher Galeri
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = uriToBitmap(it)
            displayAndProcessImage(bitmap)
        }
    }

    // Launcher Kamera
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            displayAndProcessImage(it)
        }
    }

    // Launcher Izin Kamera
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        initializeViews()
        setupListeners()

        loadDataFromIntent()

        if (bookId != null) {
            refreshBookData(bookId!!)
        }
    }

    private fun initializeViews() {
        etTitle = findViewById(R.id.etEditTitle)
        etAuthor = findViewById(R.id.etEditAuthor)
        etDescription = findViewById(R.id.etEditDescription)
        etCategory = findViewById(R.id.etEditCategory)
        etIsbn = findViewById(R.id.etEditIsbn)
        etPublicationYear = findViewById(R.id.etEditPublicationYear)
        etStock = findViewById(R.id.etEditStock) // [BARU] Inisialisasi

        ivBookCoverPreview = findViewById(R.id.ivBookCoverPreview)
        btnPickGallery = findViewById(R.id.btnPickGallery)
        btnOpenCamera = findViewById(R.id.btnOpenCamera)

        cbIsAvailable = findViewById(R.id.cbIsAvailable)
        cbIsBorrowed = findViewById(R.id.cbIsBorrowed)
        btnSave = findViewById(R.id.btnSaveEditBook)
    }

    private fun setupListeners() {
        btnSave.setOnClickListener { saveBook() }

        cbIsBorrowed.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbIsAvailable.isChecked = false
                cbIsAvailable.isEnabled = false
            } else {
                cbIsAvailable.isEnabled = true
            }
        }

        btnPickGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btnOpenCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(null)
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun loadDataFromIntent() {
        bookId = intent.getStringExtra(Constants.EXTRA_BOOK_ID)

        if (bookId == null) {
            Toast.makeText(this, "Error: ID Buku tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val title = intent.getStringExtra("EXTRA_TITLE") ?: ""
        val author = intent.getStringExtra("EXTRA_AUTHOR") ?: ""
        val description = intent.getStringExtra("EXTRA_DESCRIPTION") ?: ""
        val category = intent.getStringExtra("EXTRA_CATEGORY") ?: ""
        val isbn = intent.getStringExtra("EXTRA_ISBN") ?: ""
        val pubYear = intent.getIntExtra("EXTRA_PUBLICATION_YEAR", 0)
        val coverUrl = intent.getStringExtra("EXTRA_COVER_URL") ?: ""
        val isAvailable = intent.getBooleanExtra("EXTRA_IS_AVAILABLE", true)
        val isBorrowed = intent.getBooleanExtra("EXTRA_IS_BORROWED", false)
        val stock = intent.getIntExtra("EXTRA_STOCK", 0) // Ambil data stok
        val totalCopies = intent.getIntExtra("EXTRA_TOTAL_COPIES", 0)

        etTitle.setText(title)
        etAuthor.setText(author)
        etDescription.setText(description)
        etCategory.setText(category)
        etIsbn.setText(isbn)
        etPublicationYear.setText(if (pubYear > 0) pubYear.toString() else "")
        etStock.setText(stock.toString()) // [BARU] Set text stok ke UI

        cbIsAvailable.isChecked = isAvailable
        cbIsBorrowed.isChecked = isBorrowed
        if (isBorrowed) cbIsAvailable.isEnabled = false

        if (coverUrl.isNotEmpty()) {
            loadImageFromUrlOrBase64(coverUrl)
        }

        currentBook = Book(
            id = bookId!!,
            title = title,
            author = author,
            description = description,
            category = category,
            isbn = isbn,
            publicationYear = pubYear,
            coverUrl = coverUrl,
            stock = stock,
            totalCopies = totalCopies,
            isAvailable = isAvailable,
            isBorrowed = isBorrowed,
            isBookmarked = false
        )
    }

    private fun loadImageFromUrlOrBase64(input: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val bitmap: Bitmap? = if (input.startsWith("data:image")) {
                    val base64String = input.substringAfter(",")
                    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                } else if (input.startsWith("http")) {
                    val url = URL(input)
                    BitmapFactory.decodeStream(url.openConnection().getInputStream())
                } else {
                    null
                }

                withContext(Dispatchers.Main) {
                    bitmap?.let { ivBookCoverPreview.setImageBitmap(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun refreshBookData(id: String) {
        lifecycleScope.launch {
            try {
                val fetchedBook = BookRepository.getBookById(id)
                if (fetchedBook != null) {
                    currentBook = fetchedBook
                    Log.d("EditBook", "Data buku berhasil diperbarui dari server")
                }
            } catch (e: Exception) {
                Log.e("EditBook", "Gagal refresh data: ${e.message}")
            }
        }
    }

    private fun displayAndProcessImage(bitmap: Bitmap?) {
        bitmap?.let {
            ivBookCoverPreview.setImageBitmap(it)
            newCoverBase64 = bitmapToBase64(it)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveBook() {
        if (currentBook == null) {
            Toast.makeText(this, "Data buku belum siap.", Toast.LENGTH_SHORT).show()
            return
        }

        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val isbn = etIsbn.text.toString().trim()
        val publicationYearStr = etPublicationYear.text.toString().trim()
        val stockStr = etStock.text.toString().trim() // [BARU] Ambil input stok
        val isAvailable = cbIsAvailable.isChecked
        val isBorrowed = cbIsBorrowed.isChecked

        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(this, "Judul dan Penulis wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val publicationYear = publicationYearStr.toIntOrNull() ?: 0
        val stock = stockStr.toIntOrNull() ?: 0 // [BARU] Konversi ke Int

        val finalCoverUrl = newCoverBase64 ?: currentBook!!.coverUrl

        val updatedBook = currentBook!!.copy(
            title = title,
            author = author,
            description = description,
            category = category,
            isbn = isbn,
            publicationYear = publicationYear,
            coverUrl = finalCoverUrl,
            stock = stock, // [BARU] Update field stock
            isAvailable = isAvailable,
            isBorrowed = isBorrowed
        )

        lifecycleScope.launch {
            btnSave.isEnabled = false
            btnSave.text = "Menyimpan..."

            val success = BookRepository.updateBook(updatedBook)

            if (success) {
                Toast.makeText(this@EditBookActivity, "Buku berhasil diupdate!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this@EditBookActivity, "Gagal mengupdate buku", Toast.LENGTH_SHORT).show()
                btnSave.isEnabled = true
                btnSave.text = "SIMPAN PERUBAHAN"
            }
        }
    }
}