package com.example.seaselmobile.model.api

class CompositionApiModel(
    var name: String,
    var instrument: String,
    var author: String,
    var difficulty: Int,
)
{
    override fun toString(): String {
        return "{\n" +
                "   name: $name\n" +
                "   instrument: $instrument\n" +
                "   author: $author\n" +
                "   difficulty: $difficulty\n" +
                "}"
    }
}