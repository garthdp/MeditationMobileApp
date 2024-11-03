package com.example.opsc_poe_part_2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.net.URL
import java.util.concurrent.Executors

var currentDiary: DiaryEntry? = null

class Dairy : AppCompatActivity() {
    private lateinit var addEntryButton: Button
    private lateinit var adapter: DiaryEntryAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dairy)
        addEntryButton = findViewById(R.id.button5)
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val email = currentUser?.email

        progressBar.visibility = View.VISIBLE
        if (email != null) {
            getDiaryEntries(email)
        }

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
        bottomNavigationView.selectedItemId = R.id.nav_diary
        addEntryButton.setOnClickListener {
            val intent = Intent(this, AddDiaryEntry::class.java)
            startActivity(intent)
        }


    }

    // gets diary entries
    @SuppressLint("Range")
    private fun getDiaryEntries(email: String){
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        val executor = Executors.newSingleThreadExecutor()
        val recyclerView : RecyclerView = findViewById(R.id.recycler_view_entries)
        recyclerView.layoutManager = LinearLayoutManager(this)
        executor.execute {
            try {
                //url for request
                val url = URL("https://opscmeditationapi.azurewebsites.net/api/Journal/GetJournalEntries?email=${email}")
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

                        adapter.setOnClickListener(object : DiaryEntryAdapter.OnClickListener{
                            override fun onClick(position: Int, model: DiaryEntry) {
                                currentDiary = model
                                val intent = Intent(this@Dairy, ViewDiaryEntry::class.java)
                                startActivity(intent)
                            }
                        })
                    }

                    val db = DBHelper(this, null)

                    db.deleteDiaries()

                    for(diary in dairyResponse){
                        db.addDiary(diary.Title, diary.Content, diary.Date, diary.Color)
                    }
                }
            } catch (e: Exception) {
                progressBar.visibility = View.INVISIBLE
                Handler(Looper.getMainLooper()).post{
                    val dairyResponse = ArrayList<DiaryEntry>()
                    val db = DBHelper(this, null)
                    val cursor = db.getEntries()
                    if (cursor!!.count > 0){
                        cursor.moveToFirst()
                        var diaryTitle = cursor.getString(cursor.getColumnIndex(DBHelper.TITLE))
                        var diaryContent = cursor.getString(cursor.getColumnIndex(DBHelper.CONTENT))
                        var diaryDate = cursor.getString(cursor.getColumnIndex(DBHelper.DATE))
                        var diaryId = cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL))
                        var color = cursor.getString(cursor.getColumnIndex(DBHelper.COLOR))
                        var diaryEntry = DiaryEntry(diaryId!!, diaryContent!!, diaryTitle!!, color!!, diaryDate!!)

                        Log.d("firest", diaryTitle)
                        dairyResponse.add(diaryEntry)
                        Log.d("Error DB", diaryEntry.toString())

                        while(cursor.moveToNext() == true){
                            diaryTitle = cursor.getString(cursor.getColumnIndex(DBHelper.TITLE))
                            diaryContent = cursor.getString(cursor.getColumnIndex(DBHelper.CONTENT))
                            diaryDate = cursor.getString(cursor.getColumnIndex(DBHelper.DATE))
                            diaryId = cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL))
                            color = cursor.getString(cursor.getColumnIndex(DBHelper.COLOR))
                            diaryEntry = DiaryEntry(diaryId!!, diaryContent!!, diaryTitle!!, color!!, diaryDate!!)
                            Log.d("diary", diaryEntry.toString())
                            dairyResponse.add(diaryEntry)
                        }
                        cursor.close()
                        val arrDairy = dairyResponse.toTypedArray()
                        // Initialize and set the adapter
                        for(diary in arrDairy){
                            Log.d("diary", diary.Title)
                        }
                        Handler(Looper.getMainLooper()).post{
                            // Initialize and set the adapter
                            adapter = DiaryEntryAdapter(arrDairy)
                            recyclerView.adapter = adapter
                            progressBar.visibility = View.INVISIBLE

                            adapter.setOnClickListener(object : DiaryEntryAdapter.OnClickListener{
                                override fun onClick(position: Int, model: DiaryEntry) {
                                    currentDiary = model
                                    val intent = Intent(this@Dairy, ViewDiaryEntry::class.java)
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}