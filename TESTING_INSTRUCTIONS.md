# Backend Connection Testing Instructions

## Prerequisites

### 1. Backend Setup
Ensure the Node.js backend is running on your local machine:

```bash
cd backend  # Navigate to backend directory
npm install  # Install dependencies
npm start    # Start the server on port 3000
```

The backend should be accessible at `http://localhost:3000/`

### 2. MongoDB Connection
Verify MongoDB Atlas is connected in the backend console output. You should see:
```
MongoDB connected successfully
Server running on port 3000
```

### 3. Android Emulator Setup
- Launch Android Studio
- Start Pixel 6 API 33 (Tiramisu) emulator
- Wait for emulator to fully boot

## API Endpoints

The Android app is configured to use these endpoints:

- **BASE_URL**: `http://10.0.2.2:3000/api/`
  - `10.0.2.2` is Android emulator's special IP that maps to `localhost` on the host machine
  
- **Authentication**:
  - POST `/api/auth/login` - User login
  - POST `/api/auth/register` - New user registration

- **Books**:
  - GET `/api/books` - Get all books
  - GET `/api/books/{id}` - Get book by ID

## Manual Backend Testing

Before testing the Android app, verify the backend is working:

### 1. Test Server Health
```bash
curl http://localhost:3000/
```
Expected: JSON response confirming server is running

### 2. Test Login Endpoint
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@tbm.com","password":"admin123"}'
```
Expected: JSON with `token` and `user` object

### 3. Test Books Endpoint
```bash
curl http://localhost:3000/api/books
```
Expected: JSON array of books from MongoDB

## Android App Testing

### Test Scenario 1: User Registration

1. Launch the app on emulator
2. On login screen, click "Register" link
3. Fill in registration form:
   - Full Name: `Test User`
   - NIK: `1234567890123456` (16 digits)
   - Email: `test@example.com`
   - Password: `password123`
   - Address: `RT 001/RW 002, Kelurahan Test`
4. Click "Register" button
5. **Expected Results**:
   - Loading indicator appears
   - Success message: "Pendaftaran berhasil" or registration message from backend
   - Automatically navigates to Login screen with email pre-filled
   - Check Logcat for API request/response logs

### Test Scenario 2: User Login

1. On login screen, enter credentials:
   - Email: `admin@tbm.com`
   - Password: `admin123`
2. Click "Login" button
3. **Expected Results**:
   - Loading indicator appears
   - Success toast: "Login berhasil! Selamat datang..."
   - JWT token saved to SharedPreferences
   - Navigates to MainActivity
   - HomeFragment loads books from backend

### Test Scenario 3: Loading Books

1. After successful login, observe HomeFragment
2. **Expected Results**:
   - Loading indicator appears briefly
   - Books load from backend API
   - RecyclerView displays books with title, author, category
   - If API fails, fallback to local data with error toast
   - Check Logcat: "Loaded X books from API"

### Test Scenario 4: Error Handling

#### Invalid Login
1. Try logging in with wrong credentials:
   - Email: `wrong@test.com`
   - Password: `wrongpass`
2. **Expected**: Error toast: "Email atau password salah"

#### Network Error
1. Stop the backend server
2. Try to login or load books
3. **Expected**: 
   - Error message: "Kesalahan koneksi..."
   - HomeFragment shows fallback local data with toast

#### Invalid Registration
1. Try registering with invalid data:
   - NIK with less than 16 digits
   - Invalid email format
   - Password less than 6 characters
2. **Expected**: Validation error messages

## Debugging with Logcat

Monitor Android Studio Logcat for detailed information:

### Filter by Tags
- `LoginActivity` - Login flow logs
- `RegisterActivity` - Registration logs
- `HomeFragment` - Book loading logs
- `OkHttp` - Full HTTP request/response logs

### Key Log Messages
```
D/LoginActivity: Login request for: email@example.com
D/OkHttp: --> POST http://10.0.2.2:3000/api/auth/login
D/OkHttp: {"email":"email@example.com","password":"****"}
D/OkHttp: <-- 200 OK http://10.0.2.2:3000/api/auth/login
D/OkHttp: {"token":"jwt.token.here","user":{...}}
D/HomeFragment: Loaded 10 books from API
```

### Common Errors and Solutions

#### Error: "Connection refused"
- **Cause**: Backend not running or emulator can't reach it
- **Solution**: 
  - Verify backend is running on port 3000
  - Check firewall settings
  - For real device, use computer's IP instead of `10.0.2.2`

#### Error: "No address associated with hostname"
- **Cause**: DNS resolution issue
- **Solution**: Use IP address instead of hostname in BASE_URL

#### Error: 404 Not Found
- **Cause**: Incorrect endpoint URL
- **Solution**: Verify BASE_URL includes `/api/` suffix

#### Books show but are local data
- **Cause**: API call failed, fallback to local repository
- **Solution**: Check Logcat for API error, verify backend is accessible

## Verification Checklist

After testing, verify:

- [ ] Backend server is running and accessible
- [ ] MongoDB is connected and has book data
- [ ] Registration creates new user in database
- [ ] Login returns JWT token
- [ ] Token is saved in SharedPreferences
- [ ] Books load from MongoDB via API
- [ ] Proper error messages for invalid input
- [ ] Loading indicators work correctly
- [ ] Network errors handled gracefully
- [ ] Logcat shows full HTTP request/response details

## Real Device Testing

To test on a physical Android device:

1. **Get your computer's IP address**:
   - Windows: `ipconfig` → look for IPv4 Address
   - Mac/Linux: `ifconfig` → look for inet address
   - Example: `192.168.1.100`

2. **Update ApiConfig.kt**:
   ```kotlin
   private const val BASE_URL = "http://192.168.1.100:3000/api/"
   ```

3. **Ensure device is on same WiFi network** as your computer

4. **Update backend CORS settings** if needed to allow your device's IP

## Security Notes

The following are configured for development:

- HTTP (not HTTPS) for local testing
- `usesCleartextTraffic="true"` in AndroidManifest.xml
- Logging interceptor shows full request/response (including passwords)

**For production:**
- Use HTTPS
- Remove or disable logging interceptor
- Implement certificate pinning
- Use secure token storage (Android Keystore)
