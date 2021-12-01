package com.example.recipesapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val calories: Double,
    val image: String,
    val label: String,
    val mealType: List<String>,
    val source: String,
    val url: String,
    val yield: Double
) : Serializable