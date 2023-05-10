package com.example.appcent_case_study.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appcent_case_study.my_classes.ApiService
import com.example.appcent_case_study.my_classes.Genre
import com.example.appcent_case_study.my_classes.GenreList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    private val _data = MutableLiveData<List<Genre>?>().apply {
        value = null
    }

    val text: LiveData<String> = _text
    var data: LiveData<List<Genre>?> = _data

    ///////////////////////////////////////////////////////////////////////





    private val _genres = MutableLiveData<GenreList>()
    val genres: LiveData<GenreList> get() = _genres


    fun getMyData(){

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)

        val retrofitData = apiService.getGenres()

        retrofitData?.enqueue(object : Callback<GenreList> {

            override fun onResponse(call: Call<GenreList>, response: Response<GenreList>) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Log.d("SUCCESS", responseBody.toString())
                    val myStrBuilder = StringBuilder()

                    for (dataItem in responseBody.data ?: emptyList()) {
                        myStrBuilder.append("ID: ${dataItem?.id}\n")
                        myStrBuilder.append("Name: ${dataItem?.name}\n")
                        myStrBuilder.append("Picture: ${dataItem?.picture}\n")
                        myStrBuilder.append("Picture Small: ${dataItem?.pictureSmall}\n")
                        myStrBuilder.append("Picture Medium: ${dataItem?.pictureMedium}\n")
                        myStrBuilder.append("Picture Big: ${dataItem?.pictureBig}\n")
                        myStrBuilder.append("Picture XL: ${dataItem?.pictureXl}\n")
                        myStrBuilder.append("Type: ${dataItem?.type}\n")
                        myStrBuilder.append("\n")
                    }

                    _data.value = responseBody.data as List<Genre>?
                    Log.d("DEBUG:", "Size is: ${_data.value?.size}")
                } else {
                    Log.d("FAILED", "Response body is null")
                    _data.value = null
                }
            }

            override fun onFailure(call: Call<GenreList>, t: Throwable) {
                Log.d("FAILED", t.message.toString())

                _data.value = null
            }
        })



    }






}