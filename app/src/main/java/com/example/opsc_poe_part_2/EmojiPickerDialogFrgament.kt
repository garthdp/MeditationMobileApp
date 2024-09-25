package com.example.opsc_poe_part_2

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
class EmojiPickerDialogFragment : DialogFragment() {

    interface EmojiPickerListener {
        fun onEmojiSelected(emojiResId: Int)
    }

    private lateinit var listener: EmojiPickerListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as EmojiPickerListener // Ensure the activity implements the interface
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an emoji")

        // Inflate the custom layout
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_emoji_picker, null)

        // Set up the emoji ImageViews and their click listeners
        view.findViewById<ImageView>(R.id.emoji1).setOnClickListener {
            listener.onEmojiSelected(R.drawable.sademoji)
            dismiss()
        }
        view.findViewById<ImageView>(R.id.emoji2).setOnClickListener {
            listener.onEmojiSelected(R.drawable.whalelogo)
            dismiss()
        }

        // Add more ImageView listeners as needed

        builder.setView(view)
        return builder.create()
    }
}
