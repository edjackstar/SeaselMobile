package com.example.seaselmobile.model

class Accord(
    var duration: Int,
    var notes: List<Int>
){
    override fun toString(): String {
        return "{\n" +
                "   duration: $duration\n" +
                "   notes: $notes\n" +
                "}"
    }
}