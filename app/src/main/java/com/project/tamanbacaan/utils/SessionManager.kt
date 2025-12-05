package com.caffeinatedr4t.tamanbacaan.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * SessionManager utility class for managing JWT tokens and user session data.
 * 
 * NOTE: This class provides an alternative API for session management but uses the same
 * SharedPreferences storage as SharedPreferencesHelper (key: "TamanBacaanPrefs").
 * Both classes can be used interchangeably throughout the app.
 * 
 * For consistency, it's recommended to use SharedPreferencesHelper which is already
 * used in LoginActivity and MainActivity. This class is provided as an additional
 * utility that matches the SessionManager API pattern commonly used in Android apps.
 */
class SessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "TamanBacaanPrefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    /**
     * Save authentication token to SharedPreferences
     */
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    /**
     * Get authentication token from SharedPreferences
     * @return JWT token or null if not found
     */
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Save complete user session data
     */
    fun saveUserSession(token: String, userId: String, userName: String, email: String, role: String) {
        prefs.edit().apply {
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_ROLE, role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Get user ID
     */
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    /**
     * Get user name
     */
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    /**
     * Get user email
     */
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    /**
     * Get user role
     */
    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Clear all session data (logout)
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
