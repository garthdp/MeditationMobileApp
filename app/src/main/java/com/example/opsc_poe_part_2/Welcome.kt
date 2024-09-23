package com.example.opsc_poe_part_2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import android.content.Intent
import android.view.View
import android.widget.Button

class Welcome : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        drawerLayout = findViewById(R.id.drawer_layout)
       // val toolbar: Toolbar = findViewById(R.id.toolbar)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        val openNavButton: Button = findViewById(R.id.open_nav_button)

        // Set the toolbar as the app bar for this activity
        //setSupportActionBar(toolbar)

        // Open drawer when button is clicked
        openNavButton.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dairy -> {
                    val intent = Intent(this, Dairy::class.java)
                    startActivity(intent)
                    true
                }
                R.id.daily_goals -> {
                    val intent = Intent(this, DailyGoals::class.java)
                    startActivity(intent)
                    true
                }
                // Add more navigation options here
                else -> false
            }
        }
    }
}
