package com.example.road_app_dip.utils

import android.content.Context

object TokenManager {

    private const val PREFS_NAME = "AppPrefs"
    private const val TOKEN_KEY = "bearer_token"

    fun saveToken(context: Context, bearerToken: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN_KEY, bearerToken)
        editor.apply()
    }

}
