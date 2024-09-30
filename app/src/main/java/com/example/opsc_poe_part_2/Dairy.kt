package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.Executors
import kotlin.math.log

class Dairy : AppCompatActivity() {
    private lateinit var addEntryButton: Button
    private lateinit var adapter: DiaryEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dairy)
        addEntryButton = findViewById(R.id.button5)
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        getDiaryEntries()

        // Initialize BottomNavigationView and set up item selection listener
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_diary -> {
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
        bottomNavigationView.selectedItemId = R.id.nav_diary
        addEntryButton.setOnClickListener {
            val intent = Intent(this, AddDiaryEntry::class.java)
            startActivity(intent)
        }
    }

    // gets diary entries
    private fun getDiaryEntries(){
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        val executor = Executors.newSingleThreadExecutor()
        val recyclerView : RecyclerView = findViewById(R.id.recycler_view_entries)
        recyclerView.layoutManager = LinearLayoutManager(this)
        executor.execute {
            try {
                //url for request
                val url = URL("https://opscmeditationapi.azurewebsites.net/api/Journal/GetJournalEntries?email=${userEmail}")
                val json = url.readText()
                // if null on response log error
                if(json.equals("null")){
                    progressBar.visibility = View.INVISIBLE
                    Log.d("Error", "Nothing found")
                }
                else{
                    // assigns response to array of diary entries
                    val dairyResponse = Gson().fromJson(json, Array<DiaryEntry>::class.java)
                    Log.d("Response", dairyResponse[0].toString())
                    Handler(Looper.getMainLooper()).post{
                        // Initialize and set the adapter
                        adapter = DiaryEntryAdapter(dairyResponse)
                        recyclerView.adapter = adapter
                        progressBar.visibility = View.INVISIBLE
                    }
                }
            } catch (e: Exception) {
                progressBar.visibility = View.INVISIBLE
                Log.d("Error", e.message.toString())
            }
        }
    }
}