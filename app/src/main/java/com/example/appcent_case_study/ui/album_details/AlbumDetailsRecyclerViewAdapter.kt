package com.example.appcent_case_study.ui.album_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.R
import com.example.appcent_case_study.my_classes.Track
import com.squareup.picasso.Picasso

class AlbumDetailsRecyclerViewAdapter(private var data: List<Track>, var imgUrl: String) : RecyclerView.Adapter<AlbumDetailsRecyclerViewAdapter.TrackViewHolder>() {


    class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        var image : ImageView = itemView.findViewById(R.id.cover_image_view)
        var track_title : TextView = itemView.findViewById(R.id.track_title_text_view)
        var duration : TextView = itemView.findViewById(R.id.duration_text_view)
        var icon : ImageView = itemView.findViewById(R.id.icon_image_view)


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            println("YOU CLICKED THIS: \n")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_track_piece, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        // Loading image views
        val imageUrl = imgUrl
        Picasso.get().load(imageUrl).into(holder.image)
        holder.icon.setImageResource(R.drawable.ic_baseline_favorite_border_24)

        holder.track_title.text = data[position].title

        holder.duration.text = data[position].duration?.let { formatTime(it.toInt()) }

        holder.icon.setOnClickListener{
            println("YOU CLICKED HEART OF: ${data[position].title}")
        }
        holder.itemView.setOnClickListener{
            println("YOU CLICKED: ${data[position].title}")
        }


    }

    override fun getItemCount() = data.size


    fun formatTime(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d\"", minutes, seconds)
    }




}