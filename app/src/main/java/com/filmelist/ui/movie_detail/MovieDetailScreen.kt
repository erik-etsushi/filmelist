package com.filmelist.ui.movie_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.filmelist.BuildConfig
import com.filmelist.domain.model.MediaType
import com.filmelist.domain.model.Movie
import com.filmelist.domain.model.MovieList
import com.filmelist.domain.model.toGenreNames

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    mediaType: String,
    onBack: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var showListSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.addedMessage) {
        state.addedMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.movie != null) {
                ExtendedFloatingActionButton(
                    onClick = { showListSheet = true },
                    icon = { Icon(Icons.Default.PlaylistAdd, null) },
                    text = { Text("Adicionar à lista") },
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.error != null -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(Icons.Default.ErrorOutline, null, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Erro ao carregar detalhes", fontWeight = FontWeight.Bold)
                    TextButton(onClick = onBack) { Text("Voltar") }
                }
                state.movie != null -> MovieDetailContent(
                    movie = state.movie!!,
                    onBack = onBack,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }

    if (showListSheet && state.movie != null) {
        AddToListSheet(
            lists = state.lists,
            onListSelected = { listId ->
                viewModel.addToList(listId)
                showListSheet = false
            },
            onDismiss = { showListSheet = false },
        )
    }
}

@Composable
private fun MovieDetailContent(movie: Movie, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        movie.backdropPath?.let { path ->
            AsyncImage(
                model = "${BuildConfig.TMDB_IMAGE_BASE_URL}w780$path",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(300.dp).blur(4.dp),
            )
            Box(
                modifier = Modifier.fillMaxWidth().height(300.dp)
                    .background(Color.Black.copy(alpha = 0.5f)),
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(56.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.width(120.dp).height(180.dp),
                ) {
                    movie.posterPath?.let { path ->
                        AsyncImage(
                            model = "${BuildConfig.TMDB_IMAGE_BASE_URL}w342$path",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } ?: Box(
                        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Movie, null, modifier = Modifier.size(48.dp))
                    }
                }

                Column(
                    modifier = Modifier.weight(1f).padding(top = if (movie.backdropPath != null) 80.dp else 0.dp),
                ) {
                    Text(
                        movie.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if (movie.backdropPath != null) Color.White else MaterialTheme.colorScheme.onBackground,
                    )
                    if (movie.originalTitle != movie.title) {
                        Text(
                            movie.originalTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                        Text(
                            " ${String.format("%.1f", movie.voteAverage)}/10",
                            color = if (movie.backdropPath != null) Color.White else MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    InfoChipsRow(movie)
                    Spacer(Modifier.height(16.dp))
                    if (movie.genreIds.isNotEmpty()) {
                        Text("Gêneros", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(movie.genreIds.toGenreNames(), style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                    }
                    if (movie.overview.isNotBlank()) {
                        Text("Sinopse", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(movie.overview, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
                    }
                    Spacer(Modifier.height(80.dp))
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(8.dp).align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(50)),
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
        }
    }
}

@Composable
private fun InfoChipsRow(movie: Movie) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (movie.releaseDate.length >= 4) {
            InfoChip(movie.releaseDate.take(4))
        }
        if (movie.mediaType == MediaType.MOVIE && movie.runtime != null && movie.runtime > 0) {
            InfoChip("${movie.runtime} min")
        }
        if (movie.mediaType == MediaType.TV && movie.numberOfSeasons != null) {
            val seasons = movie.numberOfSeasons
            InfoChip("$seasons ${if (seasons == 1) "temporada" else "temporadas"}")
        }
        InfoChip(if (movie.mediaType == MediaType.MOVIE) "Filme" else "Série")
    }
}

@Composable
private fun InfoChip(label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddToListSheet(
    lists: List<MovieList>,
    onListSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                "Adicionar à lista",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
            )
            HorizontalDivider()
            lists.forEach { list ->
                ListItem(
                    headlineContent = { Text(list.name) },
                    supportingContent = { Text("${list.movies.size} títulos") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingContent = {
                        IconButton(onClick = { onListSelected(list.id) }) {
                            Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}
