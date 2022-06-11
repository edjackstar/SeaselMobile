package com.example.seaselmobile

import android.app.Application
import com.example.seaselmobile.dagger.DaggerComponent
import com.example.seaselmobile.dagger.DaggerDaggerComponent
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SEaselApplication : Application()