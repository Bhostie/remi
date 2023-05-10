package com.example.appcent_case_study.ui.artists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.databinding.ActivityArtistListBinding

class ArtistList : AppCompatActivity() {

    private var _binding: ActivityArtistListBinding? = null
    private val binding get() = _binding!!
    lateinit var recyclerView: RecyclerView

    private val artistsViewModel by lazy {
        ViewModelProvider(this).get(ArtistsViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityArtistListBinding.inflate(layoutInflater)

        val genreId = intent.getStringExtra("genreId").toString()
        val genreName = intent.getStringExtra("genreName").toString()
        title = genreName

        artistsViewModel.getIntentData(genreId)
        artistsViewModel.getMyData()

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Create adapter instance with empty data
        val adapter = ArtistRecyclerViewAdapter(emptyList())
        recyclerView.adapter = adapter

        // Observe the data in the ViewModel and set it to the adapter when it changes
        artistsViewModel.data.observe(this) { artistList ->
            artistList?.let {
                adapter.updateData(it)
                adapter.notifyDataSetChanged()
            }
        }

        setContentView(binding.root)
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}