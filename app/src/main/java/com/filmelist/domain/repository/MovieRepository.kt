package com.filmelist.domain.repository

import com.filmelist.domain.model.Movie
import com.filmelist.domain.model.MovieList
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun searchMovies(query: String): Result<List<Movie>>
    suspend fun searchTv(query: String): Result<List<Movie>>
    suspend fun getMovieDetails(id: Int): Result<Movie>
    suspend fun getTvDetails(id: Int): Result<Movie>
}

interface ListRepository {
    fun getAllLists(): Flow<List<MovieList>>
    fun getListWithMovies(listId: Long): Flow<MovieList?>
    suspend fun createList(name: String): Long
    suspend fun deleteList(listId: Long)
    suspend fun renameList(listId: Long, newName: String)
    suspend fun addMovieToList(listId: Long, movie: Movie)
    suspend fun removeMovieFromList(listId: Long, movieId: Int)
    suspend fun isMovieInList(listId: Long, movieId: Int): Boolean
}
