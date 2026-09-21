package com.example.kyrsach

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var pieChart: PieChart
    private lateinit var moodColorIndicator: View
    private lateinit var tvMoodText: TextView
    private val moodEntries = mutableListOf<MoodEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendarView)
        pieChart = findViewById(R.id.pie_chart_mood)
        moodColorIndicator = findViewById(R.id.mood_color_indicator)
        tvMoodText = findViewById(R.id.tv_mood_text)

        loadMoodData()
        setupCalendar()
        setupChart()

        findViewById<FloatingActionButton>(R.id.fab_add_entry).setOnClickListener {
            val cal = Calendar.getInstance()
            startActivity(Intent(this, EntryActivity::class.java).apply {
                putExtra("YEAR", cal.get(Calendar.YEAR))
                putExtra("MONTH", cal.get(Calendar.MONTH) + 1)
                putExtra("DAY", cal.get(Calendar.DAY_OF_MONTH))
            })
        }
    }

    override fun onResume() {
        super.onResume()
        loadMoodData()
        updateCalendarColors()
        updateChart()
    }

    private fun loadMoodData() {
        moodEntries.clear()
        val prefs = getSharedPreferences("mood_prefs", Context.MODE_PRIVATE)
        val allMoods = prefs.all

        for ((key, value) in allMoods) {
            if (key.startsWith("mood_") && !key.startsWith("mood_text_")) {
                val parts = key.split("_")
                if (parts.size == 4) {
                    val year = parts[1].toIntOrNull() ?: continue
                    val month = parts[2].toIntOrNull() ?: continue
                    val day = parts[3].toIntOrNull() ?: continue
                    val moodType = value as? Int ?: continue

                    // ✅ Загружаем текст
                    val text = prefs.getString("mood_text_${year}_${month}_${day}", "") ?: ""

                    moodEntries.add(MoodEntry(year, month, day, moodType, text))
                }
            }
        }
    }

    private fun setupCalendar() {
        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val cal = eventDay.calendar
                val year = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH) + 1
                val day = cal.get(Calendar.DAY_OF_MONTH)

                val moodEntry = moodEntries.find {
                    it.year == year && it.month == month && it.day == day
                }

                if (moodEntry != null) {
                    when (moodEntry.moodType) {
                        1 -> {
                            moodColorIndicator.setBackgroundColor(Color.parseColor("#4CAF50"))
                            // ✅ Показываем текст, если он есть
                            tvMoodText.text = if (moodEntry.text.isNotEmpty()) {
                                "😊 Хорошее настроение: ${moodEntry.text}"
                            } else {
                                "😊 Хорошее настроение"
                            }
                        }
                        2 -> {
                            moodColorIndicator.setBackgroundColor(Color.parseColor("#FFEB3B"))
                            tvMoodText.text = if (moodEntry.text.isNotEmpty()) {
                                "😐 Нейтральное настроение: ${moodEntry.text}"
                            } else {
                                "😐 Нейтральное настроение"
                            }
                        }
                        3 -> {
                            moodColorIndicator.setBackgroundColor(Color.parseColor("#F44336"))
                            tvMoodText.text = if (moodEntry.text.isNotEmpty()) {
                                "😞 Плохое настроение: ${moodEntry.text}"
                            } else {
                                "😞 Плохое настроение"
                            }
                        }
                    }
                } else {
                    moodColorIndicator.setBackgroundColor(Color.parseColor("#424242"))
                    tvMoodText.text = "Нет записи на $day.$month.$year"
                }

                startActivity(Intent(this@MainActivity, EntryActivity::class.java).apply {
                    putExtra("YEAR", year)
                    putExtra("MONTH", month)
                    putExtra("DAY", day)
                })
            }
        })
    }

    private fun updateCalendarColors() {
        val calendarDays = mutableListOf<CalendarDay>()

        moodEntries.forEach { entry ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, entry.year)
                set(Calendar.MONTH, entry.month - 1)
                set(Calendar.DAY_OF_MONTH, entry.day)
            }

            val color = when (entry.moodType) {
                1 -> Color.parseColor("#4CAF50")
                2 -> Color.parseColor("#FFEB3B")
                3 -> Color.parseColor("#F44336")
                else -> Color.TRANSPARENT
            }

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 16f
                setColor(color)
            }

            calendarDays.add(CalendarDay(cal).apply {
                backgroundDrawable = drawable
                labelColor = if (entry.moodType == 2) R.color.mood_neutral_text else R.color.mood_good_text_white
            })
        }

        calendarView.setCalendarDays(calendarDays)
    }

    private fun setupChart() {
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 40f
        pieChart.setUsePercentValues(true)
        pieChart.legend.textColor = Color.parseColor("#E0E0E0")
    }

    private fun updateChart() {
        val goodCount = moodEntries.count { it.moodType == 1 }.toFloat()
        val neutralCount = moodEntries.count { it.moodType == 2 }.toFloat()
        val badCount = moodEntries.count { it.moodType == 3 }.toFloat()

        val entries = mutableListOf<PieEntry>()

        // ✅ Всегда добавляем все три категории, даже если их 0
        entries.add(PieEntry(goodCount, "Хорошее"))
        entries.add(PieEntry(neutralCount, "Нейтральное"))
        entries.add(PieEntry(badCount, "Плохое"))

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#FFEB3B"),
                Color.parseColor("#F44336")
            )
            valueTextColor = Color.parseColor("#E0E0E0")
            valueTextSize = 14f
        }

        pieChart.data = PieData(dataSet)

        // ✅ Настройка отображения при нулевых значениях
        if (goodCount + neutralCount + badCount == 0f) {
            pieChart.setDrawEntryLabels(false) // Скрыть подписи на секторах
            pieChart.centerText = "Нет данных"
            pieChart.setCenterTextSize(16f)
            pieChart.setCenterTextColor(Color.parseColor("#A0A0A0"))
        } else {
            pieChart.setDrawEntryLabels(true)
            pieChart.centerText = ""
        }

        pieChart.invalidate()
    }
}