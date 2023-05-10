package com.example.appcent_case_study.my_classes

import com.google.gson.annotations.SerializedName

data class AlbumList(

	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: List<Album?>? = null
)

data class Album(

	@field:SerializedName("md5_image")
	val md5Image: String? = null,

	@field:SerializedName("tracklist")
	val tracklist: String? = null,

	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("cover_small")
	val coverSmall: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("explicit_lyrics")
	val explicitLyrics: Boolean? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("genre_id")
	val genreId: Int? = null,

	@field:SerializedName("record_type")
	val recordType: String? = null,

	@field:SerializedName("fans")
	val fans: Int? = null,

	@field:SerializedName("cover")
	val cover: String? = null,

	@field:SerializedName("cover_xl")
	val coverXl: String? = null,

	@field:SerializedName("release_date")
	val releaseDate: String? = null,

	@field:SerializedName("cover_medium")
	val coverMedium: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("cover_big")
	val coverBig: String? = null
)
