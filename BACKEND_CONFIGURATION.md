# Backend Configuration Guide

## Overview
This guide provides the necessary backend configuration for the Taman Bacaan mobile app to connect properly. The backend should be configured in the `tamanbacaan_backend` repository.

## Required Backend Configuration

### 1. CORS (Cross-Origin Resource Sharing)

The backend must allow requests from the mobile app. Add the following CORS configuration to your Express.js backend:

```javascript
// Install cors package if not already installed
// npm install cors

const cors = require('cors');

// CORS configuration
const corsOptions = {
  origin: '*', // For development; restrict in production
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
  allowedHeaders: [
    'Content-Type',
    'Authorization',
    'X-Requested-With',
    'Accept',
    'Origin'
  ],
  exposedHeaders: ['Authorization'],
  credentials: true,
  maxAge: 86400 // 24 hours
};

// Apply CORS middleware
app.use(cors(corsOptions));

// Handle preflight requests
app.options('*', cors(corsOptions));
```

### 2. Request Logging

Add comprehensive request logging to help debug mobile app connections:

```javascript
// Request logging middleware
app.use((req, res, next) => {
  const timestamp = new Date().toISOString();
  console.log(`[${timestamp}] ${req.method} ${req.url}`);
  console.log('Headers:', JSON.stringify(req.headers, null, 2));
  
  if (req.body && Object.keys(req.body).length > 0) {
    // Don't log passwords
    const sanitizedBody = { ...req.body };
    if (sanitizedBody.password) sanitizedBody.password = '[REDACTED]';
    console.log('Body:', JSON.stringify(sanitizedBody, null, 2));
  }
  
  next();
});

// Response logging
app.use((req, res, next) => {
  const originalSend = res.send;
  res.send = function(data) {
    console.log(`Response Status: ${res.statusCode}`);
    if (res.statusCode >= 400) {
      console.log('Error Response:', data);
    }
    originalSend.apply(res, arguments);
  };
  next();
});
```

### 3. Error Handling

Implement proper error handling for common scenarios:

```javascript
// 404 Handler - Not Found
app.use((req, res, next) => {
  console.log(`404 - Route not found: ${req.method} ${req.url}`);
  res.status(404).json({
    success: false,
    message: 'Endpoint tidak ditemukan',
    error: 'NOT_FOUND',
    path: req.url
  });
});

// 401 Handler - Authentication middleware
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN

  if (!token) {
    console.log('401 - No token provided');
    return res.status(401).json({
      success: false,
      message: 'Token autentikasi diperlukan',
      error: 'UNAUTHORIZED'
    });
  }

  jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
    if (err) {
      console.log('401 - Invalid token:', err.message);
      return res.status(401).json({
        success: false,
        message: 'Token tidak valid atau sudah kadaluarsa',
        error: 'INVALID_TOKEN'
      });
    }
    req.user = user;
    next();
  });
};

// Global error handler
app.use((err, req, res, next) => {
  console.error('Error occurred:', err);
  
  const statusCode = err.statusCode || 500;
  const message = err.message || 'Terjadi kesalahan pada server';
  
  res.status(statusCode).json({
    success: false,
    message: message,
    error: err.name || 'INTERNAL_ERROR',
    ...(process.env.NODE_ENV === 'development' && { stack: err.stack })
  });
});
```

### 4. API Endpoints Structure

Ensure all API endpoints follow this structure:

```
POST   /api/auth/register  - User registration (no auth)
POST   /api/auth/login     - User login (no auth)
GET    /api/books          - Get all books (requires auth)
GET    /api/books/:id      - Get book by ID (requires auth)
GET    /api/users          - Get all users (requires auth, admin only)
GET    /api/events         - Get events (requires auth)
POST   /api/books          - Create book (requires auth, admin only)
PUT    /api/books/:id      - Update book (requires auth, admin only)
DELETE /api/books/:id      - Delete book (requires auth, admin only)
```

### 5. MongoDB Connection

Verify MongoDB connection and log status:

