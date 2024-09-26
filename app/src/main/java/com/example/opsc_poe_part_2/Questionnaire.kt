package com.example.opsc_poe_part_2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Questionnaire : AppCompatActivity() {
    private lateinit var btnContinue: Button
    private lateinit var btnReduceStress: Button
    private lateinit var btnImprovePerformance: Button
    private lateinit var btnBetterSleep: Button
    private lateinit var btnIncreaseHappiness: Button
    private lateinit var btnImproveMentalHealth: Button
    private lateinit var btnOther: Button
    private lateinit var sharedPreferences: SharedPreferences

    private val selectedGoals = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)

        // Initializing buttons and SharedPreferences
        sharedPreferences = getSharedPreferences("UserGoals", MODE_PRIVATE)
        btnContinue = findViewById(R.id.btnContinue)
        btnReduceStress = findViewById(R.id.btnReduceStress)
        btnImprovePerformance = findViewById(R.id.btnImprovePerformance)
        btnBetterSleep = findViewById(R.id.btnBetterSleep)
        btnIncreaseHappiness = findViewById(R.id.btnIncreaseHappiness)
        btnImproveMentalHealth = findViewById(R.id.btnImproveMentalHealth)
        btnOther = findViewById(R.id.btnOther)

        // Handling button clicks and storing selected goals
        btnReduceStress.setOnClickListener {
            if (!selectedGoals.contains("Reduce Stress")) {
                selectedGoals.add("Reduce Stress")
            }
        }
        btnImprovePerformance.setOnClickListener {
            if (!selectedGoals.contains("Improve Performance")) {
                selectedGoals.add("Improve Performance")
            }
        }
        btnBetterSleep.setOnClickListener {
            if (!selectedGoals.contains("Better Sleep")) {
                selectedGoals.add("Better Sleep")
            }
        }
        btnIncreaseHappiness.setOnClickListener {
            if (!selectedGoals.contains("Increase Happiness")) {
                selectedGoals.add("Increase Happiness")
            }
        }
        btnImproveMentalHealth.setOnClickListener {
            if (!selectedGoals.contains("Improve Mental Health")) {
                selectedGoals.add("Improve Mental Health")
            }
        }
        btnOther.setOnClickListener {
            if (!selectedGoals.contains("Other")) {
                selectedGoals.add("Other")
            }
        }

        // Setting up the Continue button to save goals and redirect to DashboardActivity
        btnContinue.setOnClickListener {
            // Save selected goals to SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putStringSet("selected_goals", selectedGoals.toSet())
            editor.apply()

            // Intent to start DashboardActivity
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}
