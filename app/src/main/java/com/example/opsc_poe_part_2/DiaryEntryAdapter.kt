package com.example.opsc_poe_part_2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class DiaryEntryAdapter(private var entries: Array<DiaryEntry>) : RecyclerView.Adapter<DiaryEntryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val entryText: TextView = view.findViewById(R.id.entryTitle)
        val emojiView: ImageView = view.findViewById(R.id.emojiView)
        val entryContent: TextView = view.findViewById(R.id.entryContent)
        val date: TextView = view.findViewById(R.id.entryDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.entryText.text = entry.Title
        holder.entryContent.text = entry.Content
        holder.date.text = entry.Date
        // Assuming your emoji is stored as a drawable resource ID in the entry
        holder.emojiView.setImageResource(entry.emoji)
    }
    fun updateEntries(newEntries: Array<DiaryEntry>) {
        entries = newEntries
        notifyDataSetChanged()  // Notify the adapter that the data has changed
    }
    override fun getItemCount(): Int {
        return entries.size
    }
}
