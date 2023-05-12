package com.example.appcent_case_study.ui.album_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.databinding.ActivityAlbumDetailsBinding

class AlbumDetails : AppCompatActivity() {

    private var _binding: ActivityAlbumDetailsBinding? = null
    private val binding get() = _binding!!
    lateinit var  recyclerView: RecyclerView

    private val albumDetailsViewModel by lazy {
        ViewModelProvider(this).get(AlbumDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityAlbumDetailsBinding.inflate(layoutInflater)

        val albumId = intent.getStringExtra("albumId").toString()
        val albumName = intent.getStringExtra("albumName").toString()
        var albumCover = intent.getStringExtra("albumCover").toString()
        title = albumName


        albumDetailsViewModel.getIntentData(albumId, albumCover)
        albumDetailsViewModel.getTrackList()

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe the data in the ViewModel and set it to the adapter when it changes
        albumDetailsViewModel.data.observe(this){ TrackList ->
            TrackList?.let{
                val adapter = AlbumDetailsRecyclerViewAdapter(it, albumCover)
                recyclerView.adapter = adapter
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


