package com.example.recipesapp.models

data class RecipeResponse(
    val _links: Links,
    val count: Int,
    val from: Int,
    val hits: MutableList<Hit>,
    var to: Int
)