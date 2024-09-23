package com.example.opsc_poe_part_2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Find VideoView by ID and set the video background
        val videoView = findViewById<VideoView>(R.id.backgroundVideoView)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.whalebackround) // Replace with your video file name in res/raw
        videoView.setVideoURI(videoUri)

        // Start the video and loop it
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            videoView.start()
        }

        // Adjust padding for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the register button by ID
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val loginButton = findViewById<Button>(R.id.btnLogin)

        // Set the onClick listener to navigate to the RegisterActivity
        registerButton.setOnClickListener {
            // Create an intent to start RegisterActivity
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}

