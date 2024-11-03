package com.example.opsc_poe_part_2

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import java.net.URL
import java.util.ArrayList
import java.util.Calendar
import java.util.concurrent.Executors

class Profile : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var txtSelectedGoals: TextView
    private lateinit var graph2: GraphView
    private lateinit var auth: FirebaseAuth
    private var sessionStartTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)

        sessionStartTime = System.currentTimeMillis() // Set session start time

        getUser()
        val btnback1 = findViewById<Button>(R.id.btnback1)

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
                else -> false
            }
        }

        btnback1.setOnClickListener {
            val intent = Intent(this, Meditation::class.java)
            startActivity(intent)
            finish()
        }

        val btnSettings = findViewById<Button>(R.id.btnSetting)
        btnSettings.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        graph2 = findViewById(R.id.graph2)
        setGraphData()
    }

    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    @SuppressLint("Range")
    private fun getUser(){
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        auth = FirebaseAuth.getInstance()
        val picture = auth.currentUser?.photoUrl.toString()
        val email = auth.currentUser?.email
        val profilePic = findViewById<ImageView>(R.id.imageView13)
        val txtName = findViewById<TextView>(R.id.lblName)
        val txtEmail = findViewById<TextView>(R.id.lblEmail)
        val txtLevel = findViewById<TextView>(R.id.lblLevel)
        val txtExperience = findViewById<TextView>(R.id.lblExperience)
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            try {
                val url = URL("https://opscmeditationapi.azurewebsites.net/api/users?email=${email}")
                val `in` = URL(picture).openStream()
                val decodePicture = BitmapFactory.decodeStream(`in`)
                val json = url.readText()
                val res = Gson().fromJson(json, User::class.java)

                val db = DBHelper(this, null)
                db.deleteDiaries()
                db.addUser(res.Name, res.Level.toString(), res.Experience.toString(), res.Email)
                Log.d("Res", json)

                Handler(Looper.getMainLooper()).post {
                    txtName.text = res.Name
                    txtEmail.text = res.Email
                    txtLevel.text = res.Level.toString()
                    txtExperience.text = res.Experience.toString()
                    profilePic.setImageBitmap(decodePicture)
                    progressBar.visibility = View.INVISIBLE
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    progressBar.visibility = View.INVISIBLE
                    val db = DBHelper(this, null)
                    val cursor = db.getUser()
                    cursor?.moveToFirst()
                    val name = cursor?.getString(cursor.getColumnIndex(DBHelper.NAME))
                    val dbEmail = cursor?.getString(cursor.getColumnIndex(DBHelper.EMAIL))
                    val level = cursor?.getString(cursor.getColumnIndex(DBHelper.LEVEL))
                    val experience = cursor?.getString(cursor.getColumnIndex(DBHelper.EXPERIENCE))
                    val user = User(experience!!.toInt(), level!!.toInt(), dbEmail!!, name!!)

                    txtName.text = user.Name
                    txtEmail.text = user.Email
                    txtLevel.text = user.Level.toString()
                    txtExperience.text = user.Experience.toString()
                    cursor?.close()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val sessionEndTime = System.currentTimeMillis()
        val sessionDuration = (sessionEndTime - sessionStartTime) / 60000

        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        val previousTime = sharedPreferences.getLong("day_$dayOfWeek", 0)
        val updatedTime = previousTime + sessionDuration

        sharedPreferences.edit().putLong("day_$dayOfWeek", updatedTime).apply()
    }

    private fun setGraphData() {
        val dataPoints = ArrayList<DataPoint>()
        val dayLabels = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        for (i in 0 until 7) {
            val timeSpentInMinutes = sharedPreferences.getLong("day_$i", 0).toDouble()
            val timeSpentInHours = timeSpentInMinutes / 60.0
            dataPoints.add(DataPoint(i.toDouble(), timeSpentInHours))
        }

        val series = BarGraphSeries(dataPoints.toTypedArray())
        graph2.removeAllSeries()
        graph2.addSeries(series)

        graph2.title = "Weekly App Usage"
        graph2.gridLabelRenderer.verticalAxisTitle = "Time Spent (Hours)"
        graph2.gridLabelRenderer.horizontalAxisTitle = "Days"

        graph2.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    dayLabels.getOrNull(value.toInt()) ?: ""
                } else {
                    String.format("%.1f h", value)
                }
            }
        }

        graph2.viewport.isYAxisBoundsManual = true
        graph2.viewport.setMinY(0.0)
        graph2.viewport.setMaxY(3.0)
        graph2.gridLabelRenderer.numVerticalLabels = 6

        graph2.viewport.isXAxisBoundsManual = true
        graph2.viewport.setMinX(0.0)
        graph2.viewport.setMaxX(6.0)
        graph2.gridLabelRenderer.numHorizontalLabels = 7

        graph2.viewport.isScalable = true
        graph2.viewport.isScrollable = true

        series.color = Color.rgb(102, 153, 255)
    }
}
