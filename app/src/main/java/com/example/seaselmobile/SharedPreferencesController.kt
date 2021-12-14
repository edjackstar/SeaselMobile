package com.example.seaselmobile

import android.content.Context
import javax.inject.Inject

class SharedPreferencesController @Inject constructor(
    context: Context
) {

    private val sp = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun getRefreshToken(): String? = sp.getString(REFRESH_TOKEN, null)
    fun setRefreshToken(token: String?) {
        sp.edit().putString(REFRESH_TOKEN, token).apply()
    }

    fun getAccessToken(): String? = sp.getString(ACCESS_TOKEN, null)
    fun setAccessToken(token: String?) {
        sp.edit().putString(ACCESS_TOKEN, token).apply()
    }

    companion object {
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val PREFERENCES = "myPreferences"
    }

}