package com.example.appcent_case_study.my_classes

import com.google.gson.annotations.SerializedName

data class SearchList(

	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: List<SearchItem?>? = null
)

data class SearchedArtist(

	@field:SerializedName("picture_xl")
	val pictureXl: String? = null,

	@field:SerializedName("tracklist")
	val tracklist: String? = null,

	@field:SerializedName("picture_big")
	val pictureBig: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("picture_small")
	val pictureSmall: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("picture")
	val picture: String? = null,

	@field:SerializedName("picture_medium")
	val pictureMedium: String? = null
)

data class SearchItem(

	@field:SerializedName("readable")
	val readable: Boolean? = null,

	@field:SerializedName("preview")
	val preview: String? = null,

	@field:SerializedName("md5_image")
	val md5Image: String? = null,

	@field:SerializedName("artist")
	val artist: SearchedArtist? = null,

	@field:SerializedName("album")
	val album: SearchedAlbum? = null,

	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("explicit_content_cover")
	val explicitContentCover: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("title_version")
	val titleVersion: String? = null,

	@field:SerializedName("explicit_lyrics")
	val explicitLyrics: Boolean? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("title_short")
	val titleShort: String? = null,

	@field:SerializedName("duration")
	val duration: String? = null,

	@field:SerializedName("rank")
	val rank: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("explicit_content_lyrics")
	val explicitContentLyrics: Int? = null
)

data class SearchedAlbum(

	@field:SerializedName("cover")
	val cover: String? = null,

	@field:SerializedName("md5_image")
	val md5Image: String? = null,

	@field:SerializedName("tracklist")
	val tracklist: String? = null,

	@field:SerializedName("cover_xl")
	val coverXl: String? = null,

	@field:SerializedName("cover_medium")
	val coverMedium: String? = null,

	@field:SerializedName("cover_small")
	val coverSmall: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("cover_big")
	val coverBig: String? = null
)
