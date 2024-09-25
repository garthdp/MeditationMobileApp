package com.example.opsc_poe_part_2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class DiaryEntryAdapter(private val entries: List<DiaryEntry>) : RecyclerView.Adapter<DiaryEntryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val entryText: TextView = view.findViewById(R.id.entryText)
        val emojiView: ImageView = view.findViewById(R.id.emojiView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.entryText.text = entry.content
        // Assuming your emoji is stored as a drawable resource ID in the entry
        holder.emojiView.setImageResource(entry.emojiResId)
    }

    override fun getItemCount(): Int {
        return entries.size
    }
}
