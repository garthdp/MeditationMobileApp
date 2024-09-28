package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class MusicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        val videoList = listOf(
            R.raw.whale_sound_one,
            R.raw.whale_sound_two
        )

        // Find the back button in the layout
        val backButton=findViewById<ImageButton>(R.id.backButton)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val adapter = VideoAdapter(this, videoList)
        viewPager.adapter = adapter

        // Set an OnClickListener on the back button
        backButton.setOnClickListener {
            //navigate back to Mediation page
            val intent = Intent(this, Meditation::class.java)
            // Start MainActivity
            startActivity(intent)

            finish()
        }
    }
}