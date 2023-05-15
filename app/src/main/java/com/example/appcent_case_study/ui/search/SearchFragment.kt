package com.example.appcent_case_study.ui.search

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.R
import com.example.appcent_case_study.databinding.FragmentHomeBinding
import com.example.appcent_case_study.databinding.FragmentSearchBinding
import com.example.appcent_case_study.my_classes.SavedTrack
import com.example.appcent_case_study.ui.home.GenreRecyclerViewAdapter
import com.google.gson.Gson

class SearchFragment : Fragment() {



    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    lateinit var recyclerView: RecyclerView


    private val searchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    fun isLiked(id: String): Boolean{
        // Retrieve JSON string from shared preferences
        val prefs = requireContext().getSharedPreferences("LocalData", AppCompatActivity.MODE_PRIVATE)
        val songJson = prefs.getString("song_${id}", null)
        return songJson!=null

    }

    fun deleteLikedSong(id: String){
        val sharedPreferences = requireContext().getSharedPreferences("LocalData", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("song_${id}")
        editor.apply()
        Toast.makeText(requireContext(), "You Unliked the song", Toast.LENGTH_SHORT).show()
    }

    fun saveLikedSong(likedSong: SavedTrack) {
        val sharedPreferences = requireContext().getSharedPreferences("LocalData", AppCompatActivity.MODE_PRIVATE)
        val songJson = Gson().toJson(likedSong)
        val editor = sharedPreferences.edit()
        editor.putString("song_${likedSong.id}", songJson)
        editor.apply()
        Toast.makeText(requireContext(), "You Liked the song", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root


        var tempKeyword = "eminem"

        binding.searchButton.setOnClickListener{

            tempKeyword = binding.keywordInput.text.toString()
            searchViewModel.getSearchData(tempKeyword)
        }

        recyclerView = binding.recyclerView
        recyclerView.setPadding(0, 0, 0, getNavigationBarHeight(requireContext()))
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = searchViewModel.data.value?.let { SearchRecyclerViewAdapter(it, this) }







        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewModel.data.observe(viewLifecycleOwner) { data ->
            recyclerView.adapter = data?.let { SearchRecyclerViewAdapter(it, this) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else 0
    }

}