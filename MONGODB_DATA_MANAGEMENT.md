# MongoDB Data Management Guide

Complete guide for viewing and managing data in the Taman Bacaan MongoDB database.

## Table of Contents
1. [Viewing Data in MongoDB Atlas Web](#1-viewing-data-in-mongodb-atlas-web)
2. [Using MongoDB Compass Desktop](#2-using-mongodb-compass-desktop)
3. [Backend API Testing](#3-backend-api-testing)
4. [Using the Data Seeder](#4-using-the-data-seeder)
5. [Manual Data Entry](#5-manual-data-entry)

---

## 1. Viewing Data in MongoDB Atlas Web

MongoDB Atlas provides a web interface to view and manage your database.

### Login Steps

1. **Navigate to MongoDB Atlas**
   - Go to [https://cloud.mongodb.com/](https://cloud.mongodb.com/)
   - Login with your credentials

2. **Access Your Cluster**
   - Click on "Browse Collections" on your cluster
   - You'll see your database (e.g., `tamanbacaan`)

3. **View Collections**
   - Click on your database name
   - Available collections:
     - `books` - All book records
     - `users` - User accounts and profiles
     - `events` - Announcements and events
     - `transactions` - Borrowing transactions

### Browse Collections

1. **Navigate to a Collection**
   - Click on any collection name (e.g., `books`)
   - View all documents in the collection

2. **Filter Data**
   ```json
   // Example: Find verified users
   { "isVerified": true }
   
   // Example: Find books by author
   { "author": "Andrea Hirata" }
   
   // Example: Find available books
   { "isAvailable": true }
   ```

3. **Search Data**
   - Use the search bar at the top
   - Enter field name and value
   - Click "Apply" to filter results

### Export Data

1. **Export to JSON**
   - Click the "Export Collection" button
   - Choose "Export query results to JSON"
   - Download the file

2. **Export to CSV**
   - Click the "Export Collection" button
   - Choose "Export collection to CSV"
   - Select fields to export
   - Download the file

---

## 2. Using MongoDB Compass Desktop

MongoDB Compass is a GUI tool for working with MongoDB databases.

### Download and Install

**Download Link:** [https://www.mongodb.com/try/download/compass](https://www.mongodb.com/try/download/compass)

- Available for Windows, macOS, and Linux
- Choose the stable version for your OS
- Install using the downloaded installer

### Connection String Format

Get your connection string from the backend `.env` file:

```
mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<database>?retryWrites=true&w=majority
```

**Example:**
```
mongodb+srv://admin:password123@cluster0.mongodb.net/tamanbacaan?retryWrites=true&w=majority
```

### Connect to Database

1. **Open MongoDB Compass**
2. **Paste Connection String**
   - Paste your connection string in the connection field
   - Click "Connect"

3. **Browse Database**
   - Click on your database name (`tamanbacaan`)
   - View all collections

### Visual Query Builder

1. **Filter Documents**
   ```json
   { "category": "Fiksi" }
   ```

2. **Project Fields** (select specific fields)
   ```json
   { "title": 1, "author": 1, "stock": 1 }
   ```

3. **Sort Results**
   - Click on column headers to sort
   - Ascending or descending order

### CRUD Operations

**Create (Insert)**
1. Click on a collection
2. Click "Insert Document" button
3. Paste JSON or use the editor
4. Click "Insert"

**Read (Query)**
1. Use the filter bar
2. Enter query in JSON format
3. Click "Find"

**Update**
1. Click on a document
2. Click "Edit" button
3. Modify fields
4. Click "Update"

**Delete**
1. Click on a document
2. Click "Delete" button
3. Confirm deletion

---

## 3. Backend API Testing

Test the backend API using curl commands or API testing tools.

### Available Endpoints

#### Authentication
```bash
# Register new user
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "nik": "1234567890123456",
    "addressRtRw": "RT 001/RW 002",
    "addressKelurahan": "Kelurahan A",
    "addressKecamatan": "Kecamatan B"
  }'

# Login
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@tbm.com",
    "password": "admin123"
  }'
```

#### Books
```bash
# Get all books
curl http://localhost:3000/api/books

# Get book by ID
curl http://localhost:3000/api/books/BOOK_ID

# Create new book (admin only)
curl -X POST http://localhost:3000/api/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Book Title",
    "author": "Author Name",
    "category": "Fiction",
    "publisher": "Publisher Name",
    "year": 2024,
    "isbn": "9781234567890",
    "stock": 5,
    "totalCopies": 5,
    "description": "Book description"
  }'

# Update book (admin only)
curl -X PUT http://localhost:3000/api/books/BOOK_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Updated Title",
    "stock": 3
  }'

# Delete book (admin only)
curl -X DELETE http://localhost:3000/api/books/BOOK_ID \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Users
```bash
# Get all users (admin only)
curl http://localhost:3000/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get user by ID (admin only)
curl http://localhost:3000/api/users/USER_ID \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Update user (admin only)
curl -X PUT http://localhost:3000/api/users/USER_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "isVerified": true
  }'
```

#### Events
```bash
# Get all events
curl http://localhost:3000/api/events

# Create event (admin only)
curl -X POST http://localhost:3000/api/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Event Title",
    "message": "Event description",
    "createdBy": "admin"
  }'

# Delete event (admin only)
curl -X DELETE http://localhost:3000/api/events/EVENT_ID \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Transactions
```bash
# Get all transactions (admin only)
curl http://localhost:3000/api/transactions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Create borrow request
curl -X POST http://localhost:3000/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "bookId": "BOOK_ID",
    "userId": "USER_ID",
    "borrowDate": "2024-12-06"
  }'

# Update transaction status (admin only)
curl -X PUT http://localhost:3000/api/transactions/TRANSACTION_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "status": "APPROVED"
  }'
```

### Using Postman/Insomnia

1. **Import Collection**
   - Create a new collection
   - Add requests for each endpoint

2. **Set Authorization**
   - Type: Bearer Token
   - Token: Paste your JWT token from login

3. **Save Environment Variables**
   ```
   BASE_URL = http://localhost:3000/api
   TOKEN = your_jwt_token_here
   ```

---

## 4. Using the Data Seeder

The app includes a built-in data seeder for testing purposes.

### Location

Admin Profile Fragment → "Tambah Data Testing" button

### Steps to Use

1. **Login as Admin**
   - Email: `admin@tbm.com`
   - Password: `admin123`

2. **Navigate to Admin Profile**
   - Click the profile icon in the top right
   - Or go to Admin Profile from the navigation

3. **Click "Tambah Data Testing"**
   - A confirmation dialog will appear

4. **Confirm Seeding**
   - Click "Ya" to start seeding
   - Progress dialog will show:
     - "Menambahkan 15 buku, 5 users, 3 events..."
     - Real-time progress updates

5. **Wait for Completion**
   - Process takes 10-30 seconds
   - Success message: "✅ Data berhasil ditambahkan ke MongoDB!"

### What Data is Created

**15 Books:**
- Indonesian classics (Laskar Pelangi, Bumi Manusia, etc.)
- International bestsellers (Harry Potter, 1984, etc.)
- Non-fiction (Sapiens, Atomic Habits, etc.)

**5 Users:**
- Verified members (Budi Santoso, Dewi Lestari)
- Unverified members (Siti Nurhaliza, Eko Prasetyo)
- Child user (Ahmad Rizki with parent)

**3 Events:**
- Lomba Baca Puisi Anak
- Pelatihan Literasi Digital
- Donasi Buku Gratis

### How to Clear Test Data

Use MongoDB Atlas or Compass to manually delete seeded data:

1. **In MongoDB Atlas:**
   - Go to Browse Collections
   - Click on a collection (e.g., `books`)
   - Select documents to delete
   - Click "Delete" button

2. **In MongoDB Compass:**
   - Select a collection
   - Use filter `{}` to show all documents
   - Select all documents (Ctrl+A)
   - Click "Delete Documents"

---

## 5. Manual Data Entry

### Using Postman/Insomnia

#### Create a Book

**Request:**
```
POST http://localhost:3000/api/books
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Body:**
```json
{
  "title": "The Hobbit",
  "author": "J.R.R. Tolkien",
  "category": "Fiksi",
  "publisher": "George Allen & Unwin",
  "year": 1937,
  "isbn": "9780547928227",
  "stock": 3,
  "totalCopies": 3,
  "coverImage": "https://example.com/hobbit-cover.jpg",
  "description": "A fantasy novel about Bilbo Baggins' adventure"
}
```

#### Create a User

**Request:**
```
POST http://localhost:3000/api/users
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Body:**
```json
{
  "fullName": "Jane Smith",
  "email": "jane.smith@example.com",
  "password": "password123",
  "nik": "3171010606950006",
  "addressRtRw": "RT 005/RW 007",
  "addressKelurahan": "Kelurahan Cikini",
  "addressKecamatan": "Kecamatan Menteng",
  "phoneNumber": "081234567895",
  "isChild": false,
  "isVerified": true
}
```

#### Create an Event

**Request:**
```
POST http://localhost:3000/api/events
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Body:**
```json
{
  "title": "Book Fair 2025",
  "message": "Annual book fair with discounted books and author meet & greet sessions. Don't miss it!",
  "createdBy": "admin"
}
```

### Field Requirements

#### Book Fields
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | String | Yes | Book title |
| author | String | Yes | Author name |
| category | String | Yes | Book category |
| publisher | String | No | Publisher name |
| year | Number | No | Publication year |
| isbn | String | No | ISBN number |
| stock | Number | Yes | Available copies |
| totalCopies | Number | Yes | Total copies owned |
| coverImage | String | No | Image URL |
| description | String | No | Book description |

#### User Fields
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| fullName | String | Yes | Full name |
| email | String | Yes | Email address |
| password | String | Yes | Password (min 6 chars) |
| nik | String | Yes | NIK (16 digits) |
| addressRtRw | String | Yes | RT/RW format |
| addressKelurahan | String | Yes | Kelurahan name |
| addressKecamatan | String | Yes | Kecamatan name |
| phoneNumber | String | No | Phone number |
| isChild | Boolean | No | Is child user |
| parentName | String | No | Parent name (if child) |
| isVerified | Boolean | No | Verification status |

#### Event Fields
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | String | Yes | Event title |
| message | String | Yes | Event description |
| createdBy | String | No | Creator username |

---

## Troubleshooting

### Common Issues

**1. Cannot connect to MongoDB**
- Check if backend server is running
- Verify connection string in `.env`
- Check internet connection

**2. Authentication errors**
- Ensure you're logged in as admin
- Check if JWT token is valid
- Token expires after 24 hours

**3. Seeder fails**
- Check backend server status
- Verify API endpoints are working
- Check MongoDB connection

**4. Duplicate key errors**
- Email or NIK already exists
- Use unique values for new users
- Delete existing data first

### Getting Help

- Check backend console logs
- Use MongoDB Atlas logs
- Review API error responses
- Contact: admin@tbm.com

---

## Best Practices

1. **Always backup before deleting data**
2. **Use test environment for experiments**
3. **Keep production credentials secure**
4. **Monitor database size regularly**
5. **Clean up old test data periodically**

---

## Additional Resources

- [MongoDB Atlas Documentation](https://docs.atlas.mongodb.com/)
- [MongoDB Compass Guide](https://docs.mongodb.com/compass/)
- [Postman Learning Center](https://learning.postman.com/)
- [Backend API Documentation](./BACKEND_CONNECTION_FIX.md)
