package com.example.appcent_case_study.ui.recipe_details

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcent_case_study.data.AppDatabase
import com.example.appcent_case_study.data.Ingredient
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.data.Recipe
import com.example.appcent_case_study.databinding.FragmentRecipeDetailsBinding
import com.example.appcent_case_study.ui.genres.RecipeViewModel
import com.example.appcent_case_study.ui.recipes.RecipeViewModelFactory

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: IngredientAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentRecipeDetailsBinding.bind(view)

        // Read the ID
        val recipeId = requireArguments().getLong("recipeId")



        // Suppose you passed in the Recipe via arguments:
        val recipe: Recipe = requireArguments().getParcelable("recipe")!!

        // 1) Show your recipe image & other fields...
        //    (Glide or asset-loading code as before)
        binding.recipeName.text = recipe.name
        binding.imageView.setImageResource(
            resources.getIdentifier(recipe.imageUri, "drawable", requireContext().packageName)
        )

        // 2) Parse the ingredients string:
        val ingredientsList = recipe.ingredients
            .split("\n")
            .mapNotNull { line ->
                val parts = line.split(";")
                if (parts.size >= 2) {
                    val name   = parts[0].trim()
                    val amount = parts[1].trim()
                    Ingredient(name, amount)
                } else null
            }

        // 3) Set up RecyclerView
        adapter = IngredientAdapter(ingredientsList)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = this@RecipeDetailFragment.adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    private fun getNavigationBarHeight(context: Context): Int {
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
    }


}