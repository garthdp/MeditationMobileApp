package com.example.opsc_poe_part_2

import android.content.Intent
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

    private val selectedGoals = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)

        btnContinue = findViewById(R.id.btnContinue)
        btnReduceStress = findViewById(R.id.btnReduceStress)
        btnImprovePerformance = findViewById(R.id.btnImprovePerformance)
        btnBetterSleep = findViewById(R.id.btnBetterSleep)
        btnIncreaseHappiness = findViewById(R.id.btnIncreaseHappiness)
        btnImproveMentalHealth = findViewById(R.id.btnImproveMentalHealth)
        btnOther = findViewById(R.id.btnOther)

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

        btnContinue.setOnClickListener {
            // Pass the selected goals to DailyGoals activity
            val intent = Intent(this, Welcome::class.java)
            intent.putStringArrayListExtra("SELECTED_GOALS", ArrayList(selectedGoals))
            startActivity(intent)
        }
    }
}
