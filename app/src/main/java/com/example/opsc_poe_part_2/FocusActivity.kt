package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class FocusActivity: AppCompatActivity() {

    private lateinit var swipeInstructionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus)

        val videoList = listOf(
            //references videos from youtube:
            //https://youtu.be/Uo-GK0-Qh0Y?si=MkW0f9pEWw1GSU5A
            R.raw.focus,
            R.raw.siren,
            R.raw.sea_harmony
        )
        val descriptions = listOf(
            "A short guide to help you focus",
            "Mystical siren sounds",
            "Sea harmony tunes"
        )

        val backButton=findViewById<ImageButton>(R.id.backButton)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val adapter = VideoAdapter(this, videoList)
        viewPager.adapter = adapter

        //TextView for descriptions and swipe instruction
        val descriptionTextView = findViewById<TextView>(R.id.videoDescription)
        swipeInstructionTextView = findViewById(R.id.swipeInstruction)
        val videoCounterTextView = findViewById<TextView>(R.id.videoCounter)

        // Set up the initial description and video counter
        descriptionTextView.text = descriptions[0]
        videoCounterTextView.text = "1 of ${videoList.size}"  // Update to show the count

        // Update the description and video counter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Update the description and counter when a new page is selected
                descriptionTextView.text = descriptions[position]
                videoCounterTextView.text = "${position + 1} of ${videoList.size}"  // Update the counter

                // Hide the swipe instruction after the first swipe
                if (position == 1) {
                    swipeInstructionTextView.visibility = View.GONE
                }
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