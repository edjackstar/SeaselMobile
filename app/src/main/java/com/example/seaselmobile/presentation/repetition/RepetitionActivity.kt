package com.example.seaselmobile.presentation.repetition

import android.Manifest
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import com.example.seaselmobile.R
import com.example.seaselmobile.RetrofitServices
import com.example.seaselmobile.StorageController
import com.example.seaselmobile.audio.calculators.AudioCalculator
import com.example.seaselmobile.audio.core.Callback
import com.example.seaselmobile.audio.core.Recorder
import com.example.seaselmobile.model.Sheme
import com.example.seaselmobile.model.note.NoteAccuracy
import com.example.seaselmobile.model.request.RepetitionResultRequestBody
import com.example.seaselmobile.utils.RefreshToken
import com.example.seaselmobile.utils.toTakts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_repetition.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class RepetitionActivity : AppCompatActivity() {

    @Inject
    lateinit var api: RetrofitServices

    @Inject
    lateinit var storageController: StorageController

    @Inject
    lateinit var refreshToken: RefreshToken

    private var recorder: Recorder? = null
    private var audioCalculator: AudioCalculator? = null
    private var handler: Handler? = null

    var compositionId: Int? = null
    var mark: Int = 5

    private var animator: ValueAnimator? = null
    private var speed = 0f
    private var duration = 1f
    private var speedVisibility = false

    private var representation: Sheme? = null

    private var isPlaying = false

    private val scope = MainScope()

    private val callback: Callback = object : Callback {
        override fun onBufferAvailable(buffer: ByteArray?) {
            val audioCalc = audioCalculator ?: return
            audioCalc.setBytes(buffer)
            Log.d(
                "!!!",
                "frequency: ${audioCalc.frequency}"
            )
            repetitionCanvas.frequency = audioCalc.frequency
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repetition)

        val author = intent.getStringExtra(REPETITION_AUTHOR)
        val name = intent.getStringExtra(REPETITION_NAME)

        recorder = Recorder(callback)
        audioCalculator = AudioCalculator()
        handler = Handler(Looper.getMainLooper())

        compositionId = intent.getIntExtra(COMPOSITION_ID, 0)
        repetitionTitle.text = "$author - $name"
        speed = repetitionSeekBar.progress / 100f + 0.5f

        repetitionPlayButton.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
                return@setOnClickListener
            }

            if (!isPlaying) {
                recorder?.start()
                animator?.duration = (1000 * duration / speed).toLong()
                animator?.start()
                repetitionPlayButton.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_stop_btn
                    )
                )
            } else {
                recorder?.stop()
                animator?.pause()

                val rep = representation

                if (rep != null)
                    repetitionCanvas.takts = rep.accords.toTakts(2)

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
            val accessToken = storageController.getAccessToken()
            val representation = api.getRepresentation(repetitionId, "Bearer $accessToken")
            val takts = representation.accords.toTakts(2)
            repetitionCanvas.takts = takts
            this@RepetitionActivity.representation = representation

            val taktLengthInTime = 1f
            duration = takts.size * taktLengthInTime
            animator = ValueAnimator.ofFloat(0f, duration).also {
                it.addUpdateListener { a ->
                    repetitionCanvas.time = a.animatedValue as Float
                }
                it.interpolator = LinearInterpolator()
                it.repeatMode = ValueAnimator.RESTART
                it.duration = (1000 * duration / speed).toLong()
                it.doOnEnd {
                    recorder?.stop()
                    onMelodieEndClick()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onMelodieEndClick() = AlertDialog.Builder(this).apply {
        val dialogView = layoutInflater.inflate(R.layout.dialog_send_mark, null)
        val dialog = setView(dialogView).create()

        val cancelButton = dialogView.findViewById<TextView>(R.id.sendMarkMarkCancel)
        val confirmButton = dialogView.findViewById<TextView>(R.id.sendMarkMarkConfirm)
        val mark = dialogView.findViewById<TextView>(R.id.sendMarkMark)

        var notes = 0.0
        repetitionCanvas.takts.forEach {
            notes += it.notes.size
        }

        var correctNotes = 0.0
        repetitionCanvas.takts.forEach { takt ->
            takt.notes.forEach {
                if (it.accuracy == NoteAccuracy.CORRECT)
                    correctNotes++
            }
        }

        val realMark = correctNotes / notes *100

        when {
            realMark>=90 -> mark.text = "5"
            realMark>=75 -> mark.text = "4"
            realMark>=60 -> mark.text = "3"
            else -> mark.text = "2"
        }

        confirmButton.setOnClickListener {
            sendMark(realMark)
            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun sendMark(newMark:Double) = scope.launch {
        val id = compositionId
        if (id != null)
            try {
                val accessToken = storageController.getAccessToken()
                api.sendRepetitionResult(
                    "Bearer $accessToken",
                    RepetitionResultRequestBody(id, newMark)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@RepetitionActivity, "Can't send mark", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    companion object {
        const val COMPOSITION_ID = "COMPOSITION_ID"
        const val REPETITION_ID = "REPETITION_ID"
        const val REPETITION_AUTHOR = "REPETITION_AUTHOR"
        const val REPETITION_NAME = "REPETITION_NAME"
    }
}