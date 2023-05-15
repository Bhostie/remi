package com.example.appcent_case_study.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appcent_case_study.my_classes.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchViewModel : ViewModel() {


    private val _data = MutableLiveData<List<SearchItem>?>().apply {
        value = null
    }

    var data: LiveData<List<SearchItem>?> = _data


    fun getSearchData(keyword: String){

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)

        val retrofitData = apiService.getSearchResults(keyword)

        retrofitData?.enqueue(object : Callback<SearchList>{

            override fun onResponse(call: Call<SearchList>, response: Response<SearchList>) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Log.d("SUCCESS", responseBody.toString())

                    _data.value = responseBody.data as List<SearchItem>?
                    Log.d("DEBUG:", "Size is: ${_data.value?.size}")
                }
                else {
                    Log.d("FAILED", "Response body is null")
                    _data.value = null
                }
            }

            override fun onFailure(call: Call<SearchList>, t: Throwable) {
                Log.d("FAILED", t.message.toString())
                _data.value = null
            }


        })



    }






}