package com.filmelist.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmelist.domain.model.MediaType
import com.filmelist.domain.model.Movie
import com.filmelist.domain.model.MovieList
import com.filmelist.domain.usecase.AddMovieToListUseCase
import com.filmelist.domain.usecase.GetAllListsUseCase
import com.filmelist.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val mediaType: MediaType = MediaType.MOVIE,
    val results: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val lists: List<MovieList> = emptyList(),
    val addedToListMessage: String? = null,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovies: SearchMoviesUseCase,
    private val addMovieToList: AddMovieToListUseCase,
    getAllLists: GetAllListsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    init {
        getAllLists().onEach { lists ->
            _state.update { it.copy(lists = lists) }
        }.launchIn(viewModelScope)

        _state.map { it.query to it.mediaType }
            .debounce(400)
            .distinctUntilChanged()
            .onEach { (query, type) -> performSearch(query, type) }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query, error = null) }
    }

    fun onMediaTypeChange(mediaType: MediaType) {
        _state.update { it.copy(mediaType = mediaType, results = emptyList()) }
    }

    private suspend fun performSearch(query: String, mediaType: MediaType) {
        if (query.isBlank()) {
            _state.update { it.copy(results = emptyList(), isLoading = false) }
            return
        }
        _state.update { it.copy(isLoading = true) }
        searchMovies(query, mediaType)
            .onSuccess { results -> _state.update { it.copy(results = results, isLoading = false) } }
            .onFailure { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
    }

    fun addToList(listId: Long, movie: Movie) {
        viewModelScope.launch {
            addMovieToList(listId, movie)
            _state.update { it.copy(addedToListMessage = "\"${movie.title}\" adicionado!") }
        }
    }

    fun clearMessage() {
        _state.update { it.copy(addedToListMessage = null) }
    }
}
