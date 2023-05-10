package com.example.appcent_case_study.ui.artists

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appcent_case_study.R

class ArtistsFragment : Fragment() {

    companion object {
        fun newInstance() = ArtistsFragment()
    }

    private lateinit var viewModel: ArtistsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_artists, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ArtistsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}