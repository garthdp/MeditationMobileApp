package com.example.opsc_poe_part_2

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DailyGoals : AppCompatActivity() {
    private lateinit var goalsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_goals)

        goalsTextView = findViewById(R.id.goals_text_view) // Make sure to create this TextView in your layout

        // Get the selected goals from the Intent
        val selectedGoals = intent.getStringArrayListExtra("SELECTED_GOALS")

        // Display the selected goals
        if (selectedGoals != null && selectedGoals.isNotEmpty()) {
            goalsTextView.text = "Your Selected Goals:\n" + selectedGoals.joinToString("\n")
        } else {
            goalsTextView.text = "No goals selected."
        }
    }
}
