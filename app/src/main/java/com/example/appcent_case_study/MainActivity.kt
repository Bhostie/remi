package com.example.appcent_case_study

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appcent_case_study.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Toolbar control
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val fav_button = findViewById<ImageView>(R.id.fav_button)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_likes, R.id.navigation_recipes
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        // Listener for navigation destination changes so we can hide or unhide the bottom nav bar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_likes, R.id.navigation_recipes, R.id.navigation_search -> {
                    binding.navView.visibility = BottomNavigationView.VISIBLE

                }
                else -> {
                    binding.navView.visibility = BottomNavigationView.GONE
                }
            }

            // For tracking Fav Recipe button
            when(destination.id) {
                R.id.navigation_recipe_detail -> {
                    // Show
                    fav_button.visibility = ImageView.VISIBLE

                    //fav_button.setOnClickListener {
                        // Get the current recipe ID from the arguments
                        //Log.d("MainActivity", "Fav button clicked")
                    //}

                }
                else -> {
                    fav_button.visibility = ImageView.INVISIBLE
                }
            }

        }

    }
}