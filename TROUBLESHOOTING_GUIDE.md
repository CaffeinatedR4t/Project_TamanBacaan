# Troubleshooting Guide - Mobile App Backend Connection

## Quick Diagnostics

Run these checks first to identify the issue:

### 1. Check Backend Status
```bash
# Is the backend running?
curl http://localhost:3000/api/health

# Expected response:
# {"status":"OK","timestamp":"2025-12-07T...","mongodb":"connected"}
```

### 2. Check Emulator Network
```bash
# Can emulator reach host machine?
adb shell ping -c 3 10.0.2.2

# Expected: ping responses with time < 100ms
```

### 3. Check Mobile App Logs
```bash
# View API connection logs
adb logcat | grep -E "ApiConfig|HTTP"

# Expected output:
# ApiConfig: API Config initialized
# ApiConfig: Base URL: http://10.0.2.2:3000/api/
# HTTP: → POST http://10.0.2.2:3000/api/auth/login
```

## Common Errors and Solutions

### Error 1: "Failed to connect" / Connection Timeout

**Error Message:**
```
Tidak dapat terhubung ke server. Pastikan backend berjalan dan URL sudah benar.
```

**Logcat Shows:**
```
java.net.ConnectException: Failed to connect to 10.0.2.2:3000
```

**Cause:** Backend server is not running or not accessible

**Solutions:**

1. **Start the backend:**
   ```bash
   cd /path/to/tamanbacaan_backend
   npm start
   ```
   
   Look for:
   ```
   🚀 Taman Bacaan Backend Server Started
   Server running on: http://localhost:3000
   ```

2. **Verify backend is listening on all interfaces:**
   
   Backend should bind to `0.0.0.0`, not just `localhost`:
   ```javascript
   app.listen(3000, '0.0.0.0', () => {
     console.log('Server running on port 3000');
   });
   ```

3. **Check firewall:**
   ```bash
   # Windows
   netsh advfirewall firewall add rule name="Node.js Server" dir=in action=allow protocol=TCP localport=3000
   
   # Linux
   sudo ufw allow 3000
   
   # macOS
   # System Preferences > Security > Firewall > Allow Node.js
   ```

4. **Verify port is not in use:**
   ```bash
   # Windows
   netstat -ano | findstr :3000
   
   # Linux/Mac
   lsof -i :3000
   ```

---

### Error 2: "UnknownHostException"

**Error Message:**
```
Tidak dapat terhubung ke server. Pastikan backend berjalan dan URL sudah benar.
```

**Logcat Shows:**
```
java.net.UnknownHostException: Unable to resolve host "10.0.2.2"
```

**Cause:** Network configuration issue or wrong URL

**Solutions:**

1. **For Android Emulator:**
   - Use `10.0.2.2` to access host machine
   - Verify in `ApiConfig.kt`:
   ```kotlin
   private const val DEV_HOST = "10.0.2.2:3000"
   ```

2. **For Physical Device:**
   - Use your computer's local IP address
   - Find your IP:
     ```bash
     # Windows
     ipconfig
     # Look for IPv4 Address (e.g., 192.168.1.100)
     
     # Linux/Mac
     ifconfig
     # or
     ip addr show
     ```
   - Update `ApiConfig.kt`:
   ```kotlin
   private const val DEV_HOST = "192.168.1.100:3000"
   ```

3. **Ensure device and computer are on the same network**

---

### Error 3: 404 Not Found

**Error Message:**
```
Akun tidak ditemukan
```

**Logcat Shows:**
```
HTTP: ← 404 http://10.0.2.2:3000/api/auth/login
```

**Backend Logs:**
```
404 - Route not found: POST /api/auth/login
```

**Cause:** API endpoint path mismatch

**Solutions:**

1. **Check BASE_URL in ApiConfig.kt:**
   ```kotlin
   // Should end with /api/
   private const val BASE_URL = "http://10.0.2.2:3000/api/"
   ```

2. **Check backend route definition:**
   ```javascript
   // Should NOT have duplicate /api/ prefix
   router.post('/auth/login', loginController);
   
   // Mount router with /api prefix
   app.use('/api', router);
   ```

3. **Verify endpoint in ApiService.kt:**
   ```kotlin
   // Should NOT start with /api/ (it's in BASE_URL)
   @POST("auth/login")
   fun login(@Body request: LoginRequest): Call<LoginResponse>
   ```

4. **Test endpoint manually:**
   ```bash
   curl -X POST http://localhost:3000/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"test@example.com","password":"test123"}'
   ```

---

### Error 4: 401 Unauthorized

**Error Message:**
```
Email atau password salah
```

**OR for authenticated requests:**
```
Token tidak valid atau sudah kadaluarsa
```

