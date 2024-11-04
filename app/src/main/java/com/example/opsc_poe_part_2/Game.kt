package com.example.opsc_poe_part_2

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Game : AppCompatActivity() {
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
                R.id.nav_rewards -> {
                    startActivity(Intent(this, Rewards::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, Profile::class.java))
                    true
                }
                R.id.nav_game -> {
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.nav_game

        val startButton = findViewById<Button>(R.id.start_btn_id)
        startButton.setOnClickListener {
            val intent = Intent(this, WhaleGame::class.java)
            startActivity(intent)
        }

        val menuButton = findViewById<Button>(R.id.menu_btn_id)
        menuButton.setOnClickListener {
            showGameInfoDialog()
        }
    }

    private fun showGameInfoDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialoggameinfo, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)

        dialog.show()
    }
}
