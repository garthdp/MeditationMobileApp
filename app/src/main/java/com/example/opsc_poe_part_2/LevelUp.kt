package com.example.opsc_poe_part_2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
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
        loadStickers()

        val currentDayOfWeek: Int = LocalDate.now().dayOfWeek.value
        val worked = sharedPreferences.getString("userWorked", null)

        if (currentDayOfWeek == 1){
            AreAllDaysDone()
        }


        if (worked == "yes"){
            if (goalsList.isEmpty()){

                if (currentDayOfWeek == 1) {
                    val imgday1 = findViewById<ImageView>(R.id.imgDay1)
                    imgday1.setImageResource(R.drawable.starfishcolor)
                    sharedPreferences.edit().putString("Day1", "yes").apply()
                }
                if (currentDayOfWeek == 2) {
                    val imgday2 = findViewById<ImageView>(R.id.imgDay2)
                    imgday2.setImageResource(R.drawable.starfishcolor)
                    sharedPreferences.edit().putString("Day2", "yes").apply()
                }
                if (currentDayOfWeek == 3) {
                    val imgday3 = findViewById<ImageView>(R.id.imgDay3)
                    imgday3.setImageResource(R.drawable.starfishcolor)
                    sharedPreferences.edit().putString("Day3", "yes").apply()
                }
                if (currentDayOfWeek == 4) {
                    val imgday4 = findViewById<ImageView>(R.id.imgDay4)
                    imgday4.setImageResource(R.drawable.starfishcolor)
                    sharedPreferences.edit().putString("Day4", "yes").apply()
                }
                if (currentDayOfWeek == 5) {
                    val imgday5 = findViewById<ImageView>(R.id.imgDay5)
                    imgday5.setImageResource(R.drawable.starfishcolor)
                    sharedPreferences.edit().putString("Day5", "yes").apply()
                }
                if (currentDayOfWeek == 6) {
                    val imgday6 = findViewById<ImageView>(R.id.imgDay6)
                    imgday6.setImageResource(R.drawable.starfishcolor)
                    sharedPreferences.edit().putString("Day6", "yes").apply()
                }
                if (currentDayOfWeek == 7) {
                    val imgday7 = findViewById<ImageView>(R.id.imgDay7)
                    imgday7.setImageResource(R.drawable.starfishcolor)
                    sharedPreferences.edit().putString("Day7", "yes").apply()
                }

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

    private fun AreAllDaysDone() {
        val Day1 = sharedPreferences.getString("Day1", null)
        val Day2 = sharedPreferences.getString("Day2", null)
        val Day3 = sharedPreferences.getString("Day3", null)
        val Day4 = sharedPreferences.getString("Day4", null)
        val Day5 = sharedPreferences.getString("Day5", null)
        val Day6 = sharedPreferences.getString("Day6", null)
        val Day7 = sharedPreferences.getString("Day7", null)
        val Week1 = sharedPreferences.getString("week1", null)
        val Week2 = sharedPreferences.getString("week2", null)
        val Week3 = sharedPreferences.getString("week3", null)

        if (Day1 == "yes" && Day2 == "yes" && Day3 == "yes" &&
            Day4 == "yes" && Day5 == "yes" && Day6 == "yes" &&
            Day7 == "yes") {
            // Display toast message
            val imgweek1 = findViewById<ImageView>(R.id.imgweek1)
            imgweek1.visibility = View.VISIBLE
            sharedPreferences.edit().putString("Week1", "yes").apply()
        }
        if (Day1 == "yes" && Day2 == "yes" && Day3 == "yes" &&
            Day4 == "yes" && Day5 == "yes" && Day6 == "yes" &&
            Day7 == "yes" && Week1 == "yes") {
            // Display toast message
            val imgweek2 = findViewById<ImageView>(R.id.imgweek2)
            imgweek2.visibility = View.VISIBLE
            sharedPreferences.edit().putString("Week2", "yes").apply()
        }
        if (Day1 == "yes" && Day2 == "yes" && Day3 == "yes" &&
            Day4 == "yes" && Day5 == "yes" && Day6 == "yes" &&
            Day7 == "yes" && Week1 == "yes"&& Week2 == "yes") {
            // Display toast message
            val imgweek3 = findViewById<ImageView>(R.id.imgweek3)
            imgweek3.visibility = View.VISIBLE
            sharedPreferences.edit().putString("Week2", "yes").apply()
        }
    }

    private fun loadStickers() {
        val Day1 = sharedPreferences.getString("Day1", null)
        val Day2 = sharedPreferences.getString("Day2", null)
        val Day3 = sharedPreferences.getString("Day3", null)
        val Day4 = sharedPreferences.getString("Day4", null)
        val Day5 = sharedPreferences.getString("Day5", null)
        val Day6 = sharedPreferences.getString("Day6", null)
        val Day7 = sharedPreferences.getString("Day7", null)
        val Week1 = sharedPreferences.getString("week1", null)
        val Week2 = sharedPreferences.getString("week2", null)
        val Week3 = sharedPreferences.getString("week3", null)


        if(Day1 == "yes"){
            val imgday1 = findViewById<ImageView>(R.id.imgDay1)
            imgday1.setImageResource(R.drawable.starfishcolor)
        }
        if(Day2 == "yes"){
            val imgday2 = findViewById<ImageView>(R.id.imgDay2)
            imgday2.setImageResource(R.drawable.starfishcolor)
        }
        if(Day3 == "yes"){
            val imgday3 = findViewById<ImageView>(R.id.imgDay3)
            imgday3.setImageResource(R.drawable.starfishcolor)
        }
        if(Day4 == "yes"){
            val imgday4 = findViewById<ImageView>(R.id.imgDay4)
            imgday4.setImageResource(R.drawable.starfishcolor)
        }
        if(Day5 == "yes"){
            val imgday5 = findViewById<ImageView>(R.id.imgDay5)
            imgday5.setImageResource(R.drawable.starfishcolor)
        }
        if(Day6 == "yes"){
            val imgday6 = findViewById<ImageView>(R.id.imgDay6)
            imgday6.setImageResource(R.drawable.starfishcolor)
        }
        if(Day7 == "yes"){
            val imgday7 = findViewById<ImageView>(R.id.imgDay7)
            imgday7.setImageResource(R.drawable.starfishcolor)
        }
        if(Week1 == "yes"){
            val imgweek1 = findViewById<ImageView>(R.id.imgweek1)
            imgweek1.visibility = View.VISIBLE
        }
        if(Week2 == "yes"){
            val imgweek2 = findViewById<ImageView>(R.id.imgweek2)
            imgweek2.visibility = View.VISIBLE
        }
        if(Week3 == "yes"){
            val imgweek3 = findViewById<ImageView>(R.id.imgweek3)
            imgweek3.visibility = View.VISIBLE
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
    private fun DayWorked() {
        val worked = "yes"
        sharedPreferences.edit().putString("userWorked", worked).apply()

    }
}