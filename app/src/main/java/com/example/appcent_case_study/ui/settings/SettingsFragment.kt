package com.example.appcent_case_study.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appcent_case_study.databinding.FragmentSettingsBinding

class SettingsFragment: Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)


        //  Get pref (MODE_PRIVATE keeps them app-private)
        val sharedPreferences = requireContext()
            .getSharedPreferences("SettingsData", Context.MODE_PRIVATE)

        // Load all saved settings
        for ((key, value) in sharedPreferences.all) {
            when (key) {
                "gesture"       -> binding.sGesture.isChecked   = value as Boolean
                "voice"         -> binding.sVoice.isChecked     = value as Boolean
                "tips_shown"    -> binding.sHint.isChecked      = value as Boolean
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize settings UI components here

        val sharedPreferences = requireContext()
            .getSharedPreferences("SettingsData", Context.MODE_PRIVATE)

        // Save toggles on change
        binding.sGesture.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit()
                .putBoolean("gesture", isChecked)
                .apply()
        }
        binding.sVoice.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit()
                .putBoolean("voice", isChecked)
                .apply()
        }
        binding.sHint.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit()
                .putBoolean("tips_shown", isChecked)
                .apply()
        }





    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}