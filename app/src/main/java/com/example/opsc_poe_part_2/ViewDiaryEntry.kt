package com.example.opsc_poe_part_2

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ViewDiaryEntry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_diary_entry)

        val title: TextView = findViewById(R.id.DiaryTitleText)
        val content: TextView = findViewById(R.id.DiaryContentText)
        val date: TextView = findViewById(R.id.DiaryDateText)
        val layout: ConstraintLayout = findViewById(R.id.viewDiaryContraint)
        val backButton: ImageButton = findViewById(R.id.backButton)

        title.text = currentDiary?.Title
        content.text = currentDiary?.Content
        date.text = currentDiary?.Date
        layout.setBackgroundColor(Color.parseColor(currentDiary?.Color))
        backButton.setOnClickListener{
            this.finish()
        }
    }
}