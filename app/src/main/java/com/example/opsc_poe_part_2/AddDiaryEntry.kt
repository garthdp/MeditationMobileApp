package com.example.opsc_poe_part_2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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
            val newEntry = DiaryEntry(title, content, selectedEmojiResId)

            // Save to a static list
            DiaryEntries.entries.add(newEntry)

            // Set result to OK and pass back the new entry (optional)
            setResult(RESULT_OK)
            finish()
        }

    }

    override fun onEmojiSelected(emojiResId: Int) {
        selectedEmojiResId = emojiResId
        emojiTextView.setCompoundDrawablesWithIntrinsicBounds(0, emojiResId, 0, 0)
    }
}

// Singleton object to hold diary entries
object DiaryEntries {
    val entries = mutableListOf<DiaryEntry>()
}
data class DiaryEntry(
    val title: String,
    val content: String,
    val emojiResId: Int
)