```javascript
const mongoose = require('mongoose');

mongoose.connect(process.env.MONGODB_URI, {
  useNewUrlParser: true,
  useUnifiedTopology: true,
})
.then(() => {
  console.log('✅ MongoDB Connected Successfully');
  console.log('Database:', mongoose.connection.db.databaseName);
  console.log('Host:', mongoose.connection.host);
})
.catch((err) => {
  console.error('❌ MongoDB Connection Error:', err);
  process.exit(1);
});

// Log MongoDB queries in development
if (process.env.NODE_ENV === 'development') {
  mongoose.set('debug', true);
}
```

### 6. Server Startup Configuration

```javascript
const PORT = process.env.PORT || 3000;

const server = app.listen(PORT, '0.0.0.0', () => {
  console.log('='.repeat(50));
  console.log('🚀 Taman Bacaan Backend Server Started');
  console.log('='.repeat(50));
  console.log(`Environment: ${process.env.NODE_ENV || 'development'}`);
  console.log(`Server running on: http://localhost:${PORT}`);
  console.log(`API Base URL: http://localhost:${PORT}/api`);
  console.log(`MongoDB: ${mongoose.connection.readyState === 1 ? '✅ Connected' : '❌ Disconnected'}`);
  console.log('='.repeat(50));
  console.log('\nAvailable endpoints:');
  console.log('  POST   /api/auth/register');
  console.log('  POST   /api/auth/login');
  console.log('  GET    /api/books');
  console.log('  GET    /api/books/:id');
  console.log('  GET    /api/users');
  console.log('  GET    /api/events');
  console.log('='.repeat(50));
});

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('SIGTERM received, closing server gracefully...');
  server.close(() => {
    mongoose.connection.close(false, () => {
      console.log('MongoDB connection closed');
      process.exit(0);
    });
  });
});
```

### 7. Environment Variables (.env)

Create a `.env` file with the following variables:

```env
# Server Configuration
NODE_ENV=development
PORT=3000

# MongoDB Configuration
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/tamanbacaan?retryWrites=true&w=majority

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRES_IN=7d

# CORS Configuration
ALLOWED_ORIGINS=*

# Logging
LOG_LEVEL=debug
```

### 8. Health Check Endpoint

Add a health check endpoint for debugging:

```javascript
app.get('/api/health', (req, res) => {
  res.json({
    status: 'OK',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
    environment: process.env.NODE_ENV,
    mongodb: mongoose.connection.readyState === 1 ? 'connected' : 'disconnected',
    version: '1.0.0'
  });
});
```

## Testing Backend Connection

### From Command Line

```bash
# Test health endpoint
curl http://localhost:3000/api/health

# Test login endpoint
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@tbm.com","password":"admin123"}'

# Test books endpoint (requires token)
curl http://localhost:3000/api/books \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### From Android Emulator

The Android emulator accesses `localhost:3000` via the special IP `10.0.2.2:3000`.

Test from within the emulator:
```bash
adb shell
curl http://10.0.2.2:3000/api/health
```

## Common Issues and Solutions

### Issue 1: Mobile app can't connect to backend

**Symptoms:**
- Connection timeout errors
- "Failed to connect" messages
- `UnknownHostException`

**Solutions:**
1. Verify backend is running: `curl http://localhost:3000/api/health`
2. Check emulator can reach host: `adb shell ping 10.0.2.2`
3. Verify firewall isn't blocking port 3000
4. Check backend is bound to `0.0.0.0`, not just `localhost`

### Issue 2: 404 Not Found

**Symptoms:**
- API calls return 404
- Routes not found

**Solutions:**
1. Verify route paths include `/api/` prefix
2. Check backend logs for incoming requests
3. Ensure Express routes are registered before 404 handler
4. Verify HTTP method (GET, POST, etc.) matches

### Issue 3: 401 Unauthorized

**Symptoms:**
- Login works but other requests fail
- "Token required" messages

**Solutions:**
1. Verify JWT token is being saved in SharedPreferences
2. Check Authorization header format: `Bearer <token>`
3. Verify JWT_SECRET matches between mobile and backend
4. Check token hasn't expired

### Issue 4: CORS Errors

**Symptoms:**
- Preflight OPTIONS requests failing
- CORS policy errors in logs

