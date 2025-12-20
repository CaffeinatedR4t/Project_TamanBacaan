package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.AdminBookAdapter
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.data.CreateBookRequest
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import com.caffeinatedr4t.tamanbacaan.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import android.widget.ImageButton
import android.view.animation.OvershootInterpolator
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Fragment untuk manajemen Buku (CRUD) oleh Admin.
 * Menyediakan form untuk menambah buku baru dan daftar buku untuk diedit/dihapus.
 */
class BookManagementFragment : Fragment() {

    // UI: Daftar Buku
    private lateinit var recyclerView: RecyclerView // RecyclerView untuk menampilkan daftar buku
    private lateinit var bookAdapter: AdminBookAdapter // Adapter untuk daftar buku admin
    private val booksList = mutableListOf<Book>() // Daftar buku yang ditampilkan

    // UI: Form Tambah Buku
    private lateinit var formTitle: TextView // Judul form (Tambah Buku Baru)
    private lateinit var etTitle: EditText // Input Judul Buku
    private lateinit var etAuthor: EditText // Input Penulis
    private lateinit var etCategory: EditText // Input Kategori
    private lateinit var etStock: EditText // Input Stok (Jumlah)
    private lateinit var btnSaveBook: Button // Tombol Simpan Buku
    private lateinit var eventViewModel: EventViewModel
    // ... variabel UI yang sudah ada ...
    private lateinit var ivBookCoverPreview: ImageView
    private lateinit var btnPickGallery: Button
    private lateinit var btnOpenCamera: Button
    private lateinit var btnToggleForm: ImageButton
    private lateinit var formContainer: View

