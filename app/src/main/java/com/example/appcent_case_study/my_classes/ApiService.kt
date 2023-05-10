package com.example.appcent_case_study.my_classes

import retrofit2.http.*
import retrofit2.Call

interface ApiService {

    @GET("genre/")
    fun getGenres(): Call<GenreList>?

    @GET("artists/")
    fun getArtists(): Call<ArtistList>?

}