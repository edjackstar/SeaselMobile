package com.example.seaselmobile.activity

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.seaselmobile.R
import com.example.seaselmobile.RetrofitServices
import com.example.seaselmobile.SEaselApplication
import com.example.seaselmobile.SharedPreferencesController
import com.example.seaselmobile.model.Sheme
import com.example.seaselmobile.ustils.RefreshToken
import com.example.seaselmobile.ustils.toTakts
import kotlinx.android.synthetic.main.activity_repetition.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RepetitionActivity : AppCompatActivity() {

    @Inject
    lateinit var api: RetrofitServices

    @Inject
    lateinit var sharedPreferencesController: SharedPreferencesController

    @Inject
    lateinit var refreshToken: RefreshToken

    private var animator: ValueAnimator? = null
    private var speed = 0f
    private var duration = 1f
    private var speedVisibility = false

    private var representation: Sheme? = null

    private var isPlaying = false

    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repetition)

        SEaselApplication.component.inject(this)
        val author = intent.getStringExtra(REPETITION_AUTHOR)
        val name = intent.getStringExtra(REPETITION_NAME)
        repetitionTitle.text = "$author - $name"
        speed = repetitionSeekBar.progress / 100f + 0.5f
        repetitionPlayButton.setOnClickListener {
            if (!isPlaying) {
                animator?.duration = (1000 * duration / speed).toLong()
                animator?.start()
                repetitionPlayButton.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_stop_btn
                    )
                )
            } else {
                animator?.pause()
                animator?.setCurrentFraction(0f)
                repetitionPlayButton.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_play_btn
                    )
                )
            }
            isPlaying = !isPlaying
        }
        repetitionBackButton.setOnClickListener {
            finish()
        }
        repetitionSpeedButton.setOnClickListener {
            if (speedVisibility)
                repetitionSeekBar.visibility = View.GONE
            else
                repetitionSeekBar.visibility = View.VISIBLE
            speedVisibility = !speedVisibility
        }
        startMelodie(intent.getIntExtra(REPETITION_ID, 1))
        repetitionSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                speed = progress / 100f + 0.5f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

    }

    private fun startMelodie(repetitionId: Int) = scope.launch {
        try {
            refreshToken()
            val accessToken = sharedPreferencesController.getAccessToken()
            val representation = api.getRepresentation(repetitionId, "Bearer $accessToken")
            val takts = representation.accords.toTakts(2)
            repetitionCanvas.takts = takts
            this@RepetitionActivity.representation = representation
            Log.d("wtf22",takts.toString())

            val taktLengthInTime = 1f
            duration = takts.size * taktLengthInTime
            animator = ValueAnimator.ofFloat(0f, duration).also {
                it.addUpdateListener { a ->
                    repetitionCanvas.time = a.animatedValue as Float
                }
                it.interpolator = LinearInterpolator()
                it.repeatMode = ValueAnimator.RESTART
                it.duration = (1000 * duration / speed).toLong()
            }
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