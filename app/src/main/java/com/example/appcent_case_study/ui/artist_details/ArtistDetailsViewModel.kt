package com.example.appcent_case_study.ui.artist_details

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appcent_case_study.my_classes.Album
import com.example.appcent_case_study.my_classes.AlbumList
import com.example.appcent_case_study.my_classes.ApiService
import com.example.appcent_case_study.my_classes.ArtistItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArtistDetailsViewModel : ViewModel() {


    private val _data = MutableLiveData<ArtistItem?>().apply {
        value = null
    }
    var data: LiveData<ArtistItem?> = _data

    private val _albumdata = MutableLiveData<List<Album>?>().apply {
        value = null
    }
    var albumdata : LiveData<List<Album>?> = _albumdata

    lateinit var artistId: String

    fun getIntentData(data: String){
        artistId = data
    }

    fun getArtistDetails(){

        // Building Retrofit
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(" https://api.deezer.com/artist/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Set ApiService Interface
        val apiService: ApiService = retrofit.create(ApiService::class.java)
        // Set retrofitData with interface
        val retrofitData = apiService.getArtistDetails(artistId)
        // Fetching data from API
        retrofitData?.enqueue(object : Callback<ArtistItem> {

            override fun onResponse(call: Call<ArtistItem>, response: Response<ArtistItem>) {
                val responseBody = response.body()
                if(responseBody!=null){
                    Log.d(ContentValues.TAG, responseBody.toString())
                    _data.value = responseBody
                }
                else{
                    Log.d(ContentValues.TAG, "Response body is null")
                    _data.value = null
                }
            }
            override fun onFailure(call: Call<ArtistItem>, t: Throwable) {
                Log.d("FAILED", t.message.toString())
                _data.value = null
            }
        })
    }

    fun getAlbumList(){

        // Building Retrofit
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(" https://api.deezer.com/artist/${artistId}/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Set ApiService Interface
        val apiService: ApiService = retrofit.create(ApiService::class.java)
        // Set retrofitData with interface
        val retrofitData = apiService.getArtistAlbums()
        // Fetching data from API
        retrofitData?.enqueue(object : Callback<AlbumList> {

            override fun onResponse(call: Call<AlbumList>, response: Response<AlbumList>) {
                val responseBody = response.body()
                if(responseBody!=null){
                    Log.d(ContentValues.TAG, responseBody.toString())
                    _albumdata.value = responseBody.data as List<Album>?
                }
                else{
                    Log.d(ContentValues.TAG, "Response body is null")
                    _albumdata.value = null
                }
            }
            override fun onFailure(call: Call<AlbumList>, t: Throwable) {
                Log.d("FAILED", t.message.toString())
                _albumdata.value = null
            }
        })


    }




























}