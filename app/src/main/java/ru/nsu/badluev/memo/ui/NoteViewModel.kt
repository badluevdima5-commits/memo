package ru.nsu.badluev.memo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nsu.badluev.memo.data.NoteDatabase
import ru.nsu.badluev.memo.data.NoteEntity

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao = NoteDatabase.getDatabase(application).noteDao()
    val notes = noteDao.getAllNotes()

    fun saveNote(existingNote: NoteEntity?, title: String, text: String) {
        viewModelScope.launch {
            val note = existingNote?.copy(
                title = title,
                content = text
            ) ?: NoteEntity(title = title, content = text)

            noteDao.insertNote(note)
        }
    }
    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }
}