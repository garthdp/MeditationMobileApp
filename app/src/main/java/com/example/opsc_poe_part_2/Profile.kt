package com.example.opsc_poe_part_2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView

class Profile : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var txtSelectedGoals: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Set up window insets for edge-to-edge support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SharedPreferences and TextView

       // sharedPreferences = getSharedPreferences("UserGoals", MODE_PRIVATE)
       // txtSelectedGoals = findViewById(R.id.txtSelectedGoals)

        // Retrieve the selected goals from SharedPreferences
       // val selectedGoals = sharedPreferences.getStringSet("selected_goals", emptySet()) ?: emptySet()

        // Display the selected goals

       // if (selectedGoals.isNotEmpty()) {
       //     txtSelectedGoals.text = selectedGoals.joinToString(", ")
       // } else {
       //     txtSelectedGoals.text = "No goals selected."
       // }

        var lblEmail = findViewById<TextView>(R.id.lblemail)
        var lblPassword = findViewById<TextView>(R.id.lblpassword)
        var lblUsername = findViewById<TextView>(R.id.lblUsername)



        val btnSettings = findViewById<Button>(R.id.btnSetting)
        btnSettings.setOnClickListener {
            // Create an intent to start RegisterActivity
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

    }
}
