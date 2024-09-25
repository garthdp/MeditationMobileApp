package com.example.opsc_poe_part_2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton

class Welcome : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        val openNavButton: ImageButton = findViewById(R.id.open_nav_button) // Change to ImageButton


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
                    drawerLayout.closeDrawer(navigationView) // Close drawer after selection
                    true
                }
                R.id.daily_goals -> {
                    val intent = Intent(this, DailyGoals::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(navigationView) // Close drawer after selection
                    true
                }
                // Add more navigation options here
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView) // Close drawer if it's open
        } else {
            super.onBackPressed() // Default behavior
        }
    }
}
