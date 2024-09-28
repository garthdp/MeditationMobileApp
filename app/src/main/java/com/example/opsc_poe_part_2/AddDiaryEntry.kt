package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.concurrent.Executors

class AddDiaryEntry : AppCompatActivity(), EmojiPickerDialogFragment.EmojiPickerListener {
    private lateinit var saveButton: Button
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var emojiTextView: TextView
    private var selectedEmojiResId: Int = 0 // Store selected emoji ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_diary_entry)

        saveButton = findViewById(R.id.btn_save)
        titleEditText = findViewById(R.id.edit_text_title)
        contentEditText = findViewById(R.id.edit_text_content)
        emojiTextView = findViewById(R.id.text_view_selected_emoji)

        findViewById<ImageButton>(R.id.btn_select_emoji).setOnClickListener {
            val emojiPickerDialog = EmojiPickerDialogFragment()
            emojiPickerDialog.show(supportFragmentManager, "emoji_picker")
        }

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()

            // Create a new DiaryEntry object
            //val newEntry = DiaryEntry(title, content, selectedEmojiResId)

            // Save to a static list
            //DiaryEntries.entries.add(newEntry)

            // Set result to OK and pass back the new entry (optional)
            setResult(RESULT_OK)

            patchRequest()
            postRequest(content, title, selectedEmojiResId)
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onEmojiSelected(emojiResId: Int) {
        selectedEmojiResId = emojiResId
        emojiTextView.setCompoundDrawablesWithIntrinsicBounds(0, emojiResId, 0, 0)
    }

    fun patchRequest() {
        /*
            Code Attribution
            Title: How to use OKHTTP to make a post request in Kotlin?
            Author: heX
            Author Link: https://stackoverflow.com/users/11740298/hex
            Post Link: https://stackoverflow.com/questions/56893945/how-to-use-okhttp-to-make-a-post-request-in-kotlin
            Usage: learned how to make patch api requests
        */
        val client = OkHttpClient()

        val url = "https://opscmeditationapi.azurewebsites.net/api/users/updateLevel".toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("email", userEmail)
            .addQueryParameter("experience", "50")
            .build()

        // Create a dummy request body as PATCH requires a body, even if it's empty
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), "")

        val request = Request.Builder().url(url).patch(requestBody).build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Patch Response", "Failure")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Patch Response", "Success")
            }
        } )
    }

    fun postRequest(content: String, title: String, emoji: Int) {
        val client = OkHttpClient()

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = sdf.format(Date())
        val url = "https://opscmeditationapi.azurewebsites.net/api/Journal".toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("email", userEmail)
            .addQueryParameter("date", currentDate)
            .addQueryParameter("content", content)
            .addQueryParameter("emoji", emoji.toString())
            .addQueryParameter("title", title)
            .build()


        // Create a dummy request body as PATCH requires a body, even if it's empty
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), "")

        val request = Request.Builder().url(url).post(requestBody).build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Patch Response", "Failure")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Patch Response", "Success")
            }
        } )
    }
}

data class DiaryEntry(
    val emoji: Int,
    val EntryId: String,
    val Content: String,
    val Title: String,
    val Date: String
)
