package com.example.opsc_poe_part_2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiaryEntryAdapter(private val entries: List<DiaryEntry>) :
    RecyclerView.Adapter<DiaryEntryAdapter.EntryViewHolder>() {

    class EntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.text_view_title)
        val content: TextView = view.findViewById(R.id.text_view_content)
        val emoji: TextView = view.findViewById(R.id.text_view_emoji)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        holder.title.text = entry.title
        holder.content.text = entry.content
        holder.emoji.setCompoundDrawablesWithIntrinsicBounds(0, entry.emojiResId, 0, 0)
    }

    override fun getItemCount() = entries.size
}
