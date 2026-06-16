package com.filmelist.data.repository

import com.filmelist.data.local.dao.ListDao
import com.filmelist.data.local.dao.MovieDao
import com.filmelist.data.local.entity.ListEntity
import com.filmelist.data.local.entity.ListMovieCrossRef
import com.filmelist.data.local.entity.ListWithMovies
import com.filmelist.data.local.entity.MovieEntity
import com.filmelist.domain.model.MediaType
import com.filmelist.domain.model.Movie
import com.filmelist.domain.model.MovieList
import com.filmelist.domain.repository.ListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ListRepositoryImpl @Inject constructor(
    private val listDao: ListDao,
    private val movieDao: MovieDao,
) : ListRepository {

    override fun getAllLists(): Flow<List<MovieList>> =
        listDao.getAllListsWithMovies().map { list -> list.map { it.toDomain() } }

    override fun getListWithMovies(listId: Long): Flow<MovieList?> =
        listDao.getListWithMovies(listId).map { it?.toDomain() }

    override suspend fun createList(name: String): Long =
        listDao.insertList(ListEntity(name = name))

    override suspend fun deleteList(listId: Long) = listDao.deleteList(listId)

    override suspend fun renameList(listId: Long, newName: String) =
        listDao.renameList(listId, newName)

    override suspend fun addMovieToList(listId: Long, movie: Movie) {
        movieDao.upsertMovie(movie.toEntity())
        listDao.addMovieToList(ListMovieCrossRef(listId = listId, movieId = movie.id))
    }

    override suspend fun removeMovieFromList(listId: Long, movieId: Int) =
        listDao.removeMovieFromList(listId, movieId)

    override suspend fun isMovieInList(listId: Long, movieId: Int): Boolean =
        listDao.isMovieInList(listId, movieId) > 0
}

fun ListWithMovies.toDomain() = MovieList(
    id = list.id,
    name = list.name,
    isDefault = list.isDefault,
    movies = movies.map { it.toDomain() },
)

fun MovieEntity.toDomain() = Movie(
    id = id,
    title = title,
    originalTitle = originalTitle,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    genreIds = genreIds.split(",").mapNotNull { it.trim().toIntOrNull() },
    mediaType = MediaType.valueOf(mediaType),
    runtime = runtime,
    numberOfSeasons = numberOfSeasons,
)

fun Movie.toEntity() = MovieEntity(
    id = id,
    title = title,
    originalTitle = originalTitle,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    genreIds = genreIds.joinToString(","),
    mediaType = mediaType.name,
    runtime = runtime,
    numberOfSeasons = numberOfSeasons,
)
