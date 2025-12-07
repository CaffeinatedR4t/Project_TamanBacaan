# API Connection Setup Guide

## Overview

This guide explains how to configure the Taman Bacaan mobile app to connect to the backend API for development and production environments.

## Quick Start

### 1. Prerequisites

- ✅ Backend server running (tamanbacaan_backend repository)
- ✅ MongoDB Atlas connected and accessible
- ✅ Android Studio with emulator or physical device
- ✅ Node.js backend running on `localhost:3000`

### 2. Default Configuration (Emulator)

The app is pre-configured for Android Emulator:

```kotlin
// ApiConfig.kt
private const val DEV_HOST = "10.0.2.2:3000"
private const val USE_PRODUCTION = false
```

**No changes needed for emulator testing!**

### 3. Start Testing

1. Start backend:
   ```bash
   cd tamanbacaan_backend
   npm start
   ```

2. Launch Android app in emulator

3. Login with test credentials:
   - Email: `admin@tbm.com`
   - Password: `admin123`

---

## Configuration Options

### Environment Selection

In `ApiConfig.kt`, toggle between development and production:

```kotlin
// Development (HTTP, localhost)
private const val USE_PRODUCTION = false

// Production (HTTPS, live server)
private const val USE_PRODUCTION = true
```

### Network Logging

Logging is controlled via BuildConfig:

```kotlin
// Enabled in debug builds
if (BuildConfig.ENABLE_LOGGING) {
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
}
```

To disable logging in release:
- It's automatically disabled (set in `build.gradle.kts`)
- All HTTP requests/responses won't be logged

---

## Deployment Scenarios

### Scenario 1: Android Emulator (Default)

**Configuration:**
```kotlin
private const val DEV_HOST = "10.0.2.2:3000"
private const val USE_PRODUCTION = false
```

**Why 10.0.2.2?**
- Android emulator's special IP to access host machine's localhost
- Maps to `localhost:3000` on your development computer

**Test Connection:**
```bash
adb shell curl http://10.0.2.2:3000/api/health
```

---

### Scenario 2: Physical Android Device (Same Network)

**Configuration:**

1. Find your computer's IP address:
   ```bash
   # Windows
   ipconfig
   # Look for IPv4 Address (e.g., 192.168.1.100)
   
   # Mac/Linux
   ifconfig en0 | grep "inet "
   ```

2. Update `ApiConfig.kt`:
   ```kotlin
   private const val DEV_HOST = "192.168.1.100:3000"  // Your IP here
   private const val USE_PRODUCTION = false
   ```

3. Rebuild and install app on device

4. Ensure device and computer are on **same WiFi network**

**Test Connection:**
```bash
# From device browser, open:
http://192.168.1.100:3000/api/health
```

---

### Scenario 3: Production Deployment

**Configuration:**

1. Update production URL in `build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "BASE_URL_PROD", "\"https://api.tamanbacaan.com/api/\"")
   ```

2. Enable production in `ApiConfig.kt`:
   ```kotlin
   private const val USE_PRODUCTION = true
   ```

3. Update `AndroidManifest.xml`:
   ```xml
   <!-- Disable cleartext traffic for HTTPS only -->
   android:usesCleartextTraffic="false"
   ```

4. Build release APK:
   ```bash
   ./gradlew assembleRelease
   ```

---

## API Endpoints

### Authentication (No token required)

```kotlin
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": "...",
    "fullName": "John Doe",
    "email": "user@example.com",
    "role": "member"
  }
}
```

```kotlin
POST /api/auth/register
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "nik": "1234567890123456",
  "addressRtRw": "RT 01/RW 02",
  "addressKelurahan": "Kelurahan Name",
  "addressKecamatan": "Kecamatan Name",
  "phoneNumber": "081234567890",
  "isChild": false
}

Response:
{
  "message": "User registered successfully",
  "userId": "..."
}
```

### Books (Token required)

```kotlin
GET /api/books
Authorization: Bearer <token>

Response:
[
  {
    "_id": "...",
    "title": "Book Title",
    "author": "Author Name",
    "isbn": "1234567890",
    "category": "Fiction",
    "isAvailable": true,
    "coverImage": "https://...",
    "description": "Book description"
  }
]
```

