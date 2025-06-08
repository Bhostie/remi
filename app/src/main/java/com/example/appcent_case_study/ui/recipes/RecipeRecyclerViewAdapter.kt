package com.example.appcent_case_study.ui.genres

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.appcent_case_study.R
import com.example.appcent_case_study.data.Recipe

class RecipeRecyclerViewAdapter(
    private var items: List<Recipe> = emptyList()
) : RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder>() {

    /** Fragment or Activity can set this to handle clicks */
    var onItemClick: ((Recipe) -> Unit)? = null

    fun setData(newItems: List<Recipe>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_genre_piece, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = items[position]

        // 1) bind title
        holder.title.text = recipe.name


        // build the asset URI
        val assetUri = recipe.imageUri
            ?.takeIf { it.isNotBlank() }
            ?.let { "file:///android_asset/images/$it" }
        // fallback if your imageUri was already a full URI:
            ?: R.drawable.ic_dashboard_black_24dp

        // 2) bind imageUri (if null or empty, you might load a placeholder)
        Glide.with(holder.image.context)
            .load(assetUri)
            .centerCrop()
            .placeholder(R.drawable.ic_dashboard_black_24dp)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.image)

        // 3) click callback
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(recipe)
        }
    }

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image_view)
        val title: TextView = itemView.findViewById(R.id.title_text_view)
    }
}