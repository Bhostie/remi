package com.example.appcent_case_study.ui.likes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LikesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Likes Fragment"
    }
    val text: LiveData<String> = _text
}