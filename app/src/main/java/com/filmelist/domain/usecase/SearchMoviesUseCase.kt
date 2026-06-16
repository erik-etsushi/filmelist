package com.filmelist.domain.usecase

import com.filmelist.domain.model.Movie
import com.filmelist.domain.model.MediaType
import com.filmelist.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    suspend operator fun invoke(query: String, mediaType: MediaType): Result<List<Movie>> {
        if (query.isBlank()) return Result.success(emptyList())
        return if (mediaType == MediaType.MOVIE) {
            repository.searchMovies(query)
        } else {
            repository.searchTv(query)
        }
    }
}
