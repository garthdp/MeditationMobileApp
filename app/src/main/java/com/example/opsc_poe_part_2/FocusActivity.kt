package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class FocusActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus)

        val videoList = listOf(
            R.raw.focus
        )

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