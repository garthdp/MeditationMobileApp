package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.net.URL
import java.util.concurrent.Executors
import kotlin.math.log

class Dairy : AppCompatActivity() {
    private lateinit var addEntryButton: Button
    private lateinit var adapter: DiaryEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dairy)
        addEntryButton = findViewById(R.id.button5)

        getDiaryEntries()

        addEntryButton.setOnClickListener {
            val intent = Intent(this, AddDiaryEntry::class.java)
            startActivity(intent)
        }
    }

    private fun getDiaryEntries(){
        val executor = Executors.newSingleThreadExecutor()
        val recyclerView : RecyclerView = findViewById(R.id.recycler_view_entries)
        recyclerView.layoutManager = LinearLayoutManager(this)
        executor.execute {
            try {
                val url = URL("https://opscmeditationapi.azurewebsites.net/api/Journal/GetJournalEntries?email=${userEmail}")
                val json = url.readText()
                if(json.equals("null")){
                    Log.d("Error", "Nothing found")
                }
                else{
                    val dairyResponse = Gson().fromJson(json, Array<DiaryEntry>::class.java)
                    Log.d("Response", dairyResponse[0].toString())
                    Handler(Looper.getMainLooper()).post{
                        // Initialize and set the adapter
                        adapter = DiaryEntryAdapter(dairyResponse)
                        recyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                Log.d("Error", "Fetch error occured")
            }
        }
    }
}
