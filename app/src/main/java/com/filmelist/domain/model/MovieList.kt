package com.filmelist.domain.model

data class MovieList(
    val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false,
    val movies: List<Movie> = emptyList(),
)
