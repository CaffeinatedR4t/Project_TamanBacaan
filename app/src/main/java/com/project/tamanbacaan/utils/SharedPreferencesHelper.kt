package com. caffeinatedr4t.tamanbacaan.utils

import android.content.Context
import android. content.SharedPreferences

class SharedPreferencesHelper(context: Context) {

    private val prefs: SharedPreferences = context. getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "TamanBacaanPrefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // Save login data
    fun saveLoginData(token: String, userId: String, userName: String, email: String, role: String) {
        prefs.edit(). apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_ROLE, role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Get token
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    // Get user ID
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    // Get user name
    fun getUserName(): String? {
        return prefs. getString(KEY_USER_NAME, null)
    }

    // Get user email
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    // Get user role
    fun getUserRole(): String? {
        return prefs. getString(KEY_USER_ROLE, null)
    }

    // Check if logged in
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Clear all data (logout)
    fun clearLoginData() {
        prefs. edit().clear().apply()
    }
}