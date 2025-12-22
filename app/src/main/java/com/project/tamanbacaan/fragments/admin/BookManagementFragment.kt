package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Pastikan ada dependency fragment-ktx
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.AdminBookAdapter
import com.caffeinatedr4t.tamanbacaan.data.CreateBookRequest
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import com.caffeinatedr4t.tamanbacaan.state.BookManagementState
import com.caffeinatedr4t.tamanbacaan.viewmodels.BookManagementViewModel
import com.caffeinatedr4t.tamanbacaan.viewmodels.EventViewModel
import java.io.ByteArrayOutputStream
import kotlin.jvm.java

/**
 * Fragment untuk manajemen Buku (CRUD) oleh Admin dengan MVVM Pattern.
 */
class BookManagementFragment : Fragment() {

    // UI Components
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: AdminBookAdapter
    private val booksList = mutableListOf<Book>()

    // Form Components
    private lateinit var formTitle: TextView
    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etCategory: EditText
    private lateinit var etStock: EditText
    private lateinit var btnSaveBook: Button
    private lateinit var ivBookCoverPreview: ImageView
    private lateinit var btnPickGallery: Button
    private lateinit var btnOpenCamera: Button
    private lateinit var btnToggleForm: ImageButton
    private lateinit var formContainer: View

    // ViewModels
    // Menggunakan 'by viewModels()' untuk inisialisasi lazy
    private val viewModel: BookManagementViewModel by viewModels()
    private lateinit var eventViewModel: EventViewModel

    // Image Handling
    private var selectedCoverBase64: String? = null

    // Launchers
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { displayAndProcessImage(uriToBitmap(it)) }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let { displayAndProcessImage(it) }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) cameraLauncher.launch(null)
        else Toast.makeText(context, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
    }

    private val editBookLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.loadBooks() // Refresh list via ViewModel
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin_book_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupFormToggle()
        setupImagePickers()
        setupFormListeners()

        // Setup EventViewModel (untuk notifikasi FCM, terpisah dari CRUD buku)
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        // Observe ViewModel
        observeViewModel()

        // Load data awal
        viewModel.loadBooks()
    }

    private fun initViews(view: View) {
        btnToggleForm = view.findViewById(R.id.btnToggleForm)
        formContainer = view.findViewById(R.id.formContainer)
        formTitle = view.findViewById(R.id.formTitle)
        etTitle = view.findViewById(R.id.etTitle)
        etAuthor = view.findViewById(R.id.etAuthor)
        etCategory = view.findViewById(R.id.etCategory)
        etStock = view.findViewById(R.id.etStock)
        btnSaveBook = view.findViewById(R.id.btnSaveBook)
        recyclerView = view.findViewById(R.id.recyclerViewAdminBooks)
        ivBookCoverPreview = view.findViewById(R.id.ivBookCoverPreview)
        btnPickGallery = view.findViewById(R.id.btnPickGallery)
        btnOpenCamera = view.findViewById(R.id.btnOpenCamera)
    }

    private fun setupRecyclerView() {
        bookAdapter = AdminBookAdapter(
            books = booksList,
            onDeleteClick = { book -> showDeleteConfirmation(book) }
        )
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = bookAdapter
    }

    /**
     * Mengamati perubahan State dari BookManagementViewModel
     */
    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BookManagementState.Loading -> {
                    btnSaveBook.isEnabled = false
                    btnSaveBook.text = "Loading..."
                    // Opsional: Tampilkan ProgressBar global jika ada
                }
                is BookManagementState.SuccessLoad -> {
                    btnSaveBook.isEnabled = true
                    btnSaveBook.text = "SIMPAN BUKU BARU"

                    // Update List
                    booksList.clear()
                    booksList.addAll(state.books)
                    bookAdapter.notifyDataSetChanged()
                }
                is BookManagementState.SuccessOperation -> {
                    btnSaveBook.isEnabled = true
                    btnSaveBook.text = "SIMPAN BUKU BARU"

                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()

                    // Jika sukses tambah buku, reset form & kirim notifikasi
                    if (state.message.contains("ditambahkan", ignoreCase = true)) {
                        triggerNotificationIfAdded()
                        resetForm()
                    }
                    // Kita tidak perlu panggil loadBooks() disini karena di ViewModel sudah dipanggil otomatis
                }
                is BookManagementState.Error -> {
                    btnSaveBook.isEnabled = true
                    btnSaveBook.text = "SIMPAN BUKU BARU"
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                is BookManagementState.Idle -> {
                    // Do nothing
                }
            }
        }
    }

    private fun triggerNotificationIfAdded() {
        // Kirim notifikasi FCM (ini logika tambahan, jadi dipisah dari VM utama agar VM fokus ke data buku)
        val token = SharedPrefsManager(requireContext()).getUserToken()
        val title = etTitle.text.toString().trim() // Ambil judul dari form sebelum direset (atau simpan di var sementara)
        if (token != null && title.isNotEmpty()) {
            eventViewModel.notifyNewBookAdded(token, title)
        }
    }

    private fun setupFormToggle() {
        btnToggleForm.setOnClickListener {
            if (formContainer.visibility == View.VISIBLE) {
                formContainer.visibility = View.GONE
                btnToggleForm.animate().rotation(0f).setDuration(300).start()
            } else {
                formContainer.visibility = View.VISIBLE
                formContainer.alpha = 0f
                formContainer.animate().alpha(1f).setDuration(300).start()
                btnToggleForm.animate().rotation(45f).setInterpolator(OvershootInterpolator()).setDuration(300).start()
            }
        }
    }

    private fun setupImagePickers() {
        btnPickGallery.setOnClickListener { galleryLauncher.launch("image/*") }
        btnOpenCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(null)
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun setupFormListeners() {
        btnSaveBook.setOnClickListener {
            validateAndSaveBook()
        }
    }

    private fun validateAndSaveBook() {
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val stock = etStock.text.toString().toIntOrNull() ?: 0

        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(context, "Judul dan Penulis harus diisi.", Toast.LENGTH_SHORT).show()
            return
        }

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
            coverImage = selectedCoverBase64
        )

        // Panggil ViewModel untuk simpan
        viewModel.createBook(request)
    }

    private fun showDeleteConfirmation(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Buku")
            .setMessage("Apakah Anda yakin ingin menghapus '${book.title}'?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteBook(book.id)
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun resetForm() {
        etTitle.text?.clear()
        etAuthor.text?.clear()
        etCategory.text?.clear()
        etStock.text?.clear()
        formTitle.text = "Tambah Buku Baru"
        ivBookCoverPreview.setImageResource(R.drawable.ic_book_placeholder)
        selectedCoverBase64 = null
    }

    // --- Helper Functions for Image ---
    private fun displayAndProcessImage(bitmap: Bitmap?) {
        bitmap?.let {
            ivBookCoverPreview.setImageBitmap(it)
            selectedCoverBase64 = bitmapToBase64(it)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        return "data:image/jpeg;base64," + Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, uri))
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}