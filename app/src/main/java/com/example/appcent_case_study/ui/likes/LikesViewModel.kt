package com.example.appcent_case_study.ui.likes

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appcent_case_study.my_classes.SavedTrack
import com.google.gson.Gson

class LikesViewModel : ViewModel() {


    private val _data = MutableLiveData<MutableList<SavedTrack>>().apply {
        value = null
    }
    var data : LiveData<MutableList<SavedTrack>> = _data

    lateinit var sharedPrefs: SharedPreferences

    fun getSharedPrefs(sharedPreferences: SharedPreferences){
        sharedPrefs = sharedPreferences
    }

    fun getSavedData(){

        val trackList = mutableListOf<SavedTrack>()
        val gson = Gson()
        for ((key, value) in sharedPrefs.all) {
            if (key.startsWith("song_")) {
                val trackJson = value as String
                val track = gson.fromJson(trackJson, SavedTrack::class.java)
                trackList.add(track)
                // do something with the track object
            }
        }
        _data.value = trackList
    }

















}