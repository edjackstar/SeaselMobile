package com.example.seaselmobile

import com.example.seaselmobile.model.Sheme
import com.example.seaselmobile.model.request.AuthRequestBody
import com.example.seaselmobile.model.api.CompositionRepresentationApiModel
import com.example.seaselmobile.model.api.RepresentationApiModel
import com.example.seaselmobile.model.request.RepetitionResultRequestBody
import com.example.seaselmobile.model.api.AccessTokenApiModel
import com.example.seaselmobile.model.api.TokenApiModel
import com.example.seaselmobile.model.request.FeedbackRequestBody
import com.example.seaselmobile.model.request.RefreshTokenRequestBody
import retrofit2.http.*

interface RetrofitServices {
    companion object {
        const val BASE_URL = "https://seasel.herokuapp.com/"
    }

    @POST("/auth/login/refresh/")
    suspend fun refreshToken(@Body refresh: RefreshTokenRequestBody): AccessTokenApiModel

    @POST("/auth/login/")
    suspend fun login(@Body body: AuthRequestBody): TokenApiModel

    @POST("/api/feedback/")
    suspend fun leaveFeedback(
        @Header("authorization") token: String,
        @Body body: FeedbackRequestBody
    )

    @GET("/api/music/")
    suspend fun getMusicList(@Header("authorization") token: String): List<CompositionRepresentationApiModel>

    @GET("/api/user_music/")
    suspend fun getUserMusicList(@Header("authorization") token: String): List<CompositionRepresentationApiModel>

    @GET("/api/music/{musicId}")
    suspend fun getRepresentation(
        @Path("musicId") musicId: Int,
        @Header("authorization") token: String
    ): Sheme

    @POST("/api/repetitions/save/")
    suspend fun sendRepetitionResult(
        @Header("authorization") token: String,
        @Body body: RepetitionResultRequestBody
    )
}