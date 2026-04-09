package ru.nsu.badluev.memo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import ru.nsu.badluev.memo.data.NoteEntity
import ru.nsu.badluev.memo.ui.NoteViewModel
import ru.nsu.badluev.memo.ui.theme.MemoTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: NoteViewModel by viewModels()

        setContent {
            MemoTheme {
                NoteScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteScreen(viewModel: NoteViewModel) {
    var showInput by remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    val notes by viewModel.notes.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Мои заметки") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showInput = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить заметку"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (showInput) {
                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    label = { Text("Заголовок") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Текст заметки") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                OutlinedButton(
                    onClick = {
                        if (noteTitle.isNotBlank() || noteText.isNotBlank()) {
                            viewModel.saveNote(noteTitle, noteText)
                            noteTitle = ""
                            noteText = ""
                            showInput = false
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Сохранить")
                }
            }

            if (notes.isEmpty()) {
                Text(
                    text = "Заметок пока нет",
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    itemsIndexed(notes) { index, note ->
                        NoteCard(
                            index = index,
                            note = note,
                            onDeleteClick = { viewModel.deleteNote(note) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteCard(
    index: Int,
    note: NoteEntity,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Text(
                    text = "Заметка ${index + 1}",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (note.title.isBlank()) "Без заголовка" else note.title,
                    modifier = Modifier.padding(top = 8.dp),
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = if (note.content.isBlank()) "Без текста" else note.content,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить заметку"
                )
            }
        }
    }
}
