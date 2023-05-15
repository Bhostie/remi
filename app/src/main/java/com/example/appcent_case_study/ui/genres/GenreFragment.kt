package com.example.appcent_case_study.ui.genres

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcent_case_study.databinding.FragmentHomeBinding

class GenreFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var recyclerView: RecyclerView

    private val genreViewModel by lazy {
        ViewModelProvider(this).get(GenreViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        genreViewModel.getMyData()


        val navBarHeight = getNavigationBarHeight(requireContext())
        recyclerView = binding.recyclerView
        recyclerView.setPadding(0, 0, 0, navBarHeight)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        recyclerView.adapter = genreViewModel.data.value?.let { GenreRecyclerViewAdapter(it) }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        genreViewModel.data.observe(viewLifecycleOwner) { data ->
            recyclerView.adapter = data?.let { GenreRecyclerViewAdapter(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getNavigationBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else 0
    }
}
