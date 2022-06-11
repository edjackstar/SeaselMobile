package com.example.seaselmobile

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StorageController @Inject constructor(
    @ApplicationContext
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

    fun getFavorites(): List<Int> =
        sp.getStringSet(FAVORITES, setOf())?.toList()?.map { it.toInt() } ?: listOf()

    fun setFavorites(favorites: List<Int>) {
        sp.edit().putStringSet(FAVORITES, favorites.map { it.toString() }.toSet()).apply()
    }

    companion object {
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val FAVORITES = "FAVORITES"
        private const val PREFERENCES = "myPreferences"
    }

}