package com.example.appcent_case_study.ui.album_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.databinding.ActivityAlbumDetailsBinding
import com.example.appcent_case_study.my_classes.SavedTrack
import com.google.gson.Gson


class AlbumDetails : AppCompatActivity() {

    private var _binding: ActivityAlbumDetailsBinding? = null
    private val binding get() = _binding!!
    lateinit var  recyclerView: RecyclerView

    private val albumDetailsViewModel by lazy {
        ViewModelProvider(this).get(AlbumDetailsViewModel::class.java)
    }

    // Initialize Shared Preference for local storage
     fun saveLikedSong(likedSong: SavedTrack) {
        val sharedPreferences = getSharedPreferences("LocalData", MODE_PRIVATE)
        val songJson = Gson().toJson(likedSong)
        val editor = sharedPreferences.edit()
        editor.putString("song_${likedSong.id}", songJson)
        editor.apply()
        Toast.makeText(this, "You Liked the song", Toast.LENGTH_SHORT).show()
    }

    fun deleteLikedSong(id: String){
        val sharedPreferences = getSharedPreferences("LocalData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("song_${id}")
        editor.apply()
        Toast.makeText(this, "You Unliked the song", Toast.LENGTH_SHORT).show()
    }
    fun getLikedSong(id: String): SavedTrack {

        // Retrieve JSON string from shared preferences
        val prefs = getSharedPreferences("LocalData", MODE_PRIVATE)
        val songJson = prefs.getString("song_${id}", null)

        // Convert JSON string to data class
        val gson = Gson()
        return gson.fromJson(songJson, SavedTrack::class.java)
    }

    fun isLiked(id: String): Boolean{
        // Retrieve JSON string from shared preferences
        val prefs = getSharedPreferences("LocalData", MODE_PRIVATE)
        val songJson = prefs.getString("song_${id}", null)

        return songJson!=null

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
                val adapter = AlbumDetailsRecyclerViewAdapter(it, albumCover, this)
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


