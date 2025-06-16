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
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appcent_case_study.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Toolbar control
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val fav_button = findViewById<ImageView>(R.id.fav_button)
        val ingredients_button = findViewById<ImageView>(R.id.ingredients_button)
        setSupportActionBar(toolbar)



        // Navigation control
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_recipes, R.id.navigation_favorites, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Listener for navigation destination changes so we can hide or unhide the bottom nav bar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_recipes, R.id.navigation_favorites, R.id.navigation_settings -> {
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
                }
                else -> {
                    fav_button.visibility = ImageView.GONE
                }
            }

            // For tracking ingredient dialog button
            when(destination.id) {
                R.id.navigation_stepsFragment -> {
                    // Show
                    ingredients_button.visibility = ImageView.VISIBLE
                }
                else -> {
                    ingredients_button.visibility = ImageView.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Static method accepts (NavController, AppBarConfiguration)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}