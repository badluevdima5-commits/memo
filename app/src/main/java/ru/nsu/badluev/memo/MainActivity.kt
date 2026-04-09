package ru.nsu.badluev.memo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.nsu.badluev.memo.ui.theme.MemoTheme

data class Note(
    val title: String,
    val text: String
)

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoTheme {
                var showInput by remember { mutableStateOf(false) }
                var noteTitle by remember { mutableStateOf("") }
                var noteText by remember { mutableStateOf("") }
                val notes = remember { mutableStateListOf<Note>() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopAppBar(title = { Text("Мои заметки") }) },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showInput = true }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить заметку")}
                    }
                ){ innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding).padding(32.dp)){
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
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            )
                            OutlinedButton(onClick = {
                                notes.add(Note(title = noteTitle, text = noteText))
                                noteTitle = ""
                                noteText = ""
                                showInput = false
                            }) { Text("Сохранить") }
                        }
                        if (notes.isEmpty()) {
                            Text(text = "Заметок пока нет", modifier = Modifier.padding(top = 16.dp))
                        }
                        else {
                            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                                itemsIndexed(notes) { index, note -> Card(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                            Column(modifier = Modifier.weight(1f)) {
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
                                                    text = if (note.text.isBlank()) "Без текста" else note.text,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                            IconButton(onClick = { notes.remove(note) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Удалить заметку"
                                                ) } } } } } } } } } } } }
