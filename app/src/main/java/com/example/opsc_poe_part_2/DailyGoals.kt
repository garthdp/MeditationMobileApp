package com.example.opsc_poe_part_2

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class DailyGoals : AppCompatActivity() {

    private lateinit var goalTypeSpinner: Spinner
    private lateinit var troubleSpinner: Spinner
    private lateinit var goalDescriptionEditText: EditText
    private lateinit var saveGoalButton: Button
    private lateinit var goalsListView: ListView

    private val goalsList = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    private val REQUEST_NOTIFICATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_goals)

        // Check for notification permission
        checkNotificationPermission()

        // Initialize views
        goalTypeSpinner = findViewById(R.id.spinner_goal_type)
        troubleSpinner = findViewById(R.id.spinner_trouble)
        goalDescriptionEditText = findViewById(R.id.goal_description)
        saveGoalButton = findViewById(R.id.save_goal_button)
        goalsListView = findViewById(R.id.goals_list_view)

        // Populate Spinners
        val goalTypes = listOf("Meditation", "Focus", "Sleep")
        val troublingIssues = listOf("Stress", "Anxiety", "Lack of Focus", "Sleep Problems")

        goalTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, goalTypes)
        troubleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, troublingIssues)

        // Set up ListView adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, goalsList)
        goalsListView.adapter = adapter
        goalsListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        // Save Goal Button action
        saveGoalButton.setOnClickListener {
            val selectedGoalType = goalTypeSpinner.selectedItem.toString()
            val selectedTrouble = troubleSpinner.selectedItem.toString()
            val goalDescription = goalDescriptionEditText.text.toString()

            if (goalDescription.isNotEmpty()) {
                val goal = "$selectedGoalType - $selectedTrouble: $goalDescription"
                goalsList.add(goal)
                adapter.notifyDataSetChanged()
                showNotification(goal)  // Display notification
                goalDescriptionEditText.text.clear()  // Clear the text box
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
        bottomNavigationView.selectedItemId = R.id.nav_dashboard
        // Mark goals as completed by checking them off in the list
        goalsListView.setOnItemClickListener { _, _, position, _ ->
            goalsList.removeAt(position)
            adapter.notifyDataSetChanged()
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

    // Show a notification to remind the user of the goal
    private fun showNotification(goal: String) {
        val notificationId = 1
        val channelId = "goal_reminder_channel"

        // Create notification channel (for Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Goal Reminders"
            val descriptionText = "Reminders for your daily goals"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build and show the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.sademoji)  // Ensure ic_goal exists
            .setContentTitle("Goal Reminder")
            .setContentText("Reminder: $goal")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


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
