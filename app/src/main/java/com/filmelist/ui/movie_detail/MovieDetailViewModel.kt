package com.filmelist.ui.movie_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmelist.domain.model.MediaType
import com.filmelist.domain.model.Movie
import com.filmelist.domain.model.MovieList
import com.filmelist.domain.repository.MovieRepository
import com.filmelist.domain.usecase.AddMovieToListUseCase
import com.filmelist.domain.usecase.GetAllListsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailUiState(
    val movie: Movie? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val lists: List<MovieList> = emptyList(),
    val addedMessage: String? = null,
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository,
    private val addMovieToList: AddMovieToListUseCase,
    getAllLists: GetAllListsUseCase,
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])
    private val mediaType: MediaType = MediaType.valueOf(
        checkNotNull(savedStateHandle["mediaType"])
    )

    private val _state = MutableStateFlow(MovieDetailUiState())
    val state: StateFlow<MovieDetailUiState> = _state.asStateFlow()

    init {
        getAllLists().onEach { lists ->
            _state.update { it.copy(lists = lists) }
        }.launchIn(viewModelScope)

        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = if (mediaType == MediaType.MOVIE) {
                movieRepository.getMovieDetails(movieId)
            } else {
                movieRepository.getTvDetails(movieId)
            }
            result
                .onSuccess { movie -> _state.update { it.copy(movie = movie, isLoading = false) } }
                .onFailure { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun addToList(listId: Long) {
        val movie = _state.value.movie ?: return
        viewModelScope.launch {
            addMovieToList(listId, movie)
            _state.update { it.copy(addedMessage = "Adicionado à lista!") }
        }
    }

    fun clearMessage() = _state.update { it.copy(addedMessage = null) }
}
