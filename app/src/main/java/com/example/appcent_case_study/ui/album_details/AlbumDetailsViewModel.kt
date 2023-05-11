package com.example.appcent_case_study.ui.album_details

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appcent_case_study.my_classes.Album
import com.example.appcent_case_study.my_classes.ApiService
import com.example.appcent_case_study.my_classes.Track
import com.example.appcent_case_study.my_classes.TrackList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlbumDetailsViewModel : ViewModel() {


    private val _data = MutableLiveData<List<Track>?>().apply {
        value = null
    }
    var data: LiveData<List<Track>?> = _data

    lateinit var albumId: String
    lateinit var albumCover: String

    fun getIntentData(id: String, cover: String){
        albumId = id
        albumCover = cover
    }


    fun getTrackList(){

        // Building Retrofit
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(" https://api.deezer.com/album/${albumId}/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Set ApiService Interface
        val apiService: ApiService = retrofit.create(ApiService::class.java)
        // Set retrofitData with interface
        val retrofitData = apiService.getTracks()
        retrofitData.enqueue(object : Callback<TrackList>{

            override fun onResponse(call: Call<TrackList>, response: Response<TrackList>) {
                val responseBody = response.body()
                if(responseBody!=null){
                    Log.d(ContentValues.TAG, responseBody.toString())
                    _data.value = responseBody.data as List<Track>?
                }
                else{
                    Log.d(ContentValues.TAG, "Response body is null")
                    _data.value = null
                }
            }

            override fun onFailure(call: Call<TrackList>, t: Throwable) {
                Log.d("FAILED: ", t.message.toString())
                _data.value = null
            }



        })



    }














}