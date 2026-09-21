package com.example.kyrsach

data class MoodEntry(
    val year: Int,
    val month: Int,
    val day: Int,
    val moodType: Int,
    val text: String = ""
)