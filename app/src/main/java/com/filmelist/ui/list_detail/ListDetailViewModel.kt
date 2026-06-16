package com.filmelist.ui.list_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmelist.domain.model.MovieList
import com.filmelist.domain.usecase.GetListWithMoviesUseCase
import com.filmelist.domain.usecase.RemoveMovieFromListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getListWithMovies: GetListWithMoviesUseCase,
    private val removeMovie: RemoveMovieFromListUseCase,
) : ViewModel() {

    private val listId: Long = checkNotNull(savedStateHandle["listId"])

    val list: StateFlow<MovieList?> = getListWithMovies(listId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun removeMovie(movieId: Int) {
        viewModelScope.launch { removeMovie(listId, movieId) }
    }
}
