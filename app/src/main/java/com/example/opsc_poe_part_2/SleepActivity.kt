package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class SleepActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)

        val videoList = listOf(
            //references videos from youtube:
            //https://youtu.be/o646ovhy66o?si=n1EyAgX2p3e3JNWf
            R.raw.sleep_meditate,
            //https://youtu.be/6If7zcLsEV4?feature=shared
            R.raw.whale_sound_one
        )
        val descriptions = listOf(
            "Sleep meditation guide",
            "Whale sounds to help you fall asleep"
        )

        // Find the back button in the layout
        val backButton=findViewById<ImageButton>(R.id.backButton)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val adapter = VideoAdapter(this, videoList)
        viewPager.adapter = adapter

        // Find the TextView for descriptions
        val descriptionTextView = findViewById<TextView>(R.id.videoDescription)

        // Set up the initial description
        descriptionTextView.text = descriptions[0]

        //  update the description
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Update the description when a new page is selected
                descriptionTextView.text = descriptions[position]
            }
        })

        // OnClickListener on the back button
        backButton.setOnClickListener {
            //navigate back to Mediation page
            val intent = Intent(this, Meditation::class.java)
            // Start MainActivity
            startActivity(intent)

            finish()
        }
    }
}