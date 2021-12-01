package com.example.recipesapp.db

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromList(list: List<String>) = list.joinToString()

    @TypeConverter
    fun toList(string: String) = string.split(",").map { it.trim() }

}