**Logcat Shows:**
```
HTTP: ← 401 http://10.0.2.2:3000/api/books
```

**Cause:** Invalid credentials or missing/expired token

**Solutions for Login 401:**

1. **Verify user exists in MongoDB:**
   ```javascript
   // In MongoDB Shell or Compass
   db.users.findOne({email: "admin@tbm.com"})
   ```

2. **Check password hashing matches:**
   - Backend should use bcrypt
   - Password in database should be hashed
   - Login should compare using bcrypt.compare()

3. **Test with known credentials:**
   - Default admin: `admin@tbm.com` / `admin123`

**Solutions for Authenticated Request 401:**

1. **Check token is saved:**
   ```kotlin
   // In LoginActivity after successful login
   sharedPrefs.saveLoginData(token = loginResponse.token, ...)
   ```

2. **Verify AuthInterceptor is working:**
   - Check logcat for Authorization header:
   ```
   HTTP: Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
   ```

3. **Check token format:**
   ```kotlin
   // Should be: "Bearer <token>"
   .header("Authorization", "Bearer $token")
   ```

4. **Verify JWT_SECRET matches:**
   - Backend `.env` file should have same secret used to sign tokens

---

### Error 5: 403 Forbidden

**Error Message:**
```
Akun Anda belum diverifikasi oleh admin
```

**Cause:** User account exists but not verified/active

**Solutions:**

1. **Check user status in MongoDB:**
   ```javascript
   db.users.findOne({email: "user@example.com"})
   // Check 'status' or 'isVerified' field
   ```

2. **Update user status:**
   ```javascript
   db.users.updateOne(
     {email: "user@example.com"},
     {$set: {status: "active", isVerified: true}}
   )
   ```

3. **Use admin account for testing:**
   - Admin accounts are usually pre-verified

---

### Error 6: Socket Timeout

**Error Message:**
```
Koneksi timeout. Server mungkin tidak merespons.
```

**Logcat Shows:**
```
java.net.SocketTimeoutException: timeout
```

**Cause:** Backend taking too long to respond

**Solutions:**

1. **Check backend is not hanging:**
   - Look for slow database queries
   - Check for infinite loops
   - Review error logs

2. **Increase timeout in ApiConfig.kt:**
   ```kotlin
   .connectTimeout(60, TimeUnit.SECONDS)  // Increase from 30
   .readTimeout(60, TimeUnit.SECONDS)
   .writeTimeout(60, TimeUnit.SECONDS)
   ```

3. **Check MongoDB connection:**
   ```javascript
   // Backend should log connection status
   mongoose.connection.on('connected', () => {
     console.log('MongoDB connected');
   });
   
   mongoose.connection.on('error', (err) => {
     console.error('MongoDB error:', err);
   });
   ```

---

### Error 7: No Data Returned / Empty Response

**Symptoms:**
- Login successful but no books show up
- API returns empty array `[]`

**Cause:** MongoDB collection is empty

**Solutions:**

1. **Check if data exists:**
   ```javascript
   // In MongoDB Shell
   db.books.count()
   db.books.find().limit(5)
   ```

2. **Seed database with test data:**
   ```bash
   # If backend has seed script
   npm run seed
   ```

3. **Create sample book manually:**
   ```javascript
   db.books.insertOne({
     title: "Test Book",
     author: "Test Author",
     isbn: "1234567890",
     category: "Fiction",
     isAvailable: true,
     coverImage: "https://example.com/cover.jpg",
     description: "A test book"
   })
   ```

---

### Error 8: CORS Error (if using browser or WebView)

**Symptoms:**
- Preflight OPTIONS requests failing
- CORS policy error

**Solutions:**

1. **Install and configure CORS in backend:**
   ```bash
   npm install cors
   ```

2. **Add to backend:**
   ```javascript
   const cors = require('cors');
   app.use(cors({
     origin: '*',
     methods: ['GET', 'POST', 'PUT', 'DELETE'],
     allowedHeaders: ['Content-Type', 'Authorization']
   }));
   ```

3. **Handle preflight:**
   ```javascript
   app.options('*', cors());
   ```

---

### Error 9: JSON Parsing Error

**Logcat Shows:**
```
com.google.gson.JsonSyntaxException: Expected BEGIN_OBJECT but was STRING
```

**Cause:** Backend returning HTML error page instead of JSON

**Solutions:**

1. **Check backend response format:**
   ```javascript
   // Always return JSON, even for errors
   res.status(404).json({
     success: false,
     message: "Not found"
   });
   
   // NOT:
   res.status(404).send("Not found");
   ```

2. **Add Content-Type header:**
   ```javascript
   res.setHeader('Content-Type', 'application/json');
   ```

3. **Check for uncaught exceptions:**
   ```javascript
   // Add global error handler
   app.use((err, req, res, next) => {
     res.status(500).json({
       success: false,
       message: err.message
     });
   });
   ```

