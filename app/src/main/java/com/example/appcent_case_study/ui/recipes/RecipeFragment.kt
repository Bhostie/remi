package com.example.appcent_case_study.ui.recipes

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appcent_case_study.R
import com.example.appcent_case_study.data.AppDatabase
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.databinding.FragmentRecipesBinding
import com.example.appcent_case_study.ui.genres.RecipeRecyclerViewAdapter
import com.example.appcent_case_study.ui.genres.RecipeViewModel

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecipeRecyclerViewAdapter

    private val prefs by lazy {
        requireContext().getSharedPreferences("SettingsData", Context.MODE_PRIVATE)
    }



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
            adapter = this@RecipeFragment.adapter
            // optional: add padding for nav bar
            setPadding(0, 0, 0, getNavigationBarHeight(requireContext()))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shown = prefs.getBoolean("tips_shown", false)
        if (!shown) showHowToDialog()

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
            adapter.setData(recipes)
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

    private fun showHowToDialog() {
        // Inflate your custom layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_how_to_use, null, false)

        // Find the CheckBox in the custom layout
        val neverShowAgain = dialogView.findViewById<CheckBox>(R.id.cbDontShowAgain)

        // Create a Dialog and set the custom view
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogView)

        // Optional: Remove default dialog background to avoid cornered shape
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Handle the "Got it" button click
        dialogView.findViewById<CheckBox>(R.id.cbDontShowAgain).setOnClickListener {
            if (neverShowAgain.isChecked) {
                Log.d("RecipeFragment", "User opted to never show this dialog again")
                prefs.edit()
                    .putBoolean("tips_shown", true)
                    .apply()
            }
            dialog.dismiss()
        }

        // Show the dialog
        dialog.setCancelable(true)
        dialog.show()
    }
}