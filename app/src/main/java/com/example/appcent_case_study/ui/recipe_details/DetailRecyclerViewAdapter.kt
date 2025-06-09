package com.example.appcent_case_study.ui.recipe_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.R
import com.example.appcent_case_study.data.Ingredient

class IngredientAdapter(
    private val items: List<Ingredient>
) : RecyclerView.Adapter<IngredientAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = itemView.findViewById(R.id.name)
        val amountTv: TextView = itemView.findViewById(R.id.amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_ingredient_piece, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ing = items[position]
        holder.nameTv.text   = ing.name
        holder.amountTv.text = ing.amount
    }
}