package com.example.seaselmobile.model.api

class CompositionRepresentationApiModel(
    var composition_representation_id: Int,
    var composition_id: Int,
    var name: String,
    var instrument: String,
    var author: String,
    var difficulty: Int,
    var format: String,
    var avg_mark: Float?
)