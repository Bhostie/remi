package com.example.appcent_case_study.ui.recipe_details

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.appcent_case_study.R
import com.example.appcent_case_study.data.AppDatabase
import com.example.appcent_case_study.data.Ingredient
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.data.Recipe
import com.example.appcent_case_study.databinding.FragmentRecipeDetailsBinding
import com.example.appcent_case_study.ui.genres.RecipeViewModel
import com.example.appcent_case_study.ui.recipes.RecipeViewModelFactory

class RecipeDetailFragment : Fragment(R.layout.fragment_recipe_details) {

    private var _binding: FragmentRecipeDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: IngredientAdapter

    // 1) We need a factory to pass our LocalRecipeRepository into the VM
    private val recipeDetailViewModel by lazy {
        val db = AppDatabase.getInstance(requireContext())
        val repo = LocalRecipeRepository(db)
        val factory = DetailViewModelFactory(repo, requireArguments().getLong("recipeId"))
        ViewModelProvider(this, factory)[DetailViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentRecipeDetailsBinding.bind(view)

        recipeDetailViewModel.recipeById.observe(viewLifecycleOwner) { recipe ->

            // Bind the name
            binding.recipeName.text = recipe.name

            // Build image uri
            val assetUri = recipe.imageUri
                ?.takeIf { it.isNotBlank() }
                ?.let { "file:///android_asset/images/$it" }
                ?: R.drawable.ic_dashboard_black_24dp // Fallback image

            // Bind the image
            Glide.with(binding.imageView.context)
                .load(assetUri)
                .centerCrop()
                .placeholder(R.drawable.ic_dashboard_black_24dp)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.imageView)


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
            adapter = IngredientAdapter(ingredientsList ?: emptyList())
            binding.recyclerView.apply {
                layoutManager = GridLayoutManager(requireContext(),2)
                adapter = this@RecipeDetailFragment.adapter
            }
        }

        binding.btnStartCooking.setOnClickListener {
            // Handle start cooking button click
            val recipeId = recipeDetailViewModel.recipeById.value?.id ?: return@setOnClickListener
            val bundle = Bundle().apply {
                putLong("recipeId", recipeId)
            }
            findNavController().navigate(R.id.navigation_stepsFragment, bundle)
        }


        // Fav button listener
        val fav_button = requireActivity().findViewById<ImageView>(R.id.fav_button)
        if (recipeFavOrNot(requireArguments().getLong("recipeId"))) {
            fav_button.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            fav_button.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }

        fav_button.setOnClickListener() {
            Log.d("RecipeDetailFragment", "Fav button clicked")
            val recipeId = requireArguments().getLong("recipeId")
            if (recipeFavOrNot(recipeId)) {
                deleteFavRecipe(recipeId)
                fav_button.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            } else {
                val recipeName = recipeDetailViewModel.recipeById.value?.name ?: ""
                val recipeImageUrl = recipeDetailViewModel.recipeById.value?.imageUri ?: ""
                favRecipe(recipeId, recipeName, recipeImageUrl)
                fav_button.setImageResource(R.drawable.ic_baseline_favorite_24)
            }


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

    fun recipeFavOrNot(recipeId: Long): Boolean{
        // Retrieve JSON string from shared preferences
        val prefs = requireContext().getSharedPreferences("FavRecipeData", AppCompatActivity.MODE_PRIVATE)
        val recipeJson = prefs.getString("recipe${recipeId}", null)
        return recipeJson!=null

    }

    fun favRecipe(recipeId: Long, recipeName: String, recipeImageUrl: String) {
        val sharedPreferences = requireContext().getSharedPreferences("FavRecipeData", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("recipe${recipeId}", "$recipeName,$recipeImageUrl")
        editor.apply()
        Toast.makeText(requireContext(), "You Favorited the recipe", Toast.LENGTH_SHORT).show()

    }

    fun deleteFavRecipe(recipeId: Long) {
        val sharedPreferences = requireContext().getSharedPreferences("FavRecipeData", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("recipe${recipeId}")
        editor.apply()
        Toast.makeText(requireContext(), "You Unfavorited the recipe", Toast.LENGTH_SHORT).show()
    }


}