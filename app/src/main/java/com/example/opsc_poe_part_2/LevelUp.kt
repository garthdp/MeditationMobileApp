package com.example.opsc_poe_part_2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.time.LocalDate

class LevelUp : AppCompatActivity() {
    private val goalsList = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_level_up)
        sharedPreferences = getSharedPreferences("GoalPrefs", Context.MODE_PRIVATE)
        loadGoals()

        val currentDayOfWeek: Int = LocalDate.now().dayOfWeek.value

        if (goalsList == null){

            if (currentDayOfWeek == 1) {
                val imgday1 = findViewById<ImageView>(R.id.imgDay1)
                imgday1.setImageResource(R.drawable.starfishcolor)
            }
            if (currentDayOfWeek == 2) {
                val imgday2 = findViewById<ImageView>(R.id.imgDay2)
                imgday2.setImageResource(R.drawable.starfishcolor)
            }
            if (currentDayOfWeek == 3) {
                val imgday3 = findViewById<ImageView>(R.id.imgDay3)
                imgday3.setImageResource(R.drawable.starfishcolor)
            }
            if (currentDayOfWeek == 4) {
                val imgday4 = findViewById<ImageView>(R.id.imgDay4)
                imgday4.setImageResource(R.drawable.starfishcolor)
            }
            if (currentDayOfWeek == 5) {
                val imgday5 = findViewById<ImageView>(R.id.imgDay5)
                imgday5.setImageResource(R.drawable.starfishcolor)
            }
            if (currentDayOfWeek == 6) {
                val imgday6 = findViewById<ImageView>(R.id.imgDay6)
                imgday6.setImageResource(R.drawable.starfishcolor)
            }
            if (currentDayOfWeek == 7) {
                val imgday7 = findViewById<ImageView>(R.id.imgDay7)
                imgday7.setImageResource(R.drawable.starfishcolor)
            }

        }


        // Initialize BottomNavigationView and set up item selection listener
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_diary -> {
                    startActivity(Intent(this, Dairy::class.java))
                    true
                }
                R.id.nav_meditation -> {
                    startActivity(Intent(this, Meditation::class.java))
                    true
                }
                R.id.nav_rewards -> {
                    startActivity(Intent(this, Rewards::class.java))
                    true
                }
                R.id.nav_game -> {
                    startActivity(Intent(this, Game::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, Profile::class.java))
                    true
                }
                else -> false
            }


        }

    }

    private fun loadGoals() {
        val goalsJson = sharedPreferences.getString("goals", null)
        if (goalsJson != null) {
            val goalsType = object : TypeToken<MutableList<String>>() {}.getType()
            goalsList.addAll(gson.fromJson(goalsJson, goalsType))
//            adapter.notifyDataSetChanged()
        }

    }
}