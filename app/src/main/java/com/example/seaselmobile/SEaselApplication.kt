package com.example.seaselmobile

import android.app.Application
import com.example.seaselmobile.dagger.DaggerComponent
import com.example.seaselmobile.dagger.DaggerDaggerComponent

class SEaselApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        component = DaggerDaggerComponent.builder().context(this).build()
    }

    companion object {
        lateinit var component: DaggerComponent
    }
}