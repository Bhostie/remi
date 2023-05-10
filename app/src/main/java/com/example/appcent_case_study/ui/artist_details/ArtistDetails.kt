package com.example.appcent_case_study.ui.artist_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.databinding.ActivityArtistDetailsBinding
import com.example.appcent_case_study.my_classes.Album
import com.example.appcent_case_study.my_classes.ArtistItem
import com.squareup.picasso.Picasso

class ArtistDetails : AppCompatActivity() {

    private var _binding: ActivityArtistDetailsBinding? = null
    private val binding get() = _binding!!
    lateinit var  recyclerView: RecyclerView

    private val artistDetailsViewModel by lazy {
        ViewModelProvider(this).get(ArtistDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityArtistDetailsBinding.inflate(layoutInflater)

        val artistId = intent.getStringExtra("artistId").toString()
        val artistName = intent.getStringExtra("artistName").toString()
        title = artistName

        artistDetailsViewModel.getIntentData(artistId)
        artistDetailsViewModel.getArtistDetails()
        artistDetailsViewModel.getAlbumList()

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe the data in the ViewModel and set it to the adapter when it changes
        artistDetailsViewModel.data.observe(this) { ArtistItem ->
            ArtistItem?.let {
                val imgUrl = ArtistItem.pictureMedium
                Picasso.get().load(imgUrl).into(binding.imageView)
            }
        }
        artistDetailsViewModel.albumdata.observe(this){ AlbumList ->
            AlbumList?.let{
                val adapter = ArtistDetailsRecyclerViewAdapter(it)
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