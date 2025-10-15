package com.caffeinatedr4t.tamanbacaan.data

import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.models.EventNotification
import com.caffeinatedr4t.tamanbacaan.models.User
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Repository yang mensimulasikan database/API backend.
 * Data disimpan secara in-memory (hilang saat aplikasi ditutup).
 * Mendukung fungsi CRUD untuk manajemen buku dan anggota.
 */
object BookRepository {
    private val eventNotifications = mutableListOf<EventNotification>()
    private val nextEventId = AtomicLong(3) // Lanjutkan dari ID 2
    private val bookList = mutableListOf<Book>()
    private val pendingRequests = mutableListOf<PendingRequest>()
    // registrationRequests REMOVED (Diganti dengan aktivasi instan)
    private val activeMembers = mutableListOf<User>()

    private val nextBookId = AtomicLong(6) // Lanjutkan dari ID 5
    private val nextRequestId = AtomicLong(3) // Lanjutkan dari ID 2
    private val nextUserId = AtomicLong(103) // Lanjutkan ID anggota setelah M102

    init {
        // Data Sample untuk event
        eventNotifications.add(EventNotification("1", "Bedah Buku 'Laut Bercerita'", "Ikuti bedah buku bersama penulis Leila S. Chudori pada 10 Oktober 2025!", "05/10/2025"))
        eventNotifications.add(EventNotification("2", "Diskon Sewa Buku 50%", "Nikmati diskon 50% untuk semua kategori buku hingga 12 Oktober 2025!", "07/10/2025"))
        // Data Sample Buku
        bookList.add(Book(id = "1", title = "To Kill a Mockingbird", author = "Harper Lee", description = "A classic novel about racial injustice in the American South", coverUrl = "", category = "Fiction", isAvailable = true, isbn = "978-0-06-112008-4", avgRating = 4.5f, totalReviews = 120))
        bookList.add(Book(id = "2", title = "1984", author = "George Orwell", description = "A dystopian social science fiction novel", coverUrl = "", category = "Fiction", isAvailable = false, isbn = "978-0-452-28423-4", avgRating = 4.8f, totalReviews = 250))
        bookList.add(Book(id = "3", title = "The Great Gatsby", author = "F. Scott Fitzgerald", description = "The story of Jay Gatsby's pursuit of the American Dream", coverUrl = "", category = "Classic", isAvailable = true, isbn = "978-0-7432-7356-5", avgRating = 4.1f, totalReviews = 90))
        bookList.add(Book(id = "4", title = "Pride and Prejudice", author = "Jane Austen", description = "A romantic novel of manners", coverUrl = "", category = "Romance", isAvailable = true, isbn = "978-0-14-143951-8", avgRating = 4.3f, totalReviews = 150))
        bookList.add(Book(id = "5", title = "Atomic Habits", author = "James Clear", description = "Tiny changes, remarkable results.", coverUrl = "", category = "Self-Help", isBorrowed = true, isAvailable = false, borrowedDate = "01/10/2025", dueDate = "15/10/2025", avgRating = 4.7f, totalReviews = 300))

        // Data Sample Pending Request Pinjaman
        pendingRequests.add(PendingRequest(requestId = "1", book = bookList.first { it.id == "1" }.copy(isAvailable = false), memberName = "Budi Santoso", memberId = "M001", requestDate = "13/10/2025"))
        pendingRequests.add(PendingRequest(requestId = "2", book = bookList.first { it.id == "3" }.copy(isAvailable = true), memberName = "Siti Aisyah", memberId = "M002", requestDate = "13/10/2025"))

        // Data Anggota Aktif Default (isVerified disimulasikan)
        activeMembers.add(User(id="M100", fullName="Budi Santoso", email="user@test.com", nik="32xxxxxxxxxxxxxx", addressRtRw = "RT 005/RW 003, Kel. Demo", isChild = false, parentName = null, isVerified = true)) // Sudah diverifikasi
        activeMembers.add(User(id="M101", fullName="Siti Aisyah", email="siti@test.com", nik="32xxxxxxxxxxxxxy", addressRtRw = "RT 004/RW 003, Kel. Demo", isChild = false, parentName = null, isVerified = false)) // Belum diverifikasi
        activeMembers.add(User(id="M102", fullName="Daffa Permana", email="daffa@test.com", nik="32xxxxxxxxxxxxzz", addressRtRw = "RT 002/RW 001, Kel. Demo", isChild = true, parentName = "Ayah Daffa", isVerified = false)) // Belum diverifikasi
    }

    // Tambahkan fungsi-fungsi ini di dalam BookRepository
    fun getAllEvents(): List<EventNotification> = eventNotifications.toList()

    fun addEvent(title: String, message: String) {
        val newEvent = EventNotification(
            id = nextEventId.getAndIncrement().toString(),
            title = title,
            message = message,
            date = "15/10/2025" // Tanggal hari ini (simulasi)
        )
        eventNotifications.add(0, newEvent) // Tambah di paling atas
    }

