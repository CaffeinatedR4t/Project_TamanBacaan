# MongoDB Data Management Guide

Panduan lengkap untuk melihat dan mengelola data di MongoDB untuk aplikasi Taman Bacaan.

## Daftar Isi
1. [Menggunakan Fitur Seed Data di Aplikasi](#1-menggunakan-fitur-seed-data-di-aplikasi)
2. [Melihat Data di MongoDB Compass](#2-melihat-data-di-mongodb-compass)
3. [Melihat Data di MongoDB Atlas Web Interface](#3-melihat-data-di-mongodb-atlas-web-interface)
4. [Menambah Data Manual via MongoDB Shell](#4-menambah-data-manual-via-mongodb-shell)
5. [Troubleshooting](#5-troubleshooting)

---

## 1. Menggunakan Fitur Seed Data di Aplikasi

### Cara Menggunakan
Aplikasi Android Taman Bacaan telah dilengkapi dengan fitur **seed data** untuk menambahkan data testing secara otomatis.

#### Langkah-langkah:
1. Login sebagai **Admin** dengan kredensial:
   - Email: `admin@tbm.com`
   - Password: `admin123`

2. Dari bottom navigation, klik icon **Profile** (icon settings di kanan bawah)

3. Di halaman Admin Profile, klik tombol **"Tambah Data Testing"**

4. Konfirmasi dialog yang muncul dengan klik **"Ya"**

5. Tunggu proses seeding (sekitar 10-30 detik tergantung koneksi)

6. Dialog hasil akan muncul menampilkan:
   - Jumlah buku berhasil ditambahkan
   - Jumlah user berhasil ditambahkan
   - Jumlah event berhasil ditambahkan

### Data yang Ditambahkan
Fitur seed data akan menambahkan:

#### **15 Buku Sample:**
1. Laskar Pelangi - Andrea Hirata (Fiksi)
2. Bumi Manusia - Pramoedya Ananta Toer (Sejarah)
3. Sapiens - Yuval Noah Harari (Non-Fiksi)
4. Harry Potter and the Sorcerer's Stone - J.K. Rowling (Fantasy)
5. To Kill a Mockingbird - Harper Lee (Klasik)
6. 1984 - George Orwell (Dystopia)
7. The Hobbit - J.R.R. Tolkien (Fantasy)
8. Atomic Habits - James Clear (Self-Help)
9. Educated - Tara Westover (Memoir)
10. The Da Vinci Code - Dan Brown (Mystery)
11. Negeri 5 Menara - Ahmad Fuadi (Fiksi)
12. Ronggeng Dukuh Paruk - Ahmad Tohari (Fiksi)
13. Cantik Itu Luka - Eka Kurniawan (Fiksi)
14. Perahu Kertas - Dee Lestari (Romance)
15. 5 cm - Donny Dhirgantoro (Petualangan)

#### **5 User Sample:**
1. Budi Santoso - RT 001/RW 005, Kelurahan Cibubur
2. Siti Aminah - RT 002/RW 005, Kelurahan Cibubur
3. Ahmad Hidayat - RT 003/RW 005, Kelurahan Cibubur
4. Dewi Lestari - RT 001/RW 006, Kelurahan Cibubur
5. Andi Wijaya (Child) - RT 001/RW 006, Parent: Dewi Lestari

#### **5 Event Sample:**
1. Donasi Buku Bulan Ini
2. Lomba Menulis Cerpen
3. Baca Bersama Setiap Sabtu
4. Perpanjangan Batas Waktu Pengembalian
5. Pengadaan Buku Baru

### Catatan Penting
- **Duplikasi Data**: Jika data dengan email/NIK yang sama sudah ada, akan muncul error. Ini normal.
- **Koneksi Internet**: Pastikan backend server berjalan dan aplikasi terhubung ke internet
- **Login Required**: Hanya admin yang dapat mengakses fitur ini

---

## 2. Melihat Data di MongoDB Compass

MongoDB Compass adalah GUI desktop untuk MongoDB yang memudahkan visualisasi dan manipulasi data.

### Instalasi MongoDB Compass
1. Download dari: https://www.mongodb.com/try/download/compass
2. Install sesuai sistem operasi Anda
3. Buka aplikasi MongoDB Compass

### Koneksi ke Database

#### Untuk MongoDB Atlas (Cloud):
1. Buka MongoDB Compass
2. Klik **"New Connection"**
3. Masukkan connection string:
   ```
   mongodb+srv://username:password@cluster.mongodb.net/tamanbacaan
   ```
   (Ganti `username`, `password`, dan `cluster` sesuai kredensial Anda)
4. Klik **"Connect"**

#### Untuk MongoDB Local:
1. Buka MongoDB Compass
2. Connection string default:
   ```
   mongodb://localhost:27017
   ```
3. Klik **"Connect"**

### Melihat Data

#### 1. Pilih Database
- Klik database **"tamanbacaan"** dari sidebar kiri

#### 2. Pilih Collection
Akan ada beberapa collection:
- **books** - Data buku
- **users** - Data pengguna
- **events** - Data pengumuman
- **borrowings** - Data peminjaman (jika ada)

#### 3. View Documents
- Klik collection yang ingin dilihat
- Tab **"Documents"** menampilkan semua data
- Gunakan **filter** untuk mencari data spesifik:
  ```json
  { "category": "Fiksi" }
  { "email": "budi.santoso@test.com" }
  ```

#### 4. Export Data
- Klik **"Export Data"** di toolbar
- Pilih format: JSON atau CSV
- Simpan file

### Fitur Lainnya
- **Schema**: Lihat struktur data
- **Explain Plan**: Analisis query performance
- **Indexes**: Lihat dan manage indexes
- **Validation**: Atur validation rules

---

## 3. Melihat Data di MongoDB Atlas Web Interface

MongoDB Atlas adalah cloud database service dari MongoDB dengan web interface yang powerful.

### Login ke MongoDB Atlas
1. Buka https://cloud.mongodb.com
2. Login dengan akun Anda
3. Pilih cluster yang digunakan

### Navigate ke Data

#### 1. Browse Collections
- Klik **"Browse Collections"** di cluster Anda
- Atau klik **"Collections"** di sidebar

#### 2. Pilih Database & Collection
- Database: `tamanbacaan`
- Collection: `books`, `users`, atau `events`

#### 3. View & Filter Documents
- **View Documents**: Semua data ditampilkan dalam list
- **Filter**: Gunakan MongoDB query
  ```javascript
  { category: "Fiksi" }
  { isChild: true }
  ```
- **Sort**: Klik header kolom untuk sort
- **Search**: Gunakan search box untuk text search

### CRUD Operations via Web Interface

#### Create Document
1. Klik **"Insert Document"**
2. Tulis JSON document:
   ```json
   {
     "title": "Buku Baru",
     "author": "Penulis",
     "category": "Fiksi",
     "stock": 5
   }
   ```
3. Klik **"Insert"**

#### Update Document
1. Hover document yang ingin diubah
2. Klik icon **pencil** (Edit)
3. Ubah field yang diinginkan
4. Klik **"Update"**

#### Delete Document
1. Hover document yang ingin dihapus
2. Klik icon **trash** (Delete)
3. Konfirmasi deletion

### Monitoring & Analytics
- **Metrics**: Real-time database performance
- **Real-time Performance Panel**: Query analysis
- **Charts**: Visualisasi data dengan MongoDB Charts

---

## 4. Menambah Data Manual via MongoDB Shell

MongoDB Shell (mongosh) adalah command-line interface untuk MongoDB.

### Instalasi MongoDB Shell
1. Download dari: https://www.mongodb.com/try/download/shell
2. Install dan tambahkan ke PATH
3. Buka terminal/command prompt

### Koneksi ke Database

#### MongoDB Atlas:
```bash
mongosh "mongodb+srv://cluster.mongodb.net/tamanbacaan" --username your-username
```

#### MongoDB Local:
```bash
mongosh
use tamanbacaan
```

### Menambah Data

#### 1. Menambah Buku
```javascript
db.books.insertOne({
  title: "Judul Buku",
  author: "Nama Penulis",
  category: "Fiksi",
  publisher: "Penerbit",
  year: 2024,
  isbn: "9781234567890",
  stock: 3,
  totalCopies: 3,
  description: "Deskripsi buku",
  coverImage: "",
  isAvailable: true,
  createdAt: new Date()
})
```

#### 2. Menambah User
```javascript
db.users.insertOne({
  fullName: "Nama Lengkap",
  email: "email@example.com",
  password: "$2b$10$hashedpassword", // Harus di-hash
  nik: "1234567890123456",
  addressRtRw: "RT 001/RW 005",
  addressKelurahan: "Kelurahan",
  addressKecamatan: "Kecamatan",
  phoneNumber: "081234567890",
  role: "member",
  isChild: false,
  parentName: null,
  isVerified: false,
  createdAt: new Date()
})
```

**⚠️ Catatan**: Password harus di-hash menggunakan bcrypt. Untuk testing, gunakan fitur seed data atau register via aplikasi.

#### 3. Menambah Event
```javascript
db.events.insertOne({
  title: "Judul Event",
  message: "Pesan pengumuman",
  createdBy: "Admin TBM",
  isActive: true,
  createdAt: new Date()
})
```

### Bulk Insert (Multiple Documents)
```javascript
db.books.insertMany([
  {
    title: "Buku 1",
    author: "Penulis 1",
    category: "Fiksi",
    stock: 2
  },
  {
    title: "Buku 2",
    author: "Penulis 2",
    category: "Non-Fiksi",
    stock: 3
  }
])
```

### Query Data
```javascript
// Semua buku
db.books.find()

// Filter by category
db.books.find({ category: "Fiksi" })

// Count documents
db.books.countDocuments()

// Find one
db.books.findOne({ title: "Laskar Pelangi" })
```

### Update Data
```javascript
// Update one
db.books.updateOne(
  { title: "Laskar Pelangi" },
  { $set: { stock: 5 } }
)

// Update many
db.books.updateMany(
  { category: "Fiksi" },
  { $inc: { stock: 1 } }
)
```

### Delete Data
```javascript
// Delete one
db.books.deleteOne({ title: "Judul Buku" })

// Delete many
db.books.deleteMany({ category: "Kategori Lama" })

// Clear entire collection (HATI-HATI!)
db.books.deleteMany({})
```

---

## 5. Troubleshooting

### Masalah Umum & Solusi

#### 1. Seed Data Gagal - "Connection Error"
**Penyebab:**
- Backend server tidak berjalan
- Aplikasi tidak terhubung ke internet

**Solusi:**
1. Pastikan backend server running:
   ```bash
   cd backend
   npm start
   ```
2. Cek koneksi internet device/emulator
3. Verifikasi BASE_URL di `ApiConfig.kt` sesuai dengan server Anda

#### 2. Seed Data Gagal - "Duplicate Key Error"
**Penyebab:**
- Data dengan email/NIK yang sama sudah ada di database

**Solusi:**
- Ini normal jika Anda seed data lebih dari sekali
- Hapus data lama via MongoDB Compass/Atlas
- Atau gunakan email/NIK berbeda

#### 3. MongoDB Compass Tidak Bisa Connect
**Penyebab:**
- Connection string salah
- Database tidak accessible

**Solusi:**
1. Verifikasi connection string
2. Untuk Atlas: Pastikan IP Anda di-whitelist di Atlas Network Access
3. Untuk local: Pastikan MongoDB service running

#### 4. Data Tidak Muncul di Aplikasi
**Penyebab:**
- API endpoint tidak correct
- Authentication token expired
- Backend tidak fetch dari database

**Solusi:**
1. Cek Logcat untuk error messages
2. Verify endpoint di `ApiService.kt`
3. Re-login ke aplikasi untuk refresh token
4. Cek backend console untuk errors

#### 5. "Authentication Failed" di MongoDB Shell
**Penyebab:**
- Username/password salah
- User tidak memiliki permission

**Solusi:**
1. Verifikasi credentials
2. Di Atlas: Cek Database Access settings
3. Pastikan user memiliki read/write permission

---

## Best Practices

### 1. Testing & Development
- Gunakan **seed data** untuk testing awal
- Backup data production sebelum testing
- Gunakan database terpisah untuk development

### 2. Security
- Jangan commit connection strings ke git
- Gunakan environment variables untuk credentials
- Restrict database access by IP (MongoDB Atlas)

### 3. Data Management
- Regular backup data penting
- Monitor database size dan performance
- Clean up test data secara berkala

### 4. Performance
- Create indexes untuk query yang sering digunakan
- Limit result size dengan pagination
- Avoid fetching unnecessary fields

---

## Useful MongoDB Queries

### Statistics
```javascript
// Total books
db.books.countDocuments()

// Books by category
db.books.aggregate([
  { $group: { _id: "$category", count: { $sum: 1 } } }
])

// Users by verification status
db.users.aggregate([
  { $group: { _id: "$isVerified", count: { $sum: 1 } } }
])
```

### Data Cleanup
```javascript
// Remove duplicate users by email
db.users.aggregate([
  { $group: { _id: "$email", count: { $sum: 1 }, docs: { $push: "$_id" } } },
  { $match: { count: { $gt: 1 } } }
])

// Find books with low stock
db.books.find({ stock: { $lt: 2 } })

// Inactive events (older than 30 days)
const thirtyDaysAgo = new Date(Date.now() - 30*24*60*60*1000);
db.events.find({ createdAt: { $lt: thirtyDaysAgo } })
```

---

## Resources

### Official Documentation
- MongoDB Manual: https://docs.mongodb.com/manual/
- MongoDB Compass: https://docs.mongodb.com/compass/
- MongoDB Atlas: https://docs.atlas.mongodb.com/
- MongoDB Shell: https://docs.mongodb.com/mongodb-shell/

### Tutorials
- MongoDB University: https://university.mongodb.com/
- MongoDB Blog: https://www.mongodb.com/blog

### Support
- MongoDB Community Forums: https://www.mongodb.com/community/forums
- Stack Overflow: https://stackoverflow.com/questions/tagged/mongodb

---

## Kesimpulan

Dokumen ini memberikan panduan lengkap untuk mengelola data di MongoDB untuk aplikasi Taman Bacaan. Mulai dari menggunakan fitur seed data di aplikasi, melihat data via MongoDB Compass dan Atlas, hingga manipulasi data via MongoDB Shell.

Untuk pertanyaan atau issue, silakan hubungi tim development atau buka issue di repository GitHub.

---

**Last Updated:** December 2024  
**Version:** 1.0
