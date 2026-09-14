package com.example.kyrsach

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

    // Имитация базы данных
    private val moodEntries = mutableListOf<MoodEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendar_view)
        pieChart = findViewById(R.id.pie_chart_mood)
        moodColorIndicator = findViewById(R.id.mood_color_indicator)
        tvMoodText = findViewById(R.id.tv_mood_text)

        // Тестовые данные
        addMockData()

        setupCalendar()
        setupChart()

        findViewById<FloatingActionButton>(R.id.fab_add_entry).setOnClickListener {
            startActivity(Intent(this, EntryActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateChart()
    }

    private fun setupCalendar() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val moodEntry = moodEntries.find {
                it.year == year && it.month == (month + 1) && it.day == dayOfMonth
            }

            if (moodEntry != null) {
                // Показываем настроение выбранного дня
                when (moodEntry.moodType) {
                    1 -> {
                        moodColorIndicator.setBackgroundColor(Color.parseColor("#4CAF50"))
                        tvMoodText.text = "😊 Хорошее настроение: ${moodEntry.text}"
                    }
                    2 -> {
                        moodColorIndicator.setBackgroundColor(Color.parseColor("#FFEB3B"))
                        tvMoodText.text = "😐 Нейтральное настроение: ${moodEntry.text}"
                    }
                    3 -> {
                        moodColorIndicator.setBackgroundColor(Color.parseColor("#F44336"))
                        tvMoodText.text = "😞 Плохое настроение: ${moodEntry.text}"
                    }
                }
            } else {
                moodColorIndicator.setBackgroundColor(Color.parseColor("#E0E0E0"))
                tvMoodText.text = "Нет записи на $dayOfMonth.${month + 1}.$year"
            }

            // Открываем экран редактирования
            val intent = Intent(this, EntryActivity::class.java).apply {
                putExtra("YEAR", year)
                putExtra("MONTH", month + 1)
                putExtra("DAY", dayOfMonth)
            }
            startActivity(intent)
        }
    }

    private fun setupChart() {
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 40f
        pieChart.setUsePercentValues(true)
    }

    private fun updateChart() {
        val goodCount = moodEntries.count { it.moodType == 1 }.toFloat()
        val neutralCount = moodEntries.count { it.moodType == 2 }.toFloat()
        val badCount = moodEntries.count { it.moodType == 3 }.toFloat()

        if (goodCount + neutralCount + badCount == 0f) {
            pieChart.clear()
            return
        }

        val entries = arrayListOf<PieEntry>()
        if (goodCount > 0) entries.add(PieEntry(goodCount, "Хорошее"))
        if (neutralCount > 0) entries.add(PieEntry(neutralCount, "Нейтральное"))
        if (badCount > 0) entries.add(PieEntry(badCount, "Плохое"))

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#FFEB3B"),
                Color.parseColor("#F44336")
            )
            valueTextColor = Color.BLACK
            valueTextSize = 14f
        }

        pieChart.data = PieData(dataSet)
        pieChart.invalidate()
    }

    private fun addMockData() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        moodEntries.add(MoodEntry(year, month, 10, 1, "Отличный день!"))
        moodEntries.add(MoodEntry(year, month, 12, 3, "Немного грустно"))
        moodEntries.add(MoodEntry(year, month, 14, 2, "Обычный день"))
    }
}