    // --- Book CRUD ---
    fun addBook(newBook: Book): Boolean {
        val bookWithId = newBook.copy(id = nextBookId.getAndIncrement().toString());
        bookList.add(0, bookWithId);
        return true
    }
    fun getAllBooks(): List<Book> = bookList.toList()
    fun getBookById(id: String): Book? = bookList.find { it.id == id }
    fun updateBook(updatedBook: Book): Boolean {
        val index = bookList.indexOfFirst { it.id == updatedBook.id };
        return if (index != -1) { bookList[index] = updatedBook; true } else { false }
    }
    fun deleteBook(id: String): Boolean {
        val iterator = bookList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().id == id) { iterator.remove(); return true }
        };
        return false
    }

    // --- Transaction Request Management (Tetap) ---
    fun addPendingRequest(book: Book, memberName: String, memberId: String): Boolean {
        if (book.isBorrowed || pendingRequests.any { it.book.id == book.id }) { return false }
        val bookIndex = bookList.indexOfFirst { it.id == book.id }
        if (bookIndex != -1) { bookList[bookIndex] = bookList[bookIndex].copy(isAvailable = false) }
        val request = PendingRequest(requestId = nextRequestId.getAndIncrement().toString(), book = book, memberName = memberName, memberId = memberId, requestDate = "Hari Ini")
        pendingRequests.add(request)
        return true
    }
    fun getPendingRequests(): List<PendingRequest> = pendingRequests.toList()
    fun approveRequest(requestId: String): Boolean {
        val request = pendingRequests.find { it.requestId == requestId } ?: return false
        val bookIndex = bookList.indexOfFirst { it.id == request.book.id }

        if (bookIndex != -1) {
            // 1. Dapatkan tanggal hari ini
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val borrowedDate = dateFormat.format(calendar.time)

            // 2. Tambahkan 14 hari untuk jatuh tempo
            calendar.add(Calendar.DAY_OF_YEAR, 14)
            val dueDate = dateFormat.format(calendar.time)

            // 3. Salin objek buku dengan menambahkan tanggal
            val approvedBook = bookList[bookIndex].copy(
                isAvailable = false,
                isBorrowed = true,
                borrowedDate = borrowedDate, // Set tanggal pinjam
                dueDate = dueDate             // Set tanggal jatuh tempo
            )
            bookList[bookIndex] = approvedBook
        }

        // Hapus dari daftar permintaan
        val iterator = pendingRequests.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().requestId == requestId) {
                iterator.remove()
                return true
            }
        }
        return false
    }
    fun rejectRequest(requestId: String): Boolean {
        val request = pendingRequests.find { it.requestId == requestId } ?: return false;
        val bookIndex = bookList.indexOfFirst { it.id == request.book.id };
        if (bookIndex != -1) { bookList[bookIndex] = bookList[bookIndex].copy(isAvailable = true) };
        val iterator = pendingRequests.iterator();
        while (iterator.hasNext()) { if (iterator.next().requestId == requestId) { iterator.remove(); return true } };
        return false
    }

    // --- Registration Management (UPDATED: Instant Activation) ---

    /**
     * Mendaftarkan anggota baru dan langsung mengaktifkannya (Req: Registrasi -> Langsung Login).
     * Anggota baru memiliki status isVerified = false.
     */
    fun registerNewMember(
        fullName: String,
        nik: String,
        email: String,
        addressRtRw: String,
        isChild: Boolean,
        parentName: String?
    ): User? {
        // Cek duplikasi NIK/Email (simulasi)
        if (activeMembers.any { it.email == email || it.nik == nik }) {
            return null
        }

        val newUser = User(
            id = "M${nextUserId.getAndIncrement()}",
            fullName = fullName,
            email = email,
            nik = nik,
            addressRtRw = addressRtRw,
            isChild = isChild,
            parentName = parentName,
            status = "Active",
            isVerified = false // Anggota baru selalu belum diverifikasi
        )
        activeMembers.add(newUser)
        return newUser
    }

    // --- Member Management (CRUD + Verification Status) ---
    fun getAllMembers(): List<User> = activeMembers.toList()

    /**
     * Fungsi yang digunakan Admin untuk memverifikasi status RT/RW (Verifikasi Warga).
     */
    fun toggleVerificationStatus(userId: String): Boolean {
        val index = activeMembers.indexOfFirst { it.id == userId }
        return if (index != -1) {
            val user = activeMembers[index]
            activeMembers[index] = user.copy(isVerified = !user.isVerified)
            true
        } else {
            false
        }
    }

    fun updateMember(updatedUser: User): Boolean {
        val index = activeMembers.indexOfFirst { it.id == updatedUser.id };
        return if (index != -1) { activeMembers[index] = updatedUser; true } else { false }
    }

    fun deleteMember(id: String): Boolean {
        val iterator = activeMembers.iterator();
        while (iterator.hasNext()) { if (iterator.next().id == id) { iterator.remove(); return true } };
        return false
    }

    // --- Admin Data (Tetap) ---
    fun getTopBooks(): Map<String, Int> { return mapOf("To Kill a Mockingbird" to 45, "1984" to 38, "The Great Gatsby" to 32, "Atomic Habits" to 25, "Pride and Prejudice" to 19) }
    fun findMemberByNik(nik: String): User? { return activeMembers.find { it.nik == nik } }
}