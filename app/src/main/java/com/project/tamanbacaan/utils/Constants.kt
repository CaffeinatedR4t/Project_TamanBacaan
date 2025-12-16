package com.caffeinatedr4t.tamanbacaan.utils

object Constants {
    const val APP_NAME = "Taman Bacaan"
    const val DATABASE_VERSION = 1

    // Fragment Tags
    const val FRAGMENT_HOME = "fragment_home"
    const val FRAGMENT_SEARCH = "fragment_search"
    const val FRAGMENT_BOOKMARK = "fragment_bookmark"
    const val KEY_USER = "key_user"
    const val EXTRA_BOOK_ID = "book_id"

    // ========== ADD THESE NEW LINES ==========
    // API Configuration - For Android Emulator (API 33 Tiramisu)
    const val BASE_URL = "http://10.0.2.2:3000/api/"

    // SharedPreferences
    const val PREFS_NAME = "TamanBacaanPrefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_TOKEN = "user_token"
    const val KEY_USER_ROLE = "user_role"
    const val KEY_USER_NAME = "user_name"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    // ==========================================
}