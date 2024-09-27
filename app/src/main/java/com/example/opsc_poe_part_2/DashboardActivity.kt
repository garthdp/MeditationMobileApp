package com.example.opsc_poe_part_2

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jjoe64.graphview.DefaultLabelFormatter
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
    private var sessionStartTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Load theme preference
        val isDarkMode = loadThemePreference()
        toggleTheme(isDarkMode)

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
                else -> false
            }
        }

        // Set the graph data
        setGraphData()

        // Track session start time
        sessionStartTime = System.currentTimeMillis()

        // Theme toggle button
        val themeToggleButton = findViewById<ImageButton>(R.id.theme_toggle_button)
        themeToggleButton.setOnClickListener {
            val newIsDarkMode = !isDarkMode
            saveThemePreference(newIsDarkMode)
            toggleTheme(newIsDarkMode)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentQuoteIndex", currentQuoteIndex)
    }

    override fun onStop() {
        super.onStop()

        // Calculate session time in minutes
        val sessionEndTime = System.currentTimeMillis()
        val sessionDuration = (sessionEndTime - sessionStartTime) / 60000

        // Update the session time in SharedPreferences
        updateDailyUsage(sessionDuration)
    }

    private fun toggleTheme(isDarkMode: Boolean) {
        val themeToggleButton = findViewById<ImageButton>(R.id.theme_toggle_button)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            themeToggleButton.setImageResource(R.drawable.ic_light) // Set light mode icon
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            themeToggleButton.setImageResource(R.drawable.ic_dark) // Set dark mode icon
        }
    }

    private fun loadThemePreference(): Boolean {
        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isDarkMode", false)
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply()
    }

    private fun updateInteractionTime() {
        // Function for future use
    }

    private fun setGraphData() {
        val dataPoints = ArrayList<DataPoint>()

        // Day labels
        val dayLabels = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        for (i in 0 until 7) {
            // Retrieve time spent in minutes from SharedPreferences
            val timeSpentInMinutes = sharedPreferences.getLong("day_$i", 0).toDouble()
            val timeSpentInHours = timeSpentInMinutes / 60.0
            dataPoints.add(DataPoint(i.toDouble(), timeSpentInHours))
        }

        // Create a series for the graph
        val series = BarGraphSeries(dataPoints.toTypedArray())
        graph.removeAllSeries()
        graph.addSeries(series)

        // Set graph title and labels
        graph.title = "Weekly Sessions"
        graph.gridLabelRenderer.verticalAxisTitle = "Time Spent (Hours)"
        graph.gridLabelRenderer.horizontalAxisTitle = "Days"

        // Set custom labels for x-axis (days of the week)
        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    if (value.toInt() in dayLabels.indices) {
                        dayLabels[value.toInt()]
                    } else {
                        "" // Return empty string if out of bounds
                    }
                } else {
                    String.format("%.1f h", value)
                }
            }
        }

        // Set custom Y-axis range and labels
        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMinY(0.0) // Set minimum to 0 to make bars rise from the baseline
        graph.viewport.setMaxY(3.0) // Adjust as necessary to fit your data
        graph.gridLabelRenderer.numVerticalLabels = 6

        // Set custom X-axis bounds (0-6 for days of the week)
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(6.0)
        graph.gridLabelRenderer.numHorizontalLabels = 7

        // Enable scaling and scrolling
        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true

        // Set bar spacing to prevent bars from appearing too close together
        series.spacing = 50 // Adjust spacing to widen the gaps between bars

        // Set the color of the bars
        series.color = Color.parseColor("#6B8072")

        // Add padding to x-axis labels to avoid overlap with bars
        graph.gridLabelRenderer.padding = 50
    }
    private fun updateDailyUsage(sessionDuration: Long) {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        val todayKey = "day_$dayOfWeek"
        val currentTimeSpent = sharedPreferences.getLong(todayKey, 0)

        // Update the time spent for today
        sharedPreferences.edit().putLong(todayKey, currentTimeSpent + sessionDuration).apply()

        // Call to update the graph data
        setGraphData()
    }


    private fun handleLogout() {
        sharedPreferences.edit().clear().apply()

        val logout = findViewById<ImageButton>(R.id.logout_icon)
        logout.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }
    }
}