**Solutions:**
1. Ensure CORS middleware is installed and configured
2. Check `Access-Control-Allow-Origin` header in response
3. Verify allowed methods include the one being used
4. Ensure CORS middleware is before route handlers

### Issue 5: MongoDB Connection Failed

**Symptoms:**
- Backend starts but database operations fail
- Connection timeout to MongoDB

**Solutions:**
1. Verify MongoDB URI in `.env` file
2. Check MongoDB Atlas IP whitelist (allow 0.0.0.0/0 for testing)
3. Verify network/internet connectivity
4. Check MongoDB cluster is running

## Monitoring and Debugging

### Backend Logs to Monitor

```javascript
// Startup logs
✅ MongoDB Connected Successfully
🚀 Taman Bacaan Backend Server Started
Server running on: http://localhost:3000

// Request logs
[2025-12-07T10:30:45.123Z] POST /api/auth/login
Headers: {"content-type": "application/json"}
Body: {"email": "user@example.com"}

// Response logs
Response Status: 200
← Successfully sent response

// Error logs
❌ Error: Invalid credentials
401 - Authentication failed
```

### Mobile App Logs to Monitor (Logcat)

```
ApiConfig: API Config initialized
ApiConfig: Environment: DEVELOPMENT
ApiConfig: Base URL: http://10.0.2.2:3000/api/
HTTP: → POST http://10.0.2.2:3000/api/auth/login
HTTP: {"email":"user@example.com","password":"******"}
HTTP: ← 200 http://10.0.2.2:3000/api/auth/login
HTTP: {"token":"eyJhbGc...","user":{...}}
LoginActivity: Login successful
```

## Production Deployment

### Backend Changes for Production

1. **Use HTTPS:**
```javascript
const https = require('https');
const fs = require('fs');

const options = {
  key: fs.readFileSync('path/to/private.key'),
  cert: fs.readFileSync('path/to/certificate.crt')
};

https.createServer(options, app).listen(443, () => {
  console.log('HTTPS Server running on port 443');
});
```

2. **Restrict CORS:**
```javascript
const corsOptions = {
  origin: ['https://tamanbacaan.com', 'https://www.tamanbacaan.com'],
  credentials: true
};
```

3. **Disable detailed logging:**
```javascript
if (process.env.NODE_ENV !== 'development') {
  // Minimal logging in production
  app.use(morgan('combined'));
} else {
  app.use(morgan('dev'));
}
```

4. **Enable helmet for security:**
```javascript
const helmet = require('helmet');
app.use(helmet());
```

5. **Rate limiting:**
```javascript
const rateLimit = require('express-rate-limit');

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100 // limit each IP to 100 requests per windowMs
});

app.use('/api/', limiter);
```

## Mobile App Changes for Production

1. In `ApiConfig.kt`, set:
```kotlin
private const val USE_PRODUCTION = true
```

2. Update `BASE_URL_PROD` in `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "BASE_URL_PROD", "\"https://api.tamanbacaan.com/api/\"")
```

3. In `AndroidManifest.xml`, set:
```xml
android:usesCleartextTraffic="false"
```

## Support and Troubleshooting

### Quick Checklist

- [ ] Backend server is running
- [ ] MongoDB is connected
- [ ] Port 3000 is accessible
- [ ] CORS is properly configured
- [ ] JWT_SECRET is set in .env
- [ ] API endpoints follow /api/* pattern
- [ ] Request/response logging is enabled
- [ ] Error handlers are in place
- [ ] Mobile app BASE_URL points to correct host
- [ ] Network permissions are granted in AndroidManifest

### Debug Commands

```bash
# Check if backend is running
netstat -an | grep 3000

# Test from emulator
adb shell curl http://10.0.2.2:3000/api/health

# View Android logs
adb logcat | grep -E "HTTP|ApiConfig|LoginActivity"

# Check MongoDB connection
mongo YOUR_MONGODB_URI --eval "db.stats()"
```

---

**Last Updated:** December 7, 2025  
**For:** Taman Bacaan Mobile App  
**Backend Repository:** tamanbacaan_backend
