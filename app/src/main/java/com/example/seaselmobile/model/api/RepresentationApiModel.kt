package com.example.seaselmobile.model.api

import com.example.seaselmobile.model.Sheme

class RepresentationApiModel(
    var format: String,
    var composition: CompositionApiModel,
    var sheme: Sheme
) {
    override fun toString(): String {
        return "{\n" +
                "   format: $format\n" +
                "   composition: $composition\n" +
                "   sheme: $sheme\n" +
                "}"
    }
}
