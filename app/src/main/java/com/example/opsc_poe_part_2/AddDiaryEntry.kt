package com.example.opsc_poe_part_2

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*

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
        val emojiButton = findViewById<ImageButton>(R.id.emojiButton)
        val changeColorButton = findViewById<Button>(R.id.changeColorButton)
        val cameraButton = findViewById<ImageButton>(R.id.cameraButton)
        val imageView = findViewById<ImageView>(R.id.imgPreview)

        // Initialize the dateTextView
        dateTextView = findViewById(R.id.dateTextView)

        // Back Button functionality
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Calendar button
        val calendarButton = findViewById<ImageButton>(R.id.calendarButton)
        calendarButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Emoji cycling logic
        emojiButton.setOnClickListener {
            emojiIndex = (emojiIndex + 1) % emojiImages.size
            emojiButton.setImageResource(emojiImages[emojiIndex])
        }

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            val selectedEmoji = emojiImages[emojiIndex]
            finish()
        }

        // Change background color
        changeColorButton.setOnClickListener {
            colorIndex = (colorIndex + 1) % backgroundColors.size
            val selectedColor = Color.parseColor(backgroundColors[colorIndex])
            constraintLayout.setBackgroundColor(selectedColor)
        }

        // Open camera or gallery
        cameraButton.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Select Option")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera() // Take a photo
                    1 -> openGallery() // Choose from gallery
                }
            }
            builder.show()
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
}
