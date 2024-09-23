package com.example.opsc_poe_part_2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class EmojiAdapter(private val context: Context, private val emojis: List<Int>) : BaseAdapter() {

    override fun getCount(): Int = emojis.size

    override fun getItem(position: Int): Any = emojis[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView
        if (convertView == null) {
            imageView = LayoutInflater.from(context).inflate(R.layout.item_emoji, parent, false) as ImageView
        } else {
            imageView = convertView as ImageView
        }

        imageView.setImageResource(emojis[position])
        return imageView
    }
}
