package com.caffeinatedr4t.tamanbacaan.utils

import android.content.Context
import android.content.SharedPreferences
import com.caffeinatedr4t.tamanbacaan.models.User

class SharedPrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserSession(user: User, token: String) {
        prefs.edit().apply {
            putString(Constants.KEY_USER_ID, user.id)
            putString(Constants.KEY_USER_TOKEN, token)
            putString(Constants.KEY_USER_ROLE, user.role)
            putString(Constants.KEY_USER_NAME, user.fullName)
            putBoolean(Constants. KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUserRole(): String? {
        return prefs.getString(Constants.KEY_USER_ROLE, null)
    }

    fun getUserId(): String? {
        return prefs.getString(Constants.KEY_USER_ID, null)
    }

    fun getUserToken(): String? {
        return prefs.getString(Constants.KEY_USER_TOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}