```kotlin
GET /api/books/:id
Authorization: Bearer <token>

Response:
{
  "_id": "...",
  "title": "Book Title",
  ...
}
```

### Users (Token required, Admin only)

```kotlin
GET /api/users
Authorization: Bearer <token>

Response:
[
  {
    "_id": "...",
    "fullName": "User Name",
    "email": "user@example.com",
    "role": "member",
    ...
  }
]
```

### Events (Token required)

```kotlin
GET /api/events
Authorization: Bearer <token>

Response:
[
  {
    "_id": "...",
    "title": "Event Title",
    "description": "Event description",
    "date": "2025-12-25T10:00:00Z",
    ...
  }
]
```

---

## Network Security Configuration

The app includes a network security configuration for flexible HTTP/HTTPS support:

### Development Domains (HTTP Allowed)

- `10.0.2.2` - Android emulator localhost
- `localhost` - Direct localhost access
- `192.168.0.0/16` - Local network range

### Production Domains (HTTPS Only)

- `api.tamanbacaan.com`
- `tamanbacaan.com`

### Configuration File

Location: `app/src/main/res/xml/network_security_config.xml`

```xml
<network-security-config>
  <!-- Development: Allow HTTP -->
  <domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="true">10.0.2.2</domain>
    <domain includeSubdomains="true">localhost</domain>
  </domain-config>
  
  <!-- Production: HTTPS only -->
  <domain-config cleartextTrafficPermitted="false">
    <domain includeSubdomains="true">api.tamanbacaan.com</domain>
  </domain-config>
</network-security-config>
```

---

## Authentication Flow

### 1. Login Process

```
User enters credentials
    ↓
LoginActivity validates input
    ↓
POST /api/auth/login
    ↓
Backend validates credentials
    ↓
Returns JWT token + user data
    ↓
Token saved to SharedPreferences
    ↓
Navigate to MainActivity
```

### 2. Authenticated Requests

```
HomeFragment loads
    ↓
ApiConfig.getApiService(context) creates Retrofit instance
    ↓
AuthInterceptor adds "Authorization: Bearer <token>" header
    ↓
GET /api/books
    ↓
Backend validates JWT token
    ↓
Returns book data
    ↓
Display in RecyclerView
```

### 3. Token Management

**Token Storage:**
```kotlin
// Save token after login
sharedPrefs.saveLoginData(
    token = loginResponse.token,
    userId = loginResponse.user.id,
    userName = loginResponse.user.fullName,
    email = loginResponse.user.email,
    role = loginResponse.user.role
)
```

**Token Retrieval:**
```kotlin
// AuthInterceptor automatically adds token
val token = sharedPrefs.getToken()
request.newBuilder()
    .header("Authorization", "Bearer $token")
    .build()
```

**Token Invalidation:**
```kotlin
// Logout
sharedPrefs.clearLoginData()
// Redirects to LoginActivity
```

---

## Debugging

### View API Logs

**Android Studio Logcat:**
```
Filter: ApiConfig|HTTP|OkHttp
```

**Expected logs:**
```
ApiConfig: API Config initialized
ApiConfig: Environment: DEVELOPMENT
ApiConfig: Base URL: http://10.0.2.2:3000/api/
ApiConfig: Logging enabled: true

HTTP: → POST http://10.0.2.2:3000/api/auth/login
HTTP: {"email":"admin@tbm.com","password":"admin123"}
HTTP: ← 200 http://10.0.2.2:3000/api/auth/login
HTTP: {"token":"eyJ...","user":{...}}
```

### Test Backend Connectivity

```bash
# From command line
curl http://localhost:3000/api/health

# From emulator
adb shell curl http://10.0.2.2:3000/api/health

# Expected response
{"status":"OK","timestamp":"...","mongodb":"connected"}
```

### Common Issues

1. **Connection Failed**
   - Ensure backend is running: `npm start`
   - Check backend logs for startup messages
   - Verify firewall allows port 3000

2. **404 Not Found**
   - Verify BASE_URL ends with `/api/`
   - Check endpoint paths don't start with `/api/`
   - Confirm backend routes are registered

