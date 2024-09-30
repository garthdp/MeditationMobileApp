package com.example.opsc_poe_part_2

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import java.util.concurrent.Executors

class DashboardActivity : AppCompatActivity() {

    private var currentQuoteIndex = 0
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var graph: GraphView
    private var sessionStartTime: Long = 0
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false
    private lateinit var imageView: ImageView
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)

        // Initialize VideoView for background video
        val videoView = findViewById<VideoView>(R.id.backgroundVideoView)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.star_background)
        videoView.setVideoURI(videoUri)

        // Initialize ImageView
        imageView = findViewById(R.id.imgQuote)
        startImageLoop()

        // Set OnClickListener for ImageView to change image on click
        imageView.setOnClickListener {
            changeImage()
        }

        // Restore the current quote index after rotation
        if (savedInstanceState != null) {
            currentQuoteIndex = savedInstanceState.getInt("currentQuoteIndex", 0)
            changeImage()
        }

        // Start the video and loop it
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            videoView.start()
        }

        // Initialize MediaPlayer for sound
        val playSoundIcon = findViewById<ImageButton>(R.id.play_sound_icon)
        mediaPlayer = MediaPlayer.create(this, R.raw.audio)

        // Set OnClickListener for sound control
        playSoundIcon.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
                playSoundIcon.setImageResource(R.drawable.ic_sound)
                isPlaying = false
            } else {
                mediaPlayer.start()
                playSoundIcon.setImageResource(R.drawable.ic_sound)
                isPlaying = true
            }
        }

        // Profile and settings icons
//        val profileIcon = findViewById<ImageButton>(R.id.profile_icon)
//        profileIcon.setOnClickListener {
//            startActivity(Intent(this, Profile::class.java))
//        }

        val settingsIcon = findViewById<ImageButton>(R.id.ic_settings)
        settingsIcon.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }

        // Initialize BottomNavigationView
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
                R.id.nav_dashboard -> true
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
        bottomNavigationView.selectedItemId = R.id.nav_dashboard

        sessionStartTime = System.currentTimeMillis()

        // Theme toggle
//        val themeToggleButton = findViewById<ImageButton>(R.id.theme_toggle_button)
//        val isDarkMode = loadThemePreference()
//        toggleTheme(isDarkMode)
//        themeToggleButton.setOnClickListener {
//            val newIsDarkMode = !isDarkMode
//            saveThemePreference(newIsDarkMode)
//            toggleTheme(newIsDarkMode)
//        }

        // Logout button
        val logout = findViewById<ImageButton>(R.id.logout_icon)
        logout.setOnClickListener {
            handleLogout()
        }

        // Set the graph data
        graph = findViewById(R.id.graph)
        setGraphData()
    }

    // Function to start image loop
    private fun startImageLoop() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                changeImage()
                handler.postDelayed(this, 300000) // Change image every 5 minutes
            }
        }
        handler.post(runnable)
    }

    // Change image
    private fun changeImage() {
        imageView.setImageResource(quoteImages[currentQuoteIndex])
        currentQuoteIndex = (currentQuoteIndex + 1) % quoteImages.size
    }

    // Logic for logging out
    private fun handleLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                sharedPreferences.edit().clear().apply()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Toggle between dark/light mode
   // private fun toggleTheme(isDarkMode: Boolean) {
//        val themeToggleButton = findViewById<ImageButton>(R.id.theme_toggle_button)
//        if (isDarkMode) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            themeToggleButton.setImageResource(R.drawable.ic_light)
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            themeToggleButton.setImageResource(R.drawable.ic_dark)
//        }
//    }
//
//    private fun loadThemePreference(): Boolean {
//        return sharedPreferences.getBoolean("isDarkMode", false)
//    }
//
//    private fun saveThemePreference(isDarkMode: Boolean) {
//        sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply()
//    }

    // Set the graph data
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
                    dayLabels.getOrNull(value.toInt()) ?: ""
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

    private fun getImage() {
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            // Load image in background task here
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentQuoteIndex", currentQuoteIndex)
    }
}
