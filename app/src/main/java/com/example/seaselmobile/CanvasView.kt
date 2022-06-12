package com.example.seaselmobile

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.example.seaselmobile.model.note.Takt
import com.example.seaselmobile.model.note.frequencyRange


class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var frequency: Double = 0.0

    private var keyImage: Drawable? = null
    private var taktSizeImage: Drawable? = null
    private var upNoteImage: Drawable? = null
    private var downNoteImage: Drawable? = null
    private var upNoteImageRed: Drawable? = null
    private var downNoteImageRed: Drawable? = null
    private var upNoteImageGreen: Drawable? = null
    private var upNoteImageYellow: Drawable? = null
    private var downNoteImageGreen: Drawable? = null
    private var downNoteImageYellow: Drawable? = null

    private val linesPaint: Paint = Paint().also {
        it.color = Color.GRAY
        it.strokeWidth = 5f
    }

    private var checkLinePaint: Paint = Paint().also {
        it.color = Color.rgb(0, 163, 255)
        it.strokeWidth = 8f
    }

    private val taktLength: Float = 100 * resources.displayMetrics.density
    private val topMargin = 42.toPX()
    private val lineDist = 20.toPX()

    private val noteHeight = 64.toPX()
    private val noteWidth = 27.toPX()

    private fun getLineY(index: Int): Int {
        return index * lineDist + topMargin
    }

    fun getUpNote(noteAccuracy: Int): Drawable? {
        return when {
            noteAccuracy > 1 -> upNoteImageGreen
            noteAccuracy == 1 -> upNoteImageYellow
            noteAccuracy < 0 -> upNoteImageRed
            else -> upNoteImage
        }
    }

    fun getDownNote(noteAccuracy: Int): Drawable? {
        return when {
            noteAccuracy > 1 -> downNoteImageGreen
            noteAccuracy == 1 -> downNoteImageYellow
            noteAccuracy < 0 -> downNoteImageRed
            else -> downNoteImage
        }
    }

    private fun getNoteImage(note: Int, noteAccuracy: Int): Drawable? {
        return when (note) {
            62 -> getUpNote(noteAccuracy)
            64 -> getUpNote(noteAccuracy)
            65 -> getUpNote(noteAccuracy)
            67 -> getUpNote(noteAccuracy)
            69 -> getUpNote(noteAccuracy)
            71 -> getUpNote(noteAccuracy)
            72 -> getDownNote(noteAccuracy)
            74 -> getDownNote(noteAccuracy)
            76 -> getDownNote(noteAccuracy)
            77 -> getDownNote(noteAccuracy)
            79 -> getDownNote(noteAccuracy)
            else -> getDownNote(noteAccuracy)
        }
    }

    private fun getNoteTop(note: Int): Int {
        return when (note) {
            62 -> topMargin + 5 * lineDist - noteHeight
            64 -> topMargin + 4.5 * lineDist - noteHeight
            65 -> topMargin + 4 * lineDist - noteHeight
            67 -> topMargin + 3.5 * lineDist - noteHeight
            69 -> topMargin + 3 * lineDist - noteHeight
            71 -> topMargin + 2.5 * lineDist - noteHeight
            72 -> topMargin + 2 * lineDist
            74 -> topMargin + 1.5 * lineDist
            76 -> topMargin + 1 * lineDist
            77 -> topMargin + 0.5 * lineDist
            79 -> topMargin
            else -> 0
        }.toInt()
    }

    private fun drawNoteLines(canvas: Canvas) {
        canvas.drawLine(
            0f,
            getLineY(0).toFloat(),
            canvas.width.toFloat(),
            getLineY(0).toFloat(),
            linesPaint
        )
        canvas.drawLine(
            0f,
            getLineY(1).toFloat(),
            canvas.width.toFloat(),
            getLineY(1).toFloat(),
            linesPaint
        )
        canvas.drawLine(
            0f,
            getLineY(2).toFloat(),
            canvas.width.toFloat(),
            getLineY(2).toFloat(),
            linesPaint
        )
        canvas.drawLine(
            0f,
            getLineY(3).toFloat(),
            canvas.width.toFloat(),
            getLineY(3).toFloat(),
            linesPaint
        )
        canvas.drawLine(
            0f,
            getLineY(4).toFloat(),
            canvas.width.toFloat(),
            getLineY(4).toFloat(),
            linesPaint
        )
    }

    private fun drawCheckLine(canvas: Canvas) {
        val paint = checkLinePaint
        val lineX = canvas.width.toFloat() / 2
        canvas.drawLine(lineX, 0f, lineX, canvas.height.toFloat(), paint)
    }

    private fun drawKey(canvas: Canvas) {
        val key = keyImage
        if (key != null) {
            key.bounds = Rect(24.toPX(), 0, 88.toPX(), canvas.height)
            key.draw(canvas)
        }
        val taktSize = taktSizeImage
        if (taktSize != null) {
            taktSize.bounds = Rect(92.toPX(), 35.toPX(), 112.toPX(), canvas.height - 20.toPX())
            taktSize.draw(canvas)
        }
    }

    private fun drawNote(canvas: Canvas) {
        val backup = canvas.save()
        val center = canvas.width / 2
        canvas.translate(-time * taktLength, 0f)
        for (it in takts.indices) {
            for (note in takts[it].notes) {
                val left =
                    center + ((note.offset + it) * taktLength).toInt() + (noteWidth * 0.5).toInt()
                for (number in note.notes) {
                    if (left + noteWidth / 2 - time * taktLength < center  && left + noteWidth / 2 - time * taktLength > center - noteWidth) {

                        if (frequency in frequencyRange(note.notes[0]))
                            note.accuracy++

                        Log.d(
                            "lol",
                            "${note.notes[0]} ${note.accuracy} ${frequencyRange(note.notes[0])} $frequency"
                        )
                    } else if (left + noteWidth / 2 - time * taktLength < center - noteWidth && note.accuracy == 0) {
                        note.accuracy--
                    }
                    val image = getNoteImage(number, note.accuracy)
                    if (image != null) {
                        image.bounds = Rect(
                            left,
                            getNoteTop(number),
                            left + noteWidth,
                            noteHeight + getNoteTop(note.notes[0])
                        )
                        image.draw(canvas)
                    }
                }
            }
        }

        for (i in -100 until 100) {
            canvas.drawLine(
                center + i * taktLength, getLineY(0).toFloat(),
                center + i * taktLength, getLineY(4).toFloat(),
                linesPaint
            )
        }
        canvas.restoreToCount(backup)
    }

    init {
        keyImage = getDrawable(context, R.drawable.ic_key)
        taktSizeImage = getDrawable(context, R.drawable.ic__4)
        upNoteImage = getDrawable(context, R.drawable.ic_note_up)
        downNoteImage = getDrawable(context, R.drawable.ic_note_down)
        upNoteImageRed = getDrawable(context, R.drawable.ic_note_up_red)
        downNoteImageRed = getDrawable(context, R.drawable.ic_note_down_red)
        upNoteImageGreen = getDrawable(context, R.drawable.ic_note_up_green)
        downNoteImageGreen = getDrawable(context, R.drawable.ic_note_down_green)
        upNoteImageYellow = getDrawable(context, R.drawable.ic_note_up_yellow)
        downNoteImageYellow = getDrawable(context, R.drawable.ic_note_down_yellow)
    }

    var takts: List<Takt> = listOf()
        set(value) {
            field = value
            invalidate()
        }

    var time: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null)
            return

        drawNoteLines(canvas)
        drawNote(canvas)
        drawCheckLine(canvas)
        drawKey(canvas)
    }

    private fun Int.toPX(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}