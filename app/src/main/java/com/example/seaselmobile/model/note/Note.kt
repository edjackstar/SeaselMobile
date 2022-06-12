package com.example.seaselmobile.model.note

import android.util.Range

data class Notes(
    val offset: Float,
    val duration: Float,
    val notes: List<Int>,
    var accuracy: Int
)

fun frequencyRange(note: Int): Range<Double> {
    return when (note) {
        62 -> Range(285.42,301.9)
        64 -> Range(320.58,339.43)
        65 -> Range(339.43,359.61)
        67 -> Range(380.995,403.65)
        69 -> Range(427.65,453.08)
        71 -> Range(480.02,508.565)
        72 -> Range(508.565,538.805)
        74 -> Range(570.84,604.79)
        76 -> Range(640.76,678.86)
        77 -> Range(678.86,719.22)
        79 -> Range(761.99,807.3)
        else -> Range(0.0,1000.0)
    }
}

data class Takt(
    val duration: Int,
    val notes: List<Notes>
)

enum class NoteAccuracy {
    CORRECT,
    INCORRECT,
    NONE
}