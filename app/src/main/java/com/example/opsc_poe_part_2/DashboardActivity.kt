package com.example.opsc_poe_part_2

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
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

    private var currentQuoteIndex = 0
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var graph: GraphView
    private var sessionStartTime: Long = 0
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false

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

        // Play Sound Icon
        val playSoundIcon = findViewById<ImageButton>(R.id.play_sound_icon)

        // Initialize MediaPlayer with your audio file in the raw folder
        mediaPlayer = MediaPlayer.create(this, R.raw.audio)

        // Set the OnClickListener for the playSoundIcon
        playSoundIcon.setOnClickListener {
            if (isPlaying) {
                // If sound is playing, stop it
                mediaPlayer.pause()
                mediaPlayer.seekTo(0) // Reset to the beginning of the track
                playSoundIcon.setImageResource(R.drawable.ic_sound) // Change icon if needed
                isPlaying = false
            } else {
                // If sound is not playing, start it
                mediaPlayer.start()
                playSoundIcon.setImageResource(R.drawable.ic_sound) // Change icon to indicate it's playing (if you have such an icon)
                isPlaying = true
            }
        }

        // Release the MediaPlayer when the activity is destroyed
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.seekTo(0)
            playSoundIcon.setImageResource(R.drawable.ic_sound) // Reset to original icon when finished
            isPlaying = false
        }

        // Load theme preference
        val isDarkMode = loadThemePreference()
        toggleTheme(isDarkMode)

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        graph = findViewById(R.id.graph)

        // Restore the current quote index after rotation
        if (savedInstanceState != null) {
            currentQuoteIndex = savedInstanceState.getInt("currentQuoteIndex", 0)
        }

        // Profile and settings icons
        val profileIcon = findViewById<ImageButton>(R.id.profile_icon)
        profileIcon.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }

        val settingsIcon = findViewById<ImageButton>(R.id.ic_settings)
        settingsIcon.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
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
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
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

        // Track session start time
        sessionStartTime = System.currentTimeMillis()

        // Theme toggle button
        val themeToggleButton = findViewById<ImageButton>(R.id.theme_toggle_button)
        themeToggleButton.setOnClickListener {
            val newIsDarkMode = !isDarkMode
            saveThemePreference(newIsDarkMode)
            toggleTheme(newIsDarkMode)
        }

        // Logout button
        val logout = findViewById<ImageButton>(R.id.logout_icon)
        logout.setOnClickListener {
            handleLogout()
        }

        // Set the graph data
        setGraphData()
    }

    // Save state when rotating the screen
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentQuoteIndex", currentQuoteIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the MediaPlayer resources
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }

    override fun onStop() {
        super.onStop()
        val sessionEndTime = System.currentTimeMillis()
        val sessionDuration = (sessionEndTime - sessionStartTime) / 60000
        updateDailyUsage(sessionDuration)
    }

    // Logic for logging out
    private fun handleLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { dialog, which ->
                sharedPreferences.edit().clear().apply()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Toggle between dark/light mode
    private fun toggleTheme(isDarkMode: Boolean) {
        val themeToggleButton = findViewById<ImageButton>(R.id.theme_toggle_button)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            themeToggleButton.setImageResource(R.drawable.ic_light)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            themeToggleButton.setImageResource(R.drawable.ic_dark)
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
        // Placeholder for tracking interaction time
    }

    // Setting graph data
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
        val previousMinutes = sharedPreferences.getLong(todayKey, 0)

        sharedPreferences.edit()
            .putLong(todayKey, previousMinutes + sessionDuration)
            .apply()

        setGraphData()
    }
}
