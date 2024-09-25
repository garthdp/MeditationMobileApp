package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Dairy : AppCompatActivity() {
    private lateinit var addEntryButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DiaryEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dairy)

        addEntryButton = findViewById(R.id.button5)
        recyclerView = findViewById(R.id.recycler_view_entries)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize and set the adapter
        adapter = DiaryEntryAdapter(DiaryEntries.entries)
        recyclerView.adapter = adapter

        // Call to display existing diary entries
        displayDiaryEntries()

        addEntryButton.setOnClickListener {
            val intent = Intent(this, AddDiaryEntry::class.java)
            startActivityForResult(intent, ADD_ENTRY_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ENTRY_REQUEST && resultCode == RESULT_OK) {
            // Notify the adapter about the new entry and refresh the entries displayed
            displayDiaryEntries()
        }
    }

    private fun displayDiaryEntries() {
        adapter.notifyDataSetChanged() // Notify the adapter to refresh the views
    }

    companion object {
        const val ADD_ENTRY_REQUEST = 1
    }
}
