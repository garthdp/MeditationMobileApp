package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class FocusActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus) // Ensure you have the layout file for music page


        // Find the back button in the layout
        val backButton=findViewById<ImageButton>(R.id.backButton)

        // Set an OnClickListener on the back button
        backButton.setOnClickListener {
            // Create an Intent to navigate back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            // Start MainActivity
            startActivity(intent)
            // Optionally finish MusicActivity to remove it from the back stack
            finish()
        }
    }
}