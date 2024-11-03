package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Meditation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_diary -> {
                    startActivity(Intent(this, Dairy::class.java))
                    true
                }
                R.id.nav_meditation -> {
                    // Stays on the current page
                    true
                }
                R.id.nav_rewards -> {
                    startActivity(Intent(this, Rewards::class.java))
                    true
                }
                R.id.nav_game -> {
                    startActivity(Intent(this, Game::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.nav_meditation
    }

    // Navigation to specific pages using button clicks
    fun goToMusicPage(view: View) {
        val intent = Intent(this, MusicActivity::class.java)
        startActivity(intent)
    }

    fun goToFocusPage(view: View) {
        val intent = Intent(this, FocusActivity::class.java)
        startActivity(intent)
    }

    fun goToSleepPage(view: View) {
        val intent = Intent(this, SleepActivity::class.java)
        startActivity(intent)
    }

    fun goToMeditationPage(view: View) {
        val intent = Intent(this, MeditateActivity::class.java)
        startActivity(intent)
    }
}
