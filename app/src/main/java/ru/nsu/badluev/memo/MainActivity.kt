package ru.nsu.badluev.memo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import ru.nsu.badluev.memo.ui.theme.MemoTheme


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoTheme {
                var showInput by remember { mutableStateOf(false) }
                var noteText by remember { mutableStateOf("") }
                val notes = remember { mutableStateListOf<String>() }
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {TopAppBar(title = {Text("Мои заметки")})},
                    floatingActionButton = {FloatingActionButton(onClick = {showInput = true}) {Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить заметку"
                    )}}

                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding).padding(32.dp)
                    ){
                        if (showInput) {
                            OutlinedTextField(
                                value = noteText,
                                onValueChange = { noteText = it },
                                label = { Text("Введите заметку") },
                                modifier = Modifier.padding(innerPadding)
                            )
                            OutlinedButton(onClick = {
                                notes.add(noteText)
                                noteText = ""
                                showInput = false
                            }) { Text("Сохранить") }
                        }
                        if (notes.isEmpty()) {
                            Text(
                                text = "Заметок пока нет",
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                        else{
                            LazyColumn(modifier = Modifier.padding(top = 16.dp))
                            {
                                items(notes) { note ->
                                    Text(
                                        text = note,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
