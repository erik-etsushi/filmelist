package com.filmelist.data.repository

import com.filmelist.data.remote.api.TmdbApi
import com.filmelist.data.remote.dto.TmdbMovieDto
import com.filmelist.domain.model.MediaType
import com.filmelist.domain.model.Movie
import com.filmelist.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
) : MovieRepository {

    override suspend fun searchMovies(query: String): Result<List<Movie>> = runCatching {
        api.searchMovies(query).results.map { it.toDomain(MediaType.MOVIE) }
    }

    override suspend fun searchTv(query: String): Result<List<Movie>> = runCatching {
        api.searchTv(query).results.map { it.toDomain(MediaType.TV) }
    }

    override suspend fun getMovieDetails(id: Int): Result<Movie> = runCatching {
        api.getMovieDetails(id).toDomain(MediaType.MOVIE)
    }

    override suspend fun getTvDetails(id: Int): Result<Movie> = runCatching {
        api.getTvDetails(id).toDomain(MediaType.TV)
    }
}

fun TmdbMovieDto.toDomain(mediaType: MediaType) = Movie(
    id = id,
    title = title ?: name ?: "",
    originalTitle = originalTitle ?: originalName ?: "",
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate ?: firstAirDate ?: "",
    voteAverage = voteAverage,
    genreIds = genreIds,
    mediaType = mediaType,
    runtime = runtime,
    numberOfSeasons = numberOfSeasons,
)
