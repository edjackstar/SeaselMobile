package com.example.seaselmobile.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seaselmobile.*
import com.example.seaselmobile.model.api.CompositionRepresentationApiModel
import com.example.seaselmobile.ustils.RefreshToken
import kotlinx.android.synthetic.main.activity_music_list.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class MusicListActivity : AppCompatActivity() {

    @Inject
    lateinit var api: RetrofitServices

    @Inject
    lateinit var sharedPreferencesController: SharedPreferencesController

    @Inject
    lateinit var refreshToken: RefreshToken


    private val scope = MainScope()
    private var musicList = mutableListOf<CompositionRepresentationApiModel>()
    private lateinit var adapter: MusicListAdapter
    private val context = this

    private var onItemClickListener: (Int) -> Unit = {
        startActivity(
            Intent(this, RepetitionActivity::class.java)
                .putExtra(REPETITION_ID, musicList[it].composition_representation_id)
                .putExtra(REPETITION_AUTHOR, musicList[it].author)
                .putExtra(REPETITION_NAME, musicList[it].name)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_list)
        SEaselApplication.component.inject(this)

        getMusicList()

        musicListRecycler.layoutManager = LinearLayoutManager(this)
    }

    private fun getMusicList() = scope.launch {
        try {
            try {
                refreshToken()
            } catch (e: java.lang.Exception) {
                if (e is HttpException && e.code() == 401) {
                    sharedPreferencesController.setAccessToken(null)
                    sharedPreferencesController.setRefreshToken(null)
                    startActivity(Intent(context, LoginActivity::class.java))
                    finish()
                }
            }
            val access = sharedPreferencesController.getAccessToken()
            musicList = api.getMusicList("Bearer $access").toMutableList()
            adapter = MusicListAdapter(musicList)
            adapter.onItemClickListener = onItemClickListener
            musicListRecycler.adapter = adapter
        } catch (e: HttpException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val REPETITION_ID = "REPETITION_ID"
        const val REPETITION_AUTHOR = "REPETITION_AUTHOR"
        const val REPETITION_NAME = "REPETITION_NAME"
    }
}