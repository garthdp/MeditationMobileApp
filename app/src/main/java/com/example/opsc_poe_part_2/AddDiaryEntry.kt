package com.example.opsc_poe_part_2

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
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
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class AddDiaryEntry : AppCompatActivity() {

    private var emojiIndex = 0
    private val emojiImages = arrayOf(
        R.drawable.emoji1,
        R.drawable.emoji2,
        R.drawable.emoji3,
        R.drawable.emoji4,
    )

    private val backgroundColors = arrayOf(
        "#44CBC9", "#FFBB86FC", "#FF6200EE", "#FF03DAC5", "#FF3700B3",
        "#FFFF5722", "#FF4CAF50", "#FFFFEB3B", "#FF9C27B0", "#FFF44336"
    )

    private var colorIndex = 0
    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200

    // TextView to display the selected date
    private lateinit var dateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_diary_entry)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
        val changeColorButton = findViewById<Button>(R.id.changeColorButton)
        val cameraButton = findViewById<ImageButton>(R.id.cameraButton)
        val contentEditText = findViewById<EditText>(R.id.contentText)
        contentEditText.setText(ctrText)
        var color = ""
        // val imageView = findViewById<ImageView>(R.id.imgPreview)

        cameraButton.setOnClickListener {
            val intent = Intent(this, CameraTextReader::class.java)
            startActivity(intent)
        }

        // Back Button functionality
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            val titleEditText = findViewById<EditText>(R.id.titleText)
            val contentEditText = findViewById<EditText>(R.id.contentText)
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()

            setResult(RESULT_OK)

            levelUp()
            if(color == ""){
                color = "#44CBC9"
            }
            postRequest(content, title, color)

            val intent = Intent(this, Meditation::class.java)
            startActivity(intent)
        }

        // Change background color
        changeColorButton.setOnClickListener {
            colorIndex = (colorIndex + 1) % backgroundColors.size
            val selectedColor = Color.parseColor(backgroundColors[colorIndex])
            color = backgroundColors[colorIndex]
            constraintLayout.setBackgroundColor(selectedColor)
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    private fun showDatePickerDialog() {
        // Get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->

            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            dateTextView.text = formattedDate
        }, year, month, day)

        // Show the dialog
        datePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val photo = data?.extras?.get("data") as? Bitmap
                    val imgPreview = findViewById<ImageView>(R.id.imgPreview)
                    imgPreview.setImageBitmap(photo)
                    imgPreview.visibility = View.VISIBLE // Make it visible when an image is set
                }
                GALLERY_REQUEST_CODE -> {
                    val imageUri: Uri? = data?.data
                    val imgPreview = findViewById<ImageView>(R.id.imgPreview)
                    imgPreview.setImageURI(imageUri)
                    imgPreview.visibility = View.VISIBLE // Make it visible when an image is set
                }
            }
        } else {
            Toast.makeText(this, "Action canceled", Toast.LENGTH_SHORT).show()
        }
    }
    fun levelUp() {
        /*
            Code Attribution
            Title: How to use OKHTTP to make a post request in Kotlin?
            Author: heX
            Author Link: https://stackoverflow.com/users/11740298/hex
            Post Link: https://stackoverflow.com/questions/56893945/how-to-use-okhttp-to-make-a-post-request-in-kotlin
            Usage: learned how to make patch api requests
        */
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val progressData = workDataOf(LevelUpWorker.EXPERIENCE to 50.toString())

        Log.d("ProgressData", progressData.toString())
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<LevelUpWorker>()
                .setConstraints(constraints)
                .setInputData(progressData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()
        WorkManager.getInstance()
            .enqueue(request)
    }

    // adds diary
    fun postRequest(content: String, title: String, color: String) {
        /*
            Code Attribution
            Title: Handling Offline Network Request — WorkManager to the rescue
            Author: Pulkit Aggarwal
            Post Link: https://medium.com/coding-blocks/handling-offline-network-request-workmanager-to-the-rescue-613887cd3034
            Usage: learned how to make api requests offline
        */
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val progressData = workDataOf(AddDiaryEntryWorker.CONTENT to content, AddDiaryEntryWorker.TITLE to title, AddDiaryEntryWorker.COLOR to color)

        Log.d("ProgressData", progressData.toString())
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<AddDiaryEntryWorker>()
                .setConstraints(constraints)
                .setInputData(progressData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()
        val db = DBHelper(this, null)
        db.addDiary(title, content, LocalDate.now().toString(), color)
        WorkManager.getInstance()
            .enqueue(request)
    }
}