package com.example.opsc_poe_part_2

import android.os.Bundle
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

            GlobalScope.launch(Dispatchers.IO) {
                val check = async { patchRequest() }
                check.await()
                val addUser = async { postRequest(content, title, selectedEmojiResId) }
                addUser.await()
            }
            finish()
        }

    }

    override fun onEmojiSelected(emojiResId: Int) {
        selectedEmojiResId = emojiResId
        emojiTextView.setCompoundDrawablesWithIntrinsicBounds(0, emojiResId, 0, 0)
    }

    suspend fun patchRequest() {
        /*
            Code Attribution
            Title: How to use the OkHttp library in Android Studio
            Author: Codes Easy
            Link: https://www.youtube.com/watch?v=uSY2RqdBL04
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
        val executor = Executors.newSingleThreadExecutor()

        executor.execute{
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                println(response.body?.string())
            }
        }
        delay(3000)
    }

    suspend fun postRequest(content: String, title: String, emoji: Int) {
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
        val executor = Executors.newSingleThreadExecutor()

        executor.execute{
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                println(response.body?.string())
            }
        }
        delay(3000)
    }
}

// Singleton object to hold diary entries
object DiaryEntries {
    val entries = mutableListOf<DiaryEntry>()
}
data class DiaryEntry(
    val emoji: Int,
    val EntryId: String,
    val Content: String,
    val Title: String,
    val Date: String
)
