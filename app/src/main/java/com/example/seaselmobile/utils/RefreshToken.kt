package com.example.seaselmobile.utils

import android.util.Log
import com.example.seaselmobile.RetrofitServices
import com.example.seaselmobile.StorageController
import com.example.seaselmobile.model.request.RefreshTokenRequestBody
import javax.inject.Inject

class RefreshToken @Inject constructor(
    private val api:RetrofitServices,
    private var storageController: StorageController
) {

    suspend operator fun invoke() {
        val refresh = storageController.getRefreshToken()
        if (refresh != null)
            try {
                val accessToken = api.refreshToken(RefreshTokenRequestBody(refresh))
                storageController.setAccessToken(accessToken.access)
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
    }
}