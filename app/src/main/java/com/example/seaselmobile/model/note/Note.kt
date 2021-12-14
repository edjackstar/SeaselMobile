package com.example.seaselmobile.model.note

data class Notes(
    val offset: Float,
    val duration: Float,
    val notes: List<Int>,
    var accuracy: NoteAccuracy
)

data class Takt(
    val duration: Int,
    val notes: List<Notes>
)

enum class NoteAccuracy{
    CORRECT,
    INCORRECT,
    NONE
}