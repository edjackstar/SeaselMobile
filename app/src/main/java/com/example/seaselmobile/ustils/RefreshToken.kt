package com.example.seaselmobile.ustils

import android.content.Intent
import android.util.Log
import com.example.seaselmobile.RetrofitServices
import com.example.seaselmobile.SEaselApplication
import com.example.seaselmobile.SharedPreferencesController
import com.example.seaselmobile.activity.LoginActivity
import com.example.seaselmobile.model.request.RefreshTokenRequestBody
import retrofit2.HttpException
import javax.inject.Inject

class RefreshToken @Inject constructor(
    private val api:RetrofitServices,
    private var sharedPreferencesController: SharedPreferencesController
) {

    suspend operator fun invoke() {
        SEaselApplication.component.inject(this)
        val refresh = sharedPreferencesController.getRefreshToken()
        if (refresh != null)
            try {
                Log.d("popa", refresh)
                val accessToken = api.refreshToken(RefreshTokenRequestBody(refresh))
                sharedPreferencesController.setAccessToken(accessToken.access)
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
    }
}