---

## Advanced Debugging

### Enable Verbose Logging

1. **In ApiConfig.kt:**
   ```kotlin
   val loggingInterceptor = HttpLoggingInterceptor().apply {
       setLevel(HttpLoggingInterceptor.Level.BODY)
   }
   ```

2. **View detailed logs:**
   ```bash
   adb logcat -v time | grep -E "HTTP|OkHttp"
   ```

### Capture Network Traffic

**Using Charles Proxy or similar:**

1. Configure emulator proxy:
   ```bash
   adb shell settings put global http_proxy HOST_IP:8888
   ```

2. Install Charles certificate in emulator

3. View all HTTP traffic

### Test API Directly from Emulator

```bash
# Access emulator shell
adb shell

# Test API from inside emulator
curl -v http://10.0.2.2:3000/api/health

# Test login
curl -X POST http://10.0.2.2:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@tbm.com","password":"admin123"}'
```

### Check Actual HTTP Request

Add this to ApiConfig.kt for debugging:
```kotlin
.addInterceptor { chain ->
    val request = chain.request()
    
    // Log complete request
    println("========== REQUEST ==========")
    println("URL: ${request.url}")
    println("Method: ${request.method}")
    println("Headers: ${request.headers}")
    
    request.body?.let { body ->
        val buffer = okio.Buffer()
        body.writeTo(buffer)
        println("Body: ${buffer.readUtf8()}")
    }
    
    val response = chain.proceed(request)
    
    // Log complete response
    println("========== RESPONSE ==========")
    println("Code: ${response.code}")
    println("Headers: ${response.headers}")
    
    response
}
```

---

## Environment-Specific Issues

### Testing on Physical Device

1. **Find computer IP:**
   ```bash
   # Windows
   ipconfig
   # Linux/Mac  
   ifconfig
   ```

2. **Update ApiConfig.kt:**
   ```kotlin
   private const val DEV_HOST = "192.168.1.XXX:3000"
   ```

3. **Ensure same network:**
   - Computer and phone on same WiFi
   - No VPN active
   - No firewall blocking

### Production HTTPS Issues

1. **Enable HTTPS in ApiConfig.kt:**
   ```kotlin
   private const val USE_PRODUCTION = true
   ```

2. **Verify SSL certificate is valid:**
   ```bash
   curl -v https://api.tamanbacaan.com/api/health
   ```

3. **Check network security config:**
   - Ensure domain is in allowed list
   - Certificate pinning is correct (if used)

---

## Monitoring Tools

### Real-time Monitoring

**Backend:**
```bash
# Watch backend logs
tail -f logs/server.log

# Monitor MongoDB queries
mongo --eval "db.setProfilingLevel(2)"
```

**Mobile App:**
```bash
# Filtered logcat
adb logcat | grep -E "ApiConfig|HTTP|LoginActivity|HomeFragment"

# Save logs to file
adb logcat -d > app_logs.txt
```

### Performance Monitoring

```bash
# Check request timing
adb logcat | grep "HTTP:"

# Expected:
# HTTP: → POST http://10.0.2.2:3000/api/auth/login
# HTTP: ← 200 http://10.0.2.2:3000/api/auth/login (250ms)
```

---

## Prevention Checklist

Before deploying or testing:

- [ ] Backend server is running and accessible
- [ ] MongoDB is connected with test data
- [ ] Correct BASE_URL configured (10.0.2.2 for emulator)
- [ ] CORS enabled on backend
- [ ] Logging enabled for debugging
- [ ] Test user accounts exist in database
- [ ] Firewall allows port 3000
- [ ] Network permissions in AndroidManifest
- [ ] JWT_SECRET configured in backend .env
- [ ] API endpoints follow /api/* pattern

---

## Getting Help

### Information to Collect

When reporting issues, include:

1. **Mobile App Logs:**
   ```bash
   adb logcat -d > mobile_logs.txt
   ```

2. **Backend Logs:**
   ```bash
   # From backend console or log file
   ```

3. **Network Test:**
   ```bash
   adb shell curl -v http://10.0.2.2:3000/api/health
   ```

4. **Environment Info:**
   - Android version
   - Emulator or physical device
   - Backend version
   - MongoDB connection string (without password)

### Useful Commands Summary

```bash
# Backend
curl http://localhost:3000/api/health
npm start
netstat -an | grep 3000

# Mobile
adb logcat | grep HTTP
adb shell ping 10.0.2.2
adb shell curl http://10.0.2.2:3000/api/health

# MongoDB
mongo YOUR_URI --eval "db.books.count()"
```

---

**Last Updated:** December 7, 2025  
**Version:** 1.0  
**For:** Taman Bacaan Mobile App
