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
            startActivityForResult(intent, ADD_ENTRY_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ENTRY_REQUEST && resultCode == RESULT_OK) {
            // Notify the adapter about the new entry and refresh the entries displayed
            getDiaryEntries()
        }
    }

    private fun displayDiaryEntries() {
        adapter.notifyDataSetChanged() // Notify the adapter to refresh the views
    }
    override fun onResume() {
        super.onResume()
        // Fetch and display the diary entries every time the page is reloaded (resumed)
        getDiaryEntries()
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

                        // Call to display existing diary entries
                        displayDiaryEntries()
                    }
                }
            } catch (e: Exception) {
                Log.d("Error", "Fetch error occured")
            }
        }
    }

    companion object {
        const val ADD_ENTRY_REQUEST = 1
    }
}
