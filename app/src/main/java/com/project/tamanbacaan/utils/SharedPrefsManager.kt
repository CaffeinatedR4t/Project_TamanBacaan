package com.caffeinatedr4t.tamanbacaan.utils

import android.content. Context
import android.content.SharedPreferences
import com.caffeinatedr4t.tamanbacaan.models.User
import com.caffeinatedr4t. tamanbacaan.utils.Constants.KEY_USER
import com. google.gson.Gson

class SharedPrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserSession(user: User, token: String) {
        prefs.edit().apply {
            putString(Constants.KEY_USER, Gson().toJson(user))
            putString(Constants.KEY_USER_ID, user.id)  // ✅ SAVE USER ID
            putString(Constants.KEY_USER_ROLE, user.role)  // ✅ SAVE USER ROLE
            putString(Constants.KEY_USER_TOKEN, token)
            putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUser(): User? {
        val json = prefs.getString(KEY_USER, null)
        return if (json != null) {
            Gson().fromJson(json, User::class.java)
        } else {
            null
        }
    }

    fun getUserRole(): String? {
        // Try direct storage first
        val directRole = prefs.getString(Constants. KEY_USER_ROLE, null)
        if (directRole != null) return directRole

        // Fallback to User object
        return getUser()?.role
    }

    fun getUserId(): String? {
        // Try direct storage first
        val directId = prefs.getString(Constants.KEY_USER_ID, null)
        if (directId != null) return directId

        // Fallback to User object
        return getUser()?.id
    }

    fun getUserToken(): String? {
        return prefs.getString(Constants.KEY_USER_TOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(Constants. KEY_IS_LOGGED_IN, false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}