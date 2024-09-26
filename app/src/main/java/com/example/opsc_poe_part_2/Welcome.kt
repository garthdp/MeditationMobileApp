package com.example.opsc_poe_part_2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.content.Intent
import android.widget.ImageButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class Welcome : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        drawerLayout = findViewById(R.id.drawer_layout)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_diary -> {
                    val intent = Intent(this, Dairy::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_meditation -> {
                    val intent = Intent(this, Meditation::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_dashboard -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_rewards -> {
                    val intent = Intent(this, Rewards::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_game -> {
                    val intent = Intent(this, Game::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_logout -> {
                    // Handle logout logic here
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, Settings::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