3. **401 Unauthorized**
   - For login: Check credentials in database
   - For other requests: Verify token is saved and valid
   - Check JWT_SECRET matches between app and backend

---

## Build Configuration

### Debug Build (Development)

```kotlin
// build.gradle.kts
debug {
    buildConfigField("String", "BASE_URL_DEV", "\"http://10.0.2.2:3000/api/\"")
    buildConfigField("String", "BASE_URL_PROD", "\"https://api.tamanbacaan.com/api/\"")
    buildConfigField("boolean", "ENABLE_LOGGING", "true")
}
```

**Features:**
- HTTP logging enabled
- Cleartext traffic allowed
- Debug symbols included

**Build command:**
```bash
./gradlew assembleDebug
```

### Release Build (Production)

```kotlin
// build.gradle.kts
release {
    buildConfigField("String", "BASE_URL_DEV", "\"http://10.0.2.2:3000/api/\"")
    buildConfigField("String", "BASE_URL_PROD", "\"https://api.tamanbacaan.com/api/\"")
    buildConfigField("boolean", "ENABLE_LOGGING", "false")
    isMinifyEnabled = false
}
```

**Features:**
- HTTP logging disabled
- HTTPS enforced (when USE_PRODUCTION = true)
- Optimized for performance

**Build command:**
```bash
./gradlew assembleRelease
```

---

## Security Considerations

### Development

✅ **Current Setup (Safe for Development):**
- HTTP allowed for localhost/emulator
- Full request/response logging
- Cleartext traffic permitted
- No certificate pinning

### Production

⚠️ **Required Changes:**
1. Set `USE_PRODUCTION = true`
2. Use HTTPS production URL
3. Disable cleartext traffic
4. Disable/reduce logging
5. Consider certificate pinning

**Certificate Pinning Example:**
```kotlin
val certificatePinner = CertificatePinner.Builder()
    .add("api.tamanbacaan.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    .build()

OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```

---

## Performance Optimization

### Timeouts

Current configuration:
```kotlin
.connectTimeout(30, TimeUnit.SECONDS)  // Initial connection
.readTimeout(30, TimeUnit.SECONDS)     // Read data
.writeTimeout(30, TimeUnit.SECONDS)    // Write data
```

Adjust based on network conditions and API response times.

### Caching

Currently not implemented. Consider adding:

```kotlin
// Cache configuration
val cacheSize = 10 * 1024 * 1024 // 10 MB
val cache = Cache(context.cacheDir, cacheSize.toLong())

OkHttpClient.Builder()
    .cache(cache)
    .build()
```

---

## Backend Requirements

For the mobile app to work correctly, the backend must:

1. ✅ Run on port 3000 (or update DEV_HOST)
2. ✅ Have `/api/` prefix for all endpoints
3. ✅ Return JSON responses
4. ✅ Include CORS headers
5. ✅ Use JWT for authentication
6. ✅ Handle errors with proper status codes

See `BACKEND_CONFIGURATION.md` for detailed backend setup.

---

## Testing Checklist

Before deploying:

- [ ] Backend server is running
- [ ] MongoDB is connected with test data
- [ ] Login works with test account
- [ ] Books load from API
- [ ] Token is saved after login
- [ ] Authenticated requests include token
- [ ] Error messages are user-friendly
- [ ] Logging shows correct URLs
- [ ] Network errors are handled gracefully
- [ ] Physical device can connect (if testing)

---

## Support

### Documentation

- `BACKEND_CONFIGURATION.md` - Backend setup guide
- `TROUBLESHOOTING_GUIDE.md` - Common issues and solutions
- `BACKEND_CONNECTION_FIX.md` - Implementation details
- `SECURITY_SUMMARY.md` - Security review

### Debug Commands

```bash
# View current configuration
adb logcat -s ApiConfig:* -d

# Test API from emulator
adb shell curl -v http://10.0.2.2:3000/api/health

# Monitor HTTP traffic
adb logcat | grep -E "HTTP:|→|←"
```

---

**Last Updated:** December 7, 2025  
**Version:** 1.0  
**Author:** GitHub Copilot Advanced Agent