    // Variabel untuk menyimpan string Base64 gambar
    private var selectedCoverBase64: String? = null
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = uriToBitmap(it)
            displayAndProcessImage(bitmap)
        }
    }

    // Launcher untuk Kamera (Thumbnail)
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            displayAndProcessImage(it)
        }
    }

    // Launcher untuk meminta izin kamera
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Izin diberikan, langsung buka kamera
            cameraLauncher.launch(null)
        } else {
            // Izin ditolak
            Toast.makeText(context, "Izin kamera diperlukan untuk mengambil foto", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Activity Result Launcher untuk memproses hasil dari EditBookActivity.
     * Digunakan untuk me-refresh daftar buku jika edit berhasil (RESULT_OK).
     */
    private val editBookLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadBooks() // Refresh list setelah edit berhasil
        }
    }

    /**
     * Membuat dan mengembalikan hierarki tampilan fragmen.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_book_management, container, false)
    }

    /**
     * Dipanggil setelah `onCreateView()`.
     * Menginisialisasi semua View, menyiapkan RecyclerView, dan memuat data awal.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inisialisasi View Baru (Toggle & Container Form)
        btnToggleForm = view.findViewById(R.id.btnToggleForm)
        formContainer = view.findViewById(R.id.formContainer)

        // 2. Inisialisasi View Form Lama
        formTitle = view.findViewById(R.id.formTitle)
        etTitle = view.findViewById(R.id.etTitle)
        etAuthor = view.findViewById(R.id.etAuthor)
        etCategory = view.findViewById(R.id.etCategory)
        etStock = view.findViewById(R.id.etStock)
        btnSaveBook = view.findViewById(R.id.btnSaveBook)

        // 3. Inisialisasi RecyclerView [PENTING!]
        recyclerView = view.findViewById(R.id.recyclerViewAdminBooks)
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        // 4. Inisialisasi Image Picker Views
        ivBookCoverPreview = view.findViewById(R.id.ivBookCoverPreview)
        btnPickGallery = view.findViewById(R.id.btnPickGallery)
        btnOpenCamera = view.findViewById(R.id.btnOpenCamera)

        // 5. PANGGIL FUNGSI SETUP INI [SANGAT PENTING JANGAN TERLEWAT]
        setupRecyclerView()      // <-- Menyiapkan adapter list buku
        setupFormToggle()        // <-- Menyiapkan logika buka/tutup form
        setupImagePickers()      // <-- Menyiapkan logika upload gambar
        setupFormListeners()     // <-- Menyiapkan tombol simpan

        // 6. Muat data buku dari database
        loadBooks()
    }

    private fun setupFormToggle() {
        btnToggleForm.setOnClickListener {
            if (formContainer.visibility == View.VISIBLE) {
                // Tutup Form
                formContainer.visibility = View.GONE
                // Animasi putar balik ke icon (+)
                btnToggleForm.animate().rotation(0f).setDuration(300).start()
            } else {
                // Buka Form
                formContainer.visibility = View.VISIBLE
                formContainer.alpha = 0f
                formContainer.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()

                // Animasi putar icon (+) jadi (x)
                btnToggleForm.animate().rotation(45f).setInterpolator(OvershootInterpolator()).setDuration(300).start()
            }
        }
    }

    private fun setupImagePickers() {
        btnPickGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btnOpenCamera.setOnClickListener {
            // Cek apakah izin kamera sudah diberikan
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Jika sudah ada izin, buka kamera
                cameraLauncher.launch(null)
            } else {
                // Jika belum, minta izin ke user
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun displayAndProcessImage(bitmap: Bitmap?) {
        bitmap?.let {
            // 1. Tampilkan di ImageView
            ivBookCoverPreview.setImageBitmap(it)

            // 2. Konversi ke Base64
            selectedCoverBase64 = bitmapToBase64(it)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Kompres gambar ke JPEG kualitas 50% agar tidak terlalu besar untuk dikirim via JSON
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Fungsi helper mengubah URI (dari galeri) menjadi Bitmap
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Dipanggil ketika fragment kembali ke latar depan (visible).
     * Memastikan daftar buku selalu diperbarui.
     */
    override fun onResume() {
        super.onResume()
        loadBooks()
    }

    /**
     * Menyiapkan RecyclerView dengan adapter dan callback untuk aksi delete.
     */
    private fun setupRecyclerView() {
        // Menggunakan AdminBookAdapter dengan callback untuk Delete
        bookAdapter = AdminBookAdapter(
            books = booksList,
            onDeleteClick = { book -> showDeleteConfirmation(book) } // Callback saat tombol Delete diklik
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    /**
     * Memuat daftar semua buku dari API dan memperbarui adapter.
     */
    private fun loadBooks() {
        lifecycleScope.launch {
            try {
                val response = ApiConfig.getApiService().getBooks()
                if (response.isSuccessful) {
                    booksList.clear()
                    booksList.addAll(response.body() ?: emptyList())
                    bookAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Error loading books: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Menyiapkan listener untuk tombol simpan buku.
     */
    private fun setupFormListeners() {
        btnSaveBook.setOnClickListener {
            saveNewBook()
        }
    }

    /**
     * Menampilkan dialog konfirmasi sebelum menghapus buku.
     * @param book Objek Book yang akan dihapus.
     */
    private fun showDeleteConfirmation(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Buku")
            .setMessage("Apakah Anda yakin ingin menghapus '${book.title}'?\n\nData buku akan dihapus permanen.")
            .setPositiveButton("Hapus") { _, _ ->
                deleteBook(book)
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    /**
     * Mengambil data dari form, memvalidasi, membuat objek Book baru, dan menyimpannya ke API.
     */
    // Update fungsi saveNewBook untuk menyertakan gambar
    private fun saveNewBook() {
        // ... validasi input teks lama (title, author, stock, dll) ...
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val stock = etStock.text.toString().toIntOrNull() ?: 0

        // Validasi
        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(context, "Judul dan Penulis harus diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        // Buat request dengan gambar
        val request = CreateBookRequest(
            title = title,
            author = author,
            category = category.ifEmpty { "Uncategorized" },
            publisher = null,
            year = null,
            isbn = null,
            stock = stock,
            totalCopies = stock,
            description = "Deskripsi buku",
            // Masukkan string Base64 ke sini
            coverImage = selectedCoverBase64
        )

        lifecycleScope.launch {
            try {
                val response = ApiConfig.getApiService().createBook(request)
                if (response.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Buku '$title' berhasil ditambahkan!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val token = SharedPrefsManager(requireContext()).getUserToken()
                    if (token != null) {
                        eventViewModel.notifyNewBookAdded(token, title)
                    }

                    loadBooks()
                    resetForm()
                } else {
                    Toast.makeText(context, "Gagal menambahkan buku", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // ✅ INI YANG HILANG
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Mengatur ulang teks pada elemen form ke keadaan awal (untuk input buku baru).
     */
    private fun resetForm() {
        // 1. Reset Text Fields (Judul, Penulis, Kategori, Stok)
        etTitle.text?.clear()
        etAuthor.text?.clear()
        etCategory.text?.clear()
        etStock.text?.clear()

        // 2. Kembalikan Judul Header & Tombol ke default (jika sebelumnya berubah karena edit)
        formTitle.text = "Tambah Buku Baru"
        btnSaveBook.text = "SIMPAN BUKU BARU"

        // 3. Reset Gambar ke Placeholder & hapus data Base64
        ivBookCoverPreview.setImageResource(R.drawable.ic_book_placeholder)
        selectedCoverBase64 = null

        // 4. (Opsional) Tutup form setelah berhasil simpan (Sesuai request Anda dikomentari dulu)
        // formContainer.visibility = View.GONE
        // btnToggleForm.animate().rotation(0f).start()

        // 5. Tampilkan pesan
        Toast.makeText(context, "Form telah direset", Toast.LENGTH_SHORT).show()
    }

    /**
     * Menghapus buku dari API.
     * @param book Objek Book yang akan dihapus.
     */
    private fun deleteBook(book: Book) {
        lifecycleScope.launch {
            try {
                val response = ApiConfig.getApiService().deleteBook(book.id)
                if (response.isSuccessful) {
                    Toast.makeText(context, "'${book.title}' berhasil dihapus.", Toast.LENGTH_SHORT).show()
                    loadBooks() // Muat ulang daftar
                } else {
                    Toast.makeText(context, "Gagal menghapus buku: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}