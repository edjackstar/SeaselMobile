package com.example.seaselmobile.dagger

import android.content.Context
import com.example.seaselmobile.activity.LoginActivity
import com.example.seaselmobile.activity.MusicListActivity
import com.example.seaselmobile.activity.RepetitionActivity
import com.example.seaselmobile.ustils.RefreshToken
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface DaggerComponent {
    fun inject(activity: LoginActivity)
    fun inject(activity: MusicListActivity)
    fun inject(activity: RepetitionActivity)
    fun inject(activity: RefreshToken)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): DaggerComponent
    }
}