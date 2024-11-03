package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class Rewards : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rewards)
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
        // Initialize BottomNavigationView and set up item selection listener
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_diary -> {
                    startActivity(Intent(this, Dairy::class.java))
                    true
                }
                R.id.nav_meditation -> {
                    startActivity(Intent(this, Meditation::class.java))
                    true
                }
                R.id.nav_rewards -> {
                    true
                }
                R.id.nav_game -> {
                    startActivity(Intent(this, Game::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.nav_rewards
    }


}