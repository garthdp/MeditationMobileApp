package com.example.opsc_poe_part_2

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView

class VideoAdapter (private val context: Context, private val videoList: List<Int>) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoView: VideoView = itemView.findViewById(R.id.videoView)

        init {
            // Click listener to toggle play and pause
            itemView.setOnClickListener {
                if (videoView.isPlaying) {
                    videoView.pause()
                } else {
                    videoView.start()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoUri: Uri = Uri.parse("android.resource://${context.packageName}/${videoList[position]}")
        holder.videoView.setVideoURI(videoUri)
        holder.videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true // Loop the video
            holder.videoView.start()
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}