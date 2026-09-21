package com.example.kyrsach

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class EntryActivity : AppCompatActivity() {

    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0
    private var selectedMoodType = 0
    private var selectedColor = 0

    private lateinit var rgEmoji: RadioGroup
    private lateinit var etText: TextInputEditText
    private lateinit var colorBad: View
    private lateinit var colorNeutral: View
    private lateinit var colorGood: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        selectedYear = intent.getIntExtra("YEAR", Calendar.getInstance().get(Calendar.YEAR))
        selectedMonth = intent.getIntExtra("MONTH", Calendar.getInstance().get(Calendar.MONTH) + 1)
        selectedDay = intent.getIntExtra("DAY", Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        findViewById<TextView>(R.id.tv_entry_date).text = "$selectedDay.$selectedMonth.$selectedYear"

        rgEmoji = findViewById(R.id.rg_emoji)
        etText = findViewById(R.id.et_mood_text)
        colorBad = findViewById(R.id.color_bad)
        colorNeutral = findViewById(R.id.color_neutral)
        colorGood = findViewById(R.id.color_good)

        // Загружаем существующую запись
        val existingMood = getMood(selectedYear, selectedMonth, selectedDay)
        val existingText = getMoodText(selectedYear, selectedMonth, selectedDay)

        if (existingMood > 0) {
            selectedMoodType = existingMood
            etText.setText(existingText) // ✅ Предзаполняем текст

            when (existingMood) {
                1 -> {
                    rgEmoji.check(R.id.rb_emoji_good)
                    selectColor(colorGood, Color.parseColor("#4CAF50"), 1)
                }
                2 -> {
                    rgEmoji.check(R.id.rb_emoji_neutral)
                    selectColor(colorNeutral, Color.parseColor("#FFEB3B"), 2)
                }
                3 -> {
                    rgEmoji.check(R.id.rb_emoji_bad)
                    selectColor(colorBad, Color.parseColor("#F44336"), 3)
                }
            }
        }

        setupColorSelection()
        setupButtons()
    }

    private fun selectColor(view: View, color: Int, moodType: Int) {
        selectedColor = color
        selectedMoodType = moodType

        listOf(colorBad, colorNeutral, colorGood).forEach { it.alpha = 0.5f }
        view.alpha = 1.0f
    }

    private fun setupColorSelection() {
        colorBad.setOnClickListener {
            rgEmoji.check(R.id.rb_emoji_bad)
            selectColor(colorBad, Color.parseColor("#F44336"), 3)
        }
        colorNeutral.setOnClickListener {
            rgEmoji.check(R.id.rb_emoji_neutral)
            selectColor(colorNeutral, Color.parseColor("#FFEB3B"), 2)
        }
        colorGood.setOnClickListener {
            rgEmoji.check(R.id.rb_emoji_good)
            selectColor(colorGood, Color.parseColor("#4CAF50"), 1)
        }

        rgEmoji.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_emoji_bad -> selectColor(colorBad, Color.parseColor("#F44336"), 3)
                R.id.rb_emoji_neutral -> selectColor(colorNeutral, Color.parseColor("#FFEB3B"), 2)
                R.id.rb_emoji_good -> selectColor(colorGood, Color.parseColor("#4CAF50"), 1)
            }
        }
    }

    private fun setupButtons() {
        findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            if (selectedMoodType == 0) {
                Toast.makeText(this, "Выберите настроение", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val text = etText.text.toString().trim()

            // ✅ Сохраняем и настроение, и текст
            saveMood(selectedYear, selectedMonth, selectedDay, selectedMoodType)
            saveMoodText(selectedYear, selectedMonth, selectedDay, text)

            Toast.makeText(this, "Сохранено!", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<MaterialButton>(R.id.btn_delete).setOnClickListener {
            deleteMood(selectedYear, selectedMonth, selectedDay)
            Toast.makeText(this, "Запись удалена", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveMood(year: Int, month: Int, day: Int, moodType: Int) {
        val prefs = getSharedPreferences("mood_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("mood_${year}_${month}_${day}", moodType).apply()
    }

    private fun getMood(year: Int, month: Int, day: Int): Int {
        val prefs = getSharedPreferences("mood_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("mood_${year}_${month}_${day}", 0)
    }

    // ✅ Новые методы для работы с текстом
    private fun saveMoodText(year: Int, month: Int, day: Int, text: String) {
        val prefs = getSharedPreferences("mood_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("mood_text_${year}_${month}_${day}", text).apply()
    }

    private fun getMoodText(year: Int, month: Int, day: Int): String {
        val prefs = getSharedPreferences("mood_prefs", Context.MODE_PRIVATE)
        return prefs.getString("mood_text_${year}_${month}_${day}", "") ?: ""
    }

    private fun deleteMood(year: Int, month: Int, day: Int) {
        val prefs = getSharedPreferences("mood_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .remove("mood_${year}_${month}_${day}")
            .remove("mood_text_${year}_${month}_${day}")
            .apply()
    }
}