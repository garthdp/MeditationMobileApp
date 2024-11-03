package com.example.opsc_poe_part_2

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
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
import androidx.activity.enableEdgeToEdge
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
import java.util.concurrent.Executors

class Profile : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var txtSelectedGoals: TextView
    private lateinit var graph2: GraphView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)

        getUser()
        // Find the back button in the layout
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

        //  OnClickListener on the back button
        btnback1.setOnClickListener {
            // Navigate back to dashboard page
            val intent = Intent(this, Meditation::class.java)
            startActivity(intent)
            finish()
        }

        val btnSettings = findViewById<Button>(R.id.btnSetting)
        btnSettings.setOnClickListener {
            // Create an intent to start RegisterActivity
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

//        val imgpfp = findViewById<ImageView>(R.id.imageView13)
//        val btnpicChanger = findViewById<Button>(R.id.btnchangepfp)
//        btnpicChanger.setOnClickListener {
//            // Create an intent to start RegisterActivity
//            val options = arrayOf("Take Photo", "Choose from Gallery")
//            val builder = android.app.AlertDialog.Builder(this)
//            builder.setTitle("Select Option")
//            builder.setItems(options) { _, which ->
//                when (which) {
//                    0 -> openCamera() // Take a photo
//                    1 -> openGallery() // Choose from gallery
//                }
//                imgpfp.setImageResource(R.drawable.emoji2)
//            }
//            builder.show()
//        }
        // Set the graph data
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
    fun getUser(){
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        val picture = auth.currentUser?.photoUrl.toString()
        val profilePic = findViewById<ImageView>(R.id.imageView13)
        val txtName = findViewById<TextView>(R.id.lblName)
        val txtEmail = findViewById<TextView>(R.id.lblEmail)
        val txtLevel = findViewById<TextView>(R.id.lblLevel)
        val txtExperience = findViewById<TextView>(R.id.lblExperience)
        var decodedPicture: Bitmap? = null
        Log.d("Picture", decodedPicture.toString())
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            try {
                val url = URL("https://opscmeditationapi.azurewebsites.net/api/users?email=${email}")
                if(picture != "null"){
                    val `in` = java.net.URL(picture).openStream()
                    decodedPicture = BitmapFactory.decodeStream(`in`)
                }
                val json = url.readText()
                val res = Gson().fromJson(json, User::class.java)
                val db = DBHelper(this, null)

                db.deleteUser()

                db.addUser(res.Name, res.Level.toString(), res.Experience.toString(), res.Email)
                Log.d("Res", json)
                Handler(Looper.getMainLooper()).post {
                    txtName.text = res.Name
                    txtEmail.text = res.Email
                    txtLevel.text = res.Level.toString()
                    txtExperience.text = res.Experience.toString()
                    if(decodedPicture != null){
                        profilePic.setImageBitmap(decodedPicture)
                    }

                    progressBar.visibility = View.INVISIBLE
                }
            }
            catch (e: Exception){
                progressBar.visibility = View.INVISIBLE
                Handler(Looper.getMainLooper()).post{
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

    //Setup for the graph
    private fun setGraphData() {
        val dataPoints = ArrayList<DataPoint>()
        val dayLabels = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        for (i in 0 until 7) {
            val timeSpentInMinutes = sharedPreferences.getLong("day_$i", 0).toDouble()
            val timeSpentInHours = timeSpentInMinutes / 60.0
            dataPoints.add(DataPoint(i.toDouble(), timeSpentInHours))
        }

        val series = BarGraphSeries(dataPoints.toTypedArray())
        graph2.removeAllSeries()
        graph2.addSeries(series)


        graph2.title = "Time spent"
        graph2.gridLabelRenderer.verticalAxisTitle = "Time Spent (Hours)"
        graph2.gridLabelRenderer.horizontalAxisTitle = "Days"

        graph2.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    if (value.toInt() in dayLabels.indices) {
                        dayLabels[value.toInt()]
                    } else {
                        ""
                    }
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

        series.spacing = 50
        series.color = Color.parseColor("#6B8072")
        graph2.gridLabelRenderer.padding = 50
    }

}
