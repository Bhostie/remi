package com.example.appcent_case_study.ui.artists

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appcent_case_study.my_classes.ApiService
import com.example.appcent_case_study.my_classes.Artist
import com.example.appcent_case_study.my_classes.ArtistList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArtistsViewModel : ViewModel() {
    private val _data = MutableLiveData<List<Artist>?>().apply {
        value = null
    }
    var data: LiveData<List<Artist>?> = _data
    private var genreId: String? = null


    fun getIntentData(data: String){
        genreId = data
    }


    fun getMyData(){

        //Building Retrofit
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.deezer.com/genre/${genreId}/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Set ApiService Interface
        val apiService: ApiService = retrofit.create(ApiService::class.java)

        // Set retrofitData with interface
        val retrofitData = apiService.getArtists()

        // Fetching data from API
        retrofitData?.enqueue(object : Callback<ArtistList> {

            override fun onResponse(call: Call<ArtistList>, response: Response<ArtistList>) {
                val responseBody = response.body()
                if(responseBody!=null){
                    Log.d(ContentValues.TAG, responseBody.toString())

                    _data.value = responseBody.data as List<Artist>?
                    Log.d(ContentValues.TAG, "Size is: ${_data.value?.size}")
                }
                else{
                    Log.d(ContentValues.TAG, "Response body is null")
                    _data.value = null
                }



            }

            override fun onFailure(call: Call<ArtistList>, t: Throwable) {
                Log.d("FAILED", t.message.toString())
                _data.value = null
            }



        })





    }
}