package com.example.appcent_case_study.ui.likes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.databinding.FragmentLikesBinding

class LikesFragment : Fragment() {

    private var _binding: FragmentLikesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView

    private val likesViewModel by lazy{
        ViewModelProvider(this).get(LikesViewModel::class.java)
    }

    fun deleteLikedSong(id: String){
        val sharedPreferences = requireContext().getSharedPreferences("LocalData", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("song_${id}")
        editor.apply()
        likesViewModel.getSavedData() // Fetch the new localData due to deletion
        Toast.makeText(requireContext(), "You Unliked the song", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLikesBinding.inflate(inflater,container,false)
        val root: View = binding.root

        val sharedPreferences = requireContext().getSharedPreferences("LocalData", Context.MODE_PRIVATE)
        likesViewModel.getSharedPrefs(sharedPreferences)
        likesViewModel.getSavedData()



        recyclerView = binding.recyclerView
        recyclerView.setPadding(0, 0, 0, getNavigationBarHeight(requireContext()))
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = likesViewModel.data.value?.let { LikesRecyclerViewAdapter(it, this) }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        likesViewModel.data.observe(viewLifecycleOwner) { data ->
            recyclerView.adapter = data?.let { LikesRecyclerViewAdapter(it,this) }
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

