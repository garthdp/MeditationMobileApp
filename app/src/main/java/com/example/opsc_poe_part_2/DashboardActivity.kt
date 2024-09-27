package com.example.opsc_poe_part_2

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
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

        // Find VideoView by ID and set the video background
        val videoView = findViewById<VideoView>(R.id.backgroundVideoView)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.star_background)
        videoView.setVideoURI(videoUri)

        // Start the video and loop it
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            videoView.start()
        }
        // Load theme preference
        val isDarkMode = loadThemePreference()
        toggleTheme(isDarkMode)

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        graph = findViewById(R.id.graph)

        val quoteImageView = findViewById<ImageView>(R.id.ivQuoteImage)

        // Restore the current quote index after rotation
        if (savedInstanceState != null) {
            currentQuoteIndex = savedInstanceState.getInt("currentQuoteIndex", 0)
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
                R.id.nav_diary -> startActivity(Intent(this, Dairy::class.java))
                R.id.nav_meditation -> startActivity(Intent(this, Meditation::class.java))
                R.id.nav_dashboard -> startActivity(Intent(this, DashboardActivity::class.java))
                R.id.nav_rewards -> startActivity(Intent(this, Rewards::class.java))
                R.id.nav_game -> startActivity(Intent(this, Game::class.java))
                else -> false
            }
            true
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

        val logout = findViewById<ImageButton>(R.id.logout_icon)
        logout.setOnClickListener {
            handleLogout()
        }

    }

    private fun handleLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { dialog, which ->
                // Clear SharedPreferences
                sharedPreferences.edit().clear().apply()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
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
        val dayLabels = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        for (i in 0 until 7) {
            val timeSpentInMinutes = sharedPreferences.getLong("day_$i", 0).toDouble()
            val timeSpentInHours = timeSpentInMinutes / 60.0
            dataPoints.add(DataPoint(i.toDouble(), timeSpentInHours))
        }

        val series = BarGraphSeries(dataPoints.toTypedArray())
        graph.removeAllSeries()
        graph.addSeries(series)

        graph.title = "Weekly Sessions"
        graph.gridLabelRenderer.verticalAxisTitle = "Time Spent (Hours)"
        graph.gridLabelRenderer.horizontalAxisTitle = "Days"

        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    if (value.toInt() in dayLabels.indices) {
                        dayLabels[value.toInt()]
                    } else {
                        ""
                    }
                } else {
                    String.format("%.1f h", value)
                }
            }
        }

        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMinY(0.0)
        graph.viewport.setMaxY(3.0)
        graph.gridLabelRenderer.numVerticalLabels = 6

        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(6.0)
        graph.gridLabelRenderer.numHorizontalLabels = 7

        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true

        series.spacing = 50
        series.color = Color.parseColor("#6B8072")
        graph.gridLabelRenderer.padding = 50
    }

    private fun updateDailyUsage(sessionDuration: Long) {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val todayKey = "day_$dayOfWeek"
        val currentTimeSpent = sharedPreferences.getLong(todayKey, 0)

        sharedPreferences.edit().putLong(todayKey, currentTimeSpent + sessionDuration).apply()
        setGraphData() // Call to update the graph data
    }
}
