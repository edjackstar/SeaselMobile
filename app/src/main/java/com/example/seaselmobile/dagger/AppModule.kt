package com.example.seaselmobile.dagger

import com.example.seaselmobile.RetrofitServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): RetrofitServices =
        retrofit.create(RetrofitServices::class.java)

    @Singleton
    @Provides
    fun provideRetrofitClient(): Retrofit = Retrofit.Builder()
        .baseUrl(RetrofitServices.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
}