package com.example.apz.ktor

import android.content.Context
import android.content.SharedPreferences


object SharedPreferencesHelper {
    private const val PREFS_NAME = "MyPrefs"
    private const val TOKEN_KEY = "AuthToken"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuthToken(context: Context, token: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun getAuthToken(context: Context): String? {
        return getSharedPreferences(context).getString(TOKEN_KEY, null)
    }
}