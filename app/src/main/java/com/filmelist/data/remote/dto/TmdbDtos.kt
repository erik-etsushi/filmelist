package com.filmelist.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TmdbSearchResponse(
    val page: Int,
    val results: List<TmdbMovieDto>,
    @SerializedName("total_results") val totalResults: Int,
)

data class TmdbMovieDto(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    @SerializedName("original_title") val originalTitle: String? = null,
    @SerializedName("original_name") val originalName: String? = null,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("first_air_date") val firstAirDate: String? = null,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("genre_ids") val genreIds: List<Int> = emptyList(),
    val runtime: Int? = null,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int? = null,
)
