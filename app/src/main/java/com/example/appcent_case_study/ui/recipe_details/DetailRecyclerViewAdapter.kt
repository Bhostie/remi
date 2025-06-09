package com.example.appcent_case_study.ui.recipe_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.R
import com.example.appcent_case_study.data.Ingredient

class IngredientAdapter(
    private var items: List<Ingredient> = emptyList()
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {



    fun setData(newItems: List<Ingredient>) {
        items = newItems
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_ingredient_piece, parent, false)
        return IngredientViewHolder(v)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ing = items[position]
        holder.nameTv.text   = ing.name
        holder.amountTv.text = ing.amount
    }

    override fun getItemCount(): Int = items.size


    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = itemView.findViewById(R.id.name)
        val amountTv: TextView = itemView.findViewById(R.id.amount)
    }
}