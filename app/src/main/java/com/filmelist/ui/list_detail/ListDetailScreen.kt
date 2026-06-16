package com.filmelist.ui.list_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.filmelist.domain.model.Movie
import com.filmelist.ui.components.MovieRowCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listId: Long,
    onBack: () -> Unit,
    onMovieClick: (Int, String) -> Unit,
    viewModel: ListDetailViewModel = hiltViewModel(),
) {
    val list by viewModel.list.collectAsState()
    var movieToRemove by remember { mutableStateOf<Movie?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(list?.name ?: "", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        val movies = list?.movies ?: emptyList()
        if (movies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.VideoLibrary,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Lista vazia",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    )
                    Text(
                        "Use a busca para adicionar filmes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(movies, key = { it.id }) { movie ->
                    MovieRowCard(
                        movie = movie,
                        onClick = { onMovieClick(movie.id, movie.mediaType.name) },
                        trailingContent = {
                            IconButton(onClick = { movieToRemove = movie }) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    "Remover",
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        },
                    )
                }
            }
        }
    }

    movieToRemove?.let { movie ->
        AlertDialog(
            onDismissRequest = { movieToRemove = null },
            title = { Text("Remover da lista") },
            text = { Text("Remover \"${movie.title}\" desta lista?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeMovie(movie.id)
                        movieToRemove = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text("Remover") }
            },
            dismissButton = { TextButton(onClick = { movieToRemove = null }) { Text("Cancelar") } },
        )
    }
}
