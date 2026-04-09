package ru.nsu.badluev.memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.nsu.badluev.memo.data.NoteEntity
import ru.nsu.badluev.memo.ui.NoteViewModel
import ru.nsu.badluev.memo.ui.theme.MemoTheme

private sealed interface ScreenState {
    object List : ScreenState
    data class Details(val note: NoteEntity) : ScreenState
    data class Editor(val note: NoteEntity?) : ScreenState
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: NoteViewModel by viewModels()

        setContent {
            MemoTheme {
                MemoApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoApp(viewModel: NoteViewModel) {
    val notes by viewModel.notes.collectAsState(initial = emptyList())
    var screenState by remember { mutableStateOf<ScreenState>(ScreenState.List) }

    BackHandler(enabled = screenState != ScreenState.List) {
        screenState = ScreenState.List
    }

    val screenTitle = when (val state = screenState) {
        ScreenState.List -> "Мои заметки"
        is ScreenState.Details -> "Просмотр"
        is ScreenState.Editor -> if (state.note == null) "Новая заметка" else "Редактирование"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    if (screenState != ScreenState.List) {
                        IconButton(onClick = { screenState = ScreenState.List }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (screenState == ScreenState.List) {
                FloatingActionButton(onClick = { screenState = ScreenState.Editor(null) }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = screenState) {
                ScreenState.List -> {
                    NotesList(
                        notes = notes,
                        onNoteClick = { screenState = ScreenState.Details(it) },
                        onDeleteClick = { viewModel.deleteNote(it) }
                    )
                }
                is ScreenState.Details -> {
                    NoteDetails(
                        note = state.note,
                        onEditClick = { screenState = ScreenState.Editor(state.note) },
                        onDeleteClick = {
                            viewModel.deleteNote(state.note)
                            screenState = ScreenState.List
                        }
                    )
                }
                is ScreenState.Editor -> {
                    NoteEditor(
                        note = state.note,
                        onSaveClick = { title, content ->
                            viewModel.saveNote(state.note, title, content)
                            screenState = ScreenState.List
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotesList(
    notes: List<NoteEntity>,
    onNoteClick: (NoteEntity) -> Unit,
    onDeleteClick: (NoteEntity) -> Unit
) {
    if (notes.isEmpty()) {
        Text(text = "Заметок пока нет", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes, key = { it.id }) { note ->
                NoteCard(
                    note = note,
                    onClick = { onNoteClick(note) },
                    onDeleteClick = { onDeleteClick(note) }
                )
            }
        }
    }
}

@Composable
private fun NoteCard(
    note: NoteEntity,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title.ifBlank { "Без заголовка" },
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content.ifBlank { "Без текста" },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Composable
private fun NoteDetails(
    note: NoteEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = note.title.ifBlank { "Без заголовка" },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = note.content.ifBlank { "Текст отсутствует" },
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(16.dp))
            FloatingActionButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
        }
    }
}

@Composable
private fun NoteEditor(
    note: NoteEntity?,
    onSaveClick: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Заголовок") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Текст заметки") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSaveClick(title, content) },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() || content.isNotBlank()
        ) {
            Text("Сохранить")
        }
    }
}