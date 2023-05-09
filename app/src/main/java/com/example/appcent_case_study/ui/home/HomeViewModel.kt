package com.example.appcent_case_study.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcent_case_study.my_classes.ApiService
import com.example.appcent_case_study.my_classes.Genre
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    private val _data = MutableLiveData<String>().apply {
        value = "TEST DATA"
    }

    val text: LiveData<String> = _text
    var data: LiveData<String> = _data

    ///////////////////////////////////////////////////////////////////////




    private val _genres = MutableLiveData<Genre>()
    val genres: LiveData<Genre> get() = _genres


    fun getMyData(){

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.deezer.com/genre/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)

        val retrofitData = apiService.getGenres()

        retrofitData?.enqueue(object : Callback<Genre> {
            override fun onResponse(call: Call<Genre>?, response: Response<Genre>?) {

                val json = Gson().toJson(response?.body())
                Log.d("RESPONSE_JSON", json + "\"Response code: ${response?.body()}\"")

                val responseBody = response?.body()
                if (responseBody != null) {
                    Log.d("SUCCESS", responseBody.toString())
                    val myStrBuilder = StringBuilder()

                    responseBody.data?.forEach {
                        it?.name?.let { name ->
                            myStrBuilder.append("$name\n")
                        }
                    }
                    _data.value = myStrBuilder.toString()
                } else {
                    Log.d("FAILED", "Response body is null")
                    _data.value = "FAILED"
                }
            }

            override fun onFailure(call: Call<Genre>?, t: Throwable) {
                Log.d("FAILED", t.message.toString())

                _data.value = "FAILED"
            }
        })


    }






}