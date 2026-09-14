package com.example.kyrsach

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class MoodDecorator(
    private val dates: Collection<CalendarDay>,
    private val color: Int
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        val drawable = ShapeDrawable(OvalShape()).apply {
            paint.color = color
            val inset = 10
            setIntrinsicWidth(inset)
            setIntrinsicHeight(inset)
        }
        view.setBackgroundDrawable(drawable)
    }
}