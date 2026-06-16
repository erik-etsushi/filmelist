package com.filmelist.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val genreIds: List<Int>,
    val mediaType: MediaType,
    val runtime: Int? = null,
    val numberOfSeasons: Int? = null,
)

enum class MediaType {
    MOVIE, TV
}
