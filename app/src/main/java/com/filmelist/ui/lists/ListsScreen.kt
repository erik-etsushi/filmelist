package com.filmelist.ui.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.filmelist.BuildConfig
import com.filmelist.domain.model.MovieList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    onListClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: ListsViewModel = hiltViewModel(),
) {
    val lists by viewModel.lists.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var listToRename by remember { mutableStateOf<MovieList?>(null) }
    var listToDelete by remember { mutableStateOf<MovieList?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Listas", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, "Buscar")
                    }
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, "Nova lista")
                    }
                },
            )
        },
    ) { padding ->
        if (lists.isEmpty()) {
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
                    Text("Nenhuma lista ainda", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(lists, key = { it.id }) { list ->
                    ListCard(
                        list = list,
                        onClick = { onListClick(list.id) },
                        onRename = { listToRename = list },
                        onDelete = { listToDelete = list },
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateListDialog(
            onConfirm = { name ->
                viewModel.createList(name)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false },
        )
    }

    listToRename?.let { list ->
        RenameListDialog(
            currentName = list.name,
            onConfirm = { name ->
                viewModel.renameList(list.id, name)
                listToRename = null
            },
            onDismiss = { listToRename = null },
        )
    }

    listToDelete?.let { list ->
        AlertDialog(
            onDismissRequest = { listToDelete = null },
            title = { Text("Excluir lista") },
            text = { Text("Deseja excluir \"${list.name}\"? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteList(list.id)
                        listToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { listToDelete = null }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun ListCard(
    list: MovieList,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(140.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                val posters = list.movies.take(4).mapNotNull { it.posterPath }
                if (posters.isEmpty()) {
                    Icon(
                        Icons.Default.VideoLibrary,
                        null,
                        modifier = Modifier.size(48.dp).align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    )
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        items(posters) { path ->
                            AsyncImage(
                                model = "${BuildConfig.TMDB_IMAGE_BASE_URL}w185$path",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.width(80.dp).fillMaxHeight(),
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (list.isDefault) {
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(list.name, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "${list.movies.size} ${if (list.movies.size == 1) "título" else "títulos"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, "Opções")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Renomear") },
                            onClick = { menuExpanded = false; onRename() },
                            leadingIcon = { Icon(Icons.Default.Edit, null) },
                        )
                        if (!list.isDefault) {
                            DropdownMenuItem(
                                text = { Text("Excluir", color = MaterialTheme.colorScheme.error) },
                                onClick = { menuExpanded = false; onDelete() },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateListDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova lista") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Nome da lista") },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) {
                Text("Criar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
private fun RenameListDialog(currentName: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Renomear lista") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Nome da lista") },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) {
                Text("Salvar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
