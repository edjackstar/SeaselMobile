package com.example.seaselmobile.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.seaselmobile.R
import com.example.seaselmobile.RetrofitServices
import com.example.seaselmobile.SEaselApplication
import com.example.seaselmobile.SharedPreferencesController
import com.example.seaselmobile.model.request.AuthRequestBody
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import retrofit2.HttpException
import java.lang.Exception
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var api: RetrofitServices

    @Inject
    lateinit var sharedPreferencesController: SharedPreferencesController

    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        SEaselApplication.component.inject(this)
        if(sharedPreferencesController.getRefreshToken()!=null)
            openMusicList()
        loginEnterButton.setOnClickListener {
            login(loginEmail.text.toString(),loginPassword.text.toString())
        }
    }

    private fun login(email: String, password: String) = scope.launch {
        try {
            val tokens = api.login(AuthRequestBody(email, password))
            sharedPreferencesController.setAccessToken(tokens.access)
            sharedPreferencesController.setRefreshToken(tokens.refresh)
            openMusicList()
        }catch (e:HttpException){
            e.printStackTrace()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun openMusicList(){
        startActivity(Intent(applicationContext,MusicListActivity::class.java))
        finish()
    }
}