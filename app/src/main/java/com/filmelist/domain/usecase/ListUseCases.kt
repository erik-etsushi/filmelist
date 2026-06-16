package com.filmelist.domain.usecase

import com.filmelist.domain.model.Movie
import com.filmelist.domain.model.MovieList
import com.filmelist.domain.repository.ListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllListsUseCase @Inject constructor(private val repo: ListRepository) {
    operator fun invoke(): Flow<List<MovieList>> = repo.getAllLists()
}

class GetListWithMoviesUseCase @Inject constructor(private val repo: ListRepository) {
    operator fun invoke(listId: Long): Flow<MovieList?> = repo.getListWithMovies(listId)
}

class CreateListUseCase @Inject constructor(private val repo: ListRepository) {
    suspend operator fun invoke(name: String): Long = repo.createList(name)
}

class DeleteListUseCase @Inject constructor(private val repo: ListRepository) {
    suspend operator fun invoke(listId: Long) = repo.deleteList(listId)
}

class RenameListUseCase @Inject constructor(private val repo: ListRepository) {
    suspend operator fun invoke(listId: Long, newName: String) = repo.renameList(listId, newName)
}

class AddMovieToListUseCase @Inject constructor(private val repo: ListRepository) {
    suspend operator fun invoke(listId: Long, movie: Movie) = repo.addMovieToList(listId, movie)
}

class RemoveMovieFromListUseCase @Inject constructor(private val repo: ListRepository) {
    suspend operator fun invoke(listId: Long, movieId: Int) = repo.removeMovieFromList(listId, movieId)
}
