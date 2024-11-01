package com.example.opsc_poe_part_2

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class DiaryEntryAdapter(private var entries: Array<DiaryEntry>) : RecyclerView.Adapter<DiaryEntryAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val entryText: TextView = view.findViewById(R.id.entryTitle)
        val entryContent: TextView = view.findViewById(R.id.entryContent)
        val date: TextView = view.findViewById(R.id.entryDate)
        val background : CardView = view.findViewById(R.id.diaryItemCard)

        /*
            Code Attribution
            Title: OnClickListener is not working inside my adapter class
            Author: rahat
            author Link: https://stackoverflow.com/users/9701793/rahat
            Post Link: https://stackoverflow.com/questions/65521667/onclicklistener-is-not-working-inside-my-adapter-class
            Usage: used this and previous code from semester 1 to make onclick work
        */
        init{
            view.setOnClickListener{
                onClickListener?.onClick(adapterPosition, entries[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.diary_item, parent, false)
        return ViewHolder(view)
    }
    interface OnClickListener {
        fun onClick(position: Int, model: DiaryEntry)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.entryText.text = entry.Title
        holder.entryContent.text = entry.Content
        holder.date.text = "DATE: ${entry.Date}"
        Log.d("Color", entry.Color)
        holder.background.setCardBackgroundColor(Color.parseColor(entry.Color))
    }

    fun updateEntries(newEntries: Array<DiaryEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return entries.size
    }
}
