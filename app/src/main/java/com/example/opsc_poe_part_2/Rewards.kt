package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Rewards : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rewards)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnDailyGoals = findViewById<Button>(R.id.btnDailyGoals)
        val btnLevelUp = findViewById<Button>(R.id.btnLevelUp)

        btnDailyGoals.setOnClickListener {
            // Create an intent to start RegisterActivity
            val intent = Intent(this, DailyGoals::class.java)
            startActivity(intent)
        }

        btnLevelUp.setOnClickListener {
            // Create an intent to start RegisterActivity
            val intent = Intent(this, LevelUp::class.java)
            startActivity(intent)
        }
    }


}