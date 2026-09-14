package com.example.kyrsach
import com.prolificinteractive.materialcalendarview.CalendarDay

data class MoodEntry(
    val year: Int,
    val month: Int,
    val day: Int,
    val moodType: Int,
    val text: String
) {
    fun toCalendarDay(): CalendarDay = CalendarDay.from(year, month, day)
}