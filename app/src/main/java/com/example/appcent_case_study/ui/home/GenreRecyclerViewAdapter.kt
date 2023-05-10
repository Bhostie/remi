package com.example.appcent_case_study.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.R
import com.example.appcent_case_study.my_classes.DataItem
import com.squareup.picasso.Picasso

class GenreRecyclerViewAdapter(private val data: List<DataItem>) : RecyclerView.Adapter<GenreRecyclerViewAdapter.GenreViewHolder>() {


    class GenreViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var image: ImageView = itemView.findViewById(R.id.image_view)
        var title: TextView = itemView.findViewById(R.id.title_text_view)

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            println("YOU CLICKED THIS: \n")
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_genre_item, parent, false)
        return GenreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {

        // Converting our image string into ImageView with Picasso
        val imageUrl = data[position].picture!!
        Picasso.get().load(imageUrl).into(holder.image)

        // View the Genre name
        holder.title.text = data[position].name

        // Navigate to the Artists screen when clicked
        holder.itemView.setOnClickListener{
            println("YOU CLICKED: ${data[position].name}")
            //TODO: Navigate Artists screen
        }
    }

    override fun getItemCount() = data.size

}
