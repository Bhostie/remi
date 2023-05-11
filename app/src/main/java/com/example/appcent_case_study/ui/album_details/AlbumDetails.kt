package com.example.appcent_case_study.ui.album_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.databinding.ActivityAlbumDetailsBinding

class AlbumDetails : AppCompatActivity() {

    private val _binding: ActivityAlbumDetailsBinding? = null
    private val binding get() = _binding!!
    lateinit var  recyclerView: RecyclerView

    private val albumDetailsViewModel by lazy {
        ViewModelProvider(this).get(AlbumDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}