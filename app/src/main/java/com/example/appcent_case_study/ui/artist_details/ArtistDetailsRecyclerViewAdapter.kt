package com.example.appcent_case_study.ui.artist_details

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.R
import com.example.appcent_case_study.my_classes.Album
import com.example.appcent_case_study.my_classes.ArtistItem
import com.example.appcent_case_study.ui.album_details.AlbumDetails
import com.squareup.picasso.Picasso

class ArtistDetailsRecyclerViewAdapter(private var albumdata: List<Album>) : RecyclerView.Adapter<ArtistDetailsRecyclerViewAdapter.AlbumViewHolder>() {


    fun updateAlbumData(input: List<Album>){
        albumdata = input
    }


    class AlbumViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var image : ImageView = itemView.findViewById(R.id.image_view)
        var album_name : TextView = itemView.findViewById(R.id.album_text_view)
        var album_date : TextView = itemView.findViewById(R.id.date_text_view)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            println("YOU CLICKED THIS: \n")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_artistitem_piece, parent, false)
        return AlbumViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        // Converting our image string into ImageView with Picasso

        val imageUrl = albumdata[position].coverMedium!!
        Picasso.get().load(imageUrl).into(holder.image)

        // View the album name
        holder.album_name.text = albumdata[position].title

        // View album release date
        holder.album_date.text = albumdata[position].releaseDate

        // Navigate to the Artists screen when clicked
        holder.itemView.setOnClickListener{
            println("YOU CLICKED: ${albumdata[position].title}")  //Debug

            val intent = Intent(holder.itemView.context, AlbumDetails::class.java)
            intent.putExtra("albumId", albumdata[position].id)
            intent.putExtra("albumName",albumdata[position].title)
            intent.putExtra("albumCover",albumdata[position].coverMedium)
            holder.itemView.context.startActivity(intent)

        }
    }

    override fun getItemCount() = albumdata.size


}