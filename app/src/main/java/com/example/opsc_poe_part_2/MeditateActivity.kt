package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class MeditateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditate)

        val videoList = listOf(
            R.raw.meditation,
            R.raw.sea_meditation
        )

        val descriptions = listOf(
            "A short meditation guide",
            "A sea theme meditation guide"
        )

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

        //  OnClickListener on the back button
        backButton.setOnClickListener {
            //navigate back to Mediation page
            val intent = Intent(this, Meditation::class.java)
            // Start MainActivity
            startActivity(intent)

            finish()
        }
    }
}