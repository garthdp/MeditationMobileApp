package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Game: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

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
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.nav_rewards -> {
                    startActivity(Intent(this, Rewards::class.java))
                    true
                }
                R.id.nav_game -> {
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.nav_game
        val button = findViewById<Button>(R.id.start_btn_id)
        button.setOnClickListener {
            // Start the WhaleGame activity
            val intent = Intent(this, WhaleGame::class.java)
            startActivity(intent)
    }
}
}