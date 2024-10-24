package com.example.opsc_poe_part_2

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.time.LocalDateTime

class DailyGoals : AppCompatActivity() {

    private lateinit var goalTypeSpinner: Spinner
    private lateinit var troubleSpinner: Spinner
    private lateinit var goalDescriptionEditText: EditText
    private lateinit var saveGoalButton: Button
    private lateinit var goalsListView: ListView
    private lateinit var goalAlarmScheduler: GoalAlarmScheduler

    private val goalsList = mutableListOf<String>()
    private val alarmList = mutableListOf<AlarmItem>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sharedPreferences: SharedPreferences

    private val gson = Gson()
    private val REQUEST_NOTIFICATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_goals)
        sharedPreferences = getSharedPreferences("GoalPrefs", Context.MODE_PRIVATE)

        // Check for notification permission
        checkNotificationPermission()

        // Initialize views
        goalTypeSpinner = findViewById(R.id.spinner_goal_type)
        troubleSpinner = findViewById(R.id.spinner_trouble)
        goalDescriptionEditText = findViewById(R.id.goal_description)
        saveGoalButton = findViewById(R.id.save_goal_button)
        goalsListView = findViewById(R.id.goals_list_view)
        goalAlarmScheduler = GoalAlarmScheduler(this)

        // Populate Spinners
        val goalTypes = listOf("Meditation", "Focus", "Sleep")
        val troublingIssues = listOf("Stress", "Anxiety", "Lack of Focus", "Sleep Problems")

        goalTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, goalTypes)
        troubleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, troublingIssues)

        // Set up ListView adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, goalsList)
        goalsListView.adapter = adapter
        goalsListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        // loads lists from shared preferences
        loadGoalsAndAlarms()

        // Save Goal Button action
        saveGoalButton.setOnClickListener {
            val selectedGoalType = goalTypeSpinner.selectedItem.toString()
            val selectedTrouble = troubleSpinner.selectedItem.toString()
            val goalDescription = goalDescriptionEditText.text.toString()

            if (goalDescription.isNotEmpty()) {
                val goal = "$selectedGoalType - $selectedTrouble: $goalDescription"
                goalsList.add(goal)
                adapter.notifyDataSetChanged()
                // sets alarm for 1 hr
                val seconds = LocalDateTime.now().plusSeconds(60 * 60)
                val alarmItem = AlarmItem(goal, seconds)
                // save alarmitems to list
                alarmList.add(alarmItem)
                // schedules alarms
                goalAlarmScheduler.schedule(alarmItem)
                goalDescriptionEditText.text.clear()  // Clear the text box
                saveGoalsAndAlarms()
            } else {
                Toast.makeText(this, "Please enter a goal description", Toast.LENGTH_SHORT).show()
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
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
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
                else -> false
            }
        }
        // Mark goals as completed by checking them off in the list
        goalsListView.setOnItemClickListener { _, _, position, _ ->
            levelUp()
            // finds alarm item and cancels notification
            val alarmItemFound = alarmList[position]
            goalAlarmScheduler.cancel(alarmItemFound)
            goalsList.removeAt(position)
            adapter.notifyDataSetChanged()
            saveGoalsAndAlarms()
        }
    }

    /*
    Code Attribution
    Title: Storing Array List Object in SharedPreferences
    Author: Sinan Kozak
    author link : https://stackoverflow.com/users/1577792/sinan-kozak
    Post Link: https://stackoverflow.com/questions/22984696/storing-array-list-object-in-sharedpreferences
    Usage: learned to save list in sharedpreferences
    */

    // saves lists to shared preferences
    private fun saveGoalsAndAlarms() {
        val goalsJson = gson.toJson(goalsList)
        sharedPreferences.edit().putString("goals", goalsJson).apply()

        val alarmsJson = gson.toJson(alarmList)
        sharedPreferences.edit().putString("alarms", alarmsJson).apply()
    }

    // loads lists from shared preferences and saves them to lists
    private fun loadGoalsAndAlarms() {
        val goalsJson = sharedPreferences.getString("goals", null)
        if (goalsJson != null) {
            val goalsType = object : TypeToken<MutableList<String>>() {}.getType()
            goalsList.addAll(gson.fromJson(goalsJson, goalsType))
            adapter.notifyDataSetChanged()
        }

        val alarmsJson = sharedPreferences.getString("alarms", null)
        if (alarmsJson != null) {
            val alarmsType = object : TypeToken<MutableList<AlarmItem>>() {}.type
            alarmList.addAll(gson.fromJson(alarmsJson, alarmsType))
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }

    // levels user up
    fun levelUp() {
        /*
            Code Attribution
            Title: How to use OKHTTP to make a post request in Kotlin?
            Author: heX
            Author Link: https://stackoverflow.com/users/11740298/hex
            Post Link: https://stackoverflow.com/questions/56893945/how-to-use-okhttp-to-make-a-post-request-in-kotlin
            Usage: learned how to make patch api requests
        */
        val client = OkHttpClient()

        //gets url and adds parameters
        val url = "https://opscmeditationapi.azurewebsites.net/api/users/updateLevel".toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("email", userEmail)
            .addQueryParameter("experience", "50")
            .build()

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), "")

        // builds request
        val request = Request.Builder().url(url).patch(requestBody).build()

        // does call
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Patch Response", "Failure")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Patch Response", "Success")
            }
        } )
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
