package com.example.opsc_poe_part_2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private val quoteImages = arrayOf(
        R.drawable.quote1,
        R.drawable.quote2,
        R.drawable.quote3,
        R.drawable.quote4,
        R.drawable.quote5,
        R.drawable.quote6,
        R.drawable.quote7,
        R.drawable.quote8,
        R.drawable.quote9,
        R.drawable.quote10
    )

    private var currentQuoteIndex = 0
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var graph: GraphView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isDarkMode = loadThemePreference()
        toggleTheme(isDarkMode)

        setContentView(R.layout.activity_dashboard)

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        graph = findViewById(R.id.graph)

        val quoteImageView = findViewById<ImageView>(R.id.ivQuoteImage)

        // Restore the current quote index after rotation
        if (savedInstanceState != null) {
            currentQuoteIndex = savedInstanceState.getInt("currentQuoteIndex", 0)
            quoteImageView.setImageResource(quoteImages[currentQuoteIndex])
        }

        quoteImageView.setImageResource(quoteImages[currentQuoteIndex])

        // Handle image change on click
        quoteImageView.setOnClickListener {
            currentQuoteIndex = (currentQuoteIndex + 1) % quoteImages.size
            quoteImageView.setImageResource(quoteImages[currentQuoteIndex])
            updateInteractionTime()
            setGraphData()
        }

        // Initialize BottomNavigationView and set up item selection listener
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
                    handleLogout()
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

        setGraphData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentQuoteIndex", currentQuoteIndex)
    }

    private fun toggleTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun loadThemePreference(): Boolean {
        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isDarkMode", false)
    }

    private fun updateInteractionTime() {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        val todayKey = "day_$dayOfWeek"
        val currentTimeSpent = sharedPreferences.getInt(todayKey, 0)
        sharedPreferences.edit().putInt(todayKey, currentTimeSpent + 1).apply()
    }

    private fun setGraphData() {
        val dataPoints = ArrayList<DataPoint>()

        // Retrieve data for each day of the week
        for (i in 0 until 7) {
            val timeSpent = sharedPreferences.getInt("day_$i", 0).toDouble()
            dataPoints.add(DataPoint(i.toDouble(), timeSpent))
        }

        val series = BarGraphSeries(dataPoints.toTypedArray())
        graph.removeAllSeries()
        graph.addSeries(series)

        graph.title = "Weekly Sessions"
        graph.gridLabelRenderer.verticalAxisTitle = "Hours Spent"
        graph.gridLabelRenderer.horizontalAxisTitle = "Days"
    }

    private fun handleLogout() {
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}
