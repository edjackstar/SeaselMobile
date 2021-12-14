package com.example.seaselmobile.model

class Sheme(
    var size: String,
    var tone: String,
    var prim: Int,
    var accords: List<Accord>,
){
    override fun toString(): String {
        return "{\n" +
                "   size: $size\n" +
                "   tone: $tone\n" +
                "   prim: $prim\n" +
                "   accords: $accords\n" +
                "}"
    }
}