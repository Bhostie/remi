package com.example.appcent_case_study.ui.album_details

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.my_classes.Track

class AlbumDetailsRecyclerViewAdapter(private var data: List<Track>) : RecyclerView.Adapter<AlbumDetailsRecyclerViewAdapter.TrackViewHolder>() {


    class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{



        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            println("YOU CLICKED THIS: \n")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }




}