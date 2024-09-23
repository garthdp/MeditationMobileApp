package com.example.opsc_poe_part_2

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.DialogFragment

class EmojiPickerDialogFragment : DialogFragment() {

    private lateinit var emojiGridView: GridView
    private val emojis = listOf(
        R.drawable.sademoji
    )

    interface EmojiPickerListener {
        fun onEmojiSelected(emojiResId: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Optionally customize your dialog here
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_emoji_picker_dialog_frgament, container, false) // Corrected layout file name
        emojiGridView = view.findViewById(R.id.grid_view_emojis)

        emojiGridView.adapter = EmojiAdapter(requireContext(), emojis) // Ensure this adapter exists

        emojiGridView.setOnItemClickListener { _, _, position, _ ->
            (parentFragment as? EmojiPickerListener)?.onEmojiSelected(emojis[position])
            dismiss()
        }

        return view
    }
}
