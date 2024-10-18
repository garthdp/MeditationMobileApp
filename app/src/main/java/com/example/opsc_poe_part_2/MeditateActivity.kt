package com.example.opsc_poe_part_2

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class MeditateActivity : AppCompatActivity() {
    private lateinit var swipeInstructionTextView: TextView
    private lateinit var arrowIndicator: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditate)

        val videoList = listOf(
            //references videos from youtube:
            //https://youtu.be/YIVUPknhUWs?si=6NbKQl2Ekm4STUh7
            R.raw.meditation,
            //https://youtu.be/DbDoBzGY3vo?si=yf6O7P2FSCutmfCX
            R.raw.sea_meditation
        )

        val descriptions = listOf(
            "A short meditation guide",
            "A sea theme meditation guide"
        )

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val adapter = VideoAdapter(this, videoList)
        viewPager.adapter = adapter

        //TextView for descriptions and swipe instruction
        val descriptionTextView = findViewById<TextView>(R.id.videoDescription)
        swipeInstructionTextView = findViewById(R.id.swipeInstruction)
        arrowIndicator = findViewById(R.id.arrowIndicator) // Initialize arrow ImageView
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

        // OnClickListener on the back button
        backButton.setOnClickListener {
            // Navigate back to Meditation page
            val intent = Intent(this, Meditation::class.java)
            startActivity(intent)
            finish()
        }

        // Start pulsing animation on the arrow indicator
        startPulsingAnimation()
    }

    private fun startPulsingAnimation() {
        // Create and start the scaling animation for the arrow
        val pulseAnimatorX = ObjectAnimator.ofFloat(arrowIndicator, "scaleX", 1f, 1.1f, 1f)
        val pulseAnimatorY = ObjectAnimator.ofFloat(arrowIndicator, "scaleY", 1f, 1.1f, 1f)

        pulseAnimatorX.duration = 500 // Duration of one pulse
        pulseAnimatorX.repeatCount = ObjectAnimator.INFINITE // Repeat indefinitely
        pulseAnimatorX.repeatMode = ObjectAnimator.REVERSE // Scale back to original size

        pulseAnimatorY.duration = 500 // Duration of one pulse
        pulseAnimatorY.repeatCount = ObjectAnimator.INFINITE // Repeat indefinitely
        pulseAnimatorY.repeatMode = ObjectAnimator.REVERSE // Scale back to original size

        // Start both animations
        pulseAnimatorX.start()
        pulseAnimatorY.start()
    }
}
