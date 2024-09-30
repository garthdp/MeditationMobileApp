package com.example.opsc_poe_part_2

import android.annotation.SuppressLint
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
            .inflate(R.layout.diary_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.entryText.text = entry.Title
        holder.entryContent.text = entry.Content
        holder.date.text = "DATE: ${entry.Date}"
        holder.emojiView.setImageResource(entry.emoji)
    }

    fun updateEntries(newEntries: Array<DiaryEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return entries.size
    }
}
