package com.filmelist.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmelist.domain.model.MovieList
import com.filmelist.domain.usecase.CreateListUseCase
import com.filmelist.domain.usecase.DeleteListUseCase
import com.filmelist.domain.usecase.GetAllListsUseCase
import com.filmelist.domain.usecase.RenameListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
    getAllLists: GetAllListsUseCase,
    private val createList: CreateListUseCase,
    private val deleteList: DeleteListUseCase,
    private val renameList: RenameListUseCase,
) : ViewModel() {

    val lists: StateFlow<List<MovieList>> = getAllLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createList(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { createList.invoke(name) }
    }

    fun deleteList(listId: Long) {
        viewModelScope.launch { deleteList.invoke(listId) }
    }

    fun renameList(listId: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { renameList.invoke(listId, name) }
    }
}
