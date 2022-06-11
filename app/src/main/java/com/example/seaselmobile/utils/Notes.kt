package com.example.seaselmobile.utils

import com.example.seaselmobile.model.Accord
import com.example.seaselmobile.model.note.NoteAccuracy
import com.example.seaselmobile.model.note.Notes
import com.example.seaselmobile.model.note.Takt

fun List<Accord>.toTakts(taktDuration: Int): List<Takt> {
    val takts = mutableListOf<Takt>()
    val notesInTakt = mutableListOf<Notes>()
    var offset = 0f
    forEach { accord ->
        val realDuration = (1f / accord.duration) * taktDuration
        val note = Notes(
            offset = offset,
            duration = realDuration,
            notes = accord.notes,
            accuracy = NoteAccuracy.NONE
        )
        notesInTakt.add(note)
        offset += realDuration
        if (offset >= 1f) {
            takts.add(
                Takt(
                    duration = taktDuration,
                    notes = ArrayList(notesInTakt)
                )
            )
            notesInTakt.clear()
            offset -= 1
        }
    }
    if (notesInTakt.isNotEmpty()) {
        takts.add(
            Takt(
                duration = taktDuration,
                notes = ArrayList(notesInTakt)
            )
        )
        notesInTakt.clear()
        offset -= 1
    }
    return takts
}