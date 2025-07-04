package com.example.appcent_case_study.ui.favorites

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcent_case_study.R
import com.example.appcent_case_study.data.AppDatabase
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.data.Recipe
import com.example.appcent_case_study.databinding.FragmentRecipesBinding
import com.example.appcent_case_study.ui.genres.RecipeRecyclerViewAdapter
import com.example.appcent_case_study.ui.genres.RecipeViewModel
import com.example.appcent_case_study.ui.recipes.RecipeViewModelFactory

class FavoritesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecipeRecyclerViewAdapter

    // 1) We need a factory to pass our LocalRecipeRepository into the VM
    private val recipeViewModel by lazy {
        val db = AppDatabase.getInstance(requireContext())
        val repo = LocalRecipeRepository(db)
        val factory = RecipeViewModelFactory(repo)
        ViewModelProvider(this, factory)[RecipeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)

        // 2) Instantiate your adapter exactly once!
        adapter = RecipeRecyclerViewAdapter()

        // 3) Wire up the RecyclerView
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = this@FavoritesFragment.adapter
            // optional: add padding for nav bar
            setPadding(0, 0, 0, getNavigationBarHeight(requireContext()))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // **NAVIGATION**: when an item is clicked, navigate with the recipeId
        adapter.onItemClick = { recipe ->
            Log.d("RecipeFragment", "Clicked on recipe: ${recipe.name}")
            val bundle = Bundle().apply {
                putLong("recipeId", recipe.id)
            }
            findNavController().navigate(R.id.navigation_recipe_detail, bundle)
        }


        // 4) Observe the LiveData from Room and submit into adapter
        recipeViewModel.allRecipes.observe(viewLifecycleOwner) { recipes ->

            val SharedPreferences = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE)
            val favoriteIds = SharedPreferences.getStringSet("favorite_ids", emptySet()) ?: emptySet()

            var favlist : List<Recipe> = emptyList()

            for (recipe in recipes) {
                if(recipeFavOrNot(recipe.id)){
                    favlist = favlist + recipe
                }
            }


            adapter.setData(favlist)
        }
    }

    // 5) Clean up binding here, not inside onViewCreated
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
}