package com.example.kyrsach

import android.app.Application
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
    private var selectedMoodType = 0 // 1, 2 или 3
    private var selectedColor = 0

    private lateinit var rgEmoji: RadioGroup
    private lateinit var etText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        selectedYear = intent.getIntExtra("YEAR", Calendar.getInstance().get(Calendar.YEAR))
        selectedMonth = intent.getIntExtra("MONTH", Calendar.getInstance().get(Calendar.MONTH) + 1)
        selectedDay = intent.getIntExtra("DAY", Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        findViewById<TextView>(R.id.tv_entry_date).text = "$selectedDay.$selectedMonth.$selectedYear"

        rgEmoji = findViewById(R.id.rg_emoji)
        etText = findViewById(R.id.et_mood_text)

        setupColorSelection()
        setupButtons()
    }

    private fun setupColorSelection() {
        val colorBad = findViewById<View>(R.id.color_bad)
        val colorNeutral = findViewById<View>(R.id.color_neutral)
        val colorGood = findViewById<View>(R.id.color_good)

        fun selectColor(view: View, color: Int, moodType: Int) {
            selectedColor = color
            selectedMoodType = moodType

            listOf(colorBad, colorNeutral, colorGood).forEach { it.alpha = 0.5f }
            view.alpha = 1.0f
        }

        colorBad.setOnClickListener { selectColor(colorBad, Color.parseColor("#F44336"), 3) }
        colorNeutral.setOnClickListener { selectColor(colorNeutral, Color.parseColor("#FFEB3B"), 2) }
        colorGood.setOnClickListener { selectColor(colorGood, Color.parseColor("#4CAF50"), 1) }

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

            val text = etText.text.toString()

            (applicationContext as Application).let {
            }

            Toast.makeText(this, "Сохранено!", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<MaterialButton>(R.id.btn_delete).setOnClickListener {
            finish()
        }
    }
}