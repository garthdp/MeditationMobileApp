package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        // Set the onClick listener to navigate to the RegisterActivity
        registerButton.setOnClickListener {
            // Create an intent to start RegisterActivity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
}