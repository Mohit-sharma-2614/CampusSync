package com.example.campussync.data.repository

import com.example.campussync.data.database.entitiy.NoteEntity

interface NoteRepo {
    suspend fun insertNote(note: NoteEntity)
    suspend fun getNotesByCourse(courseId: Long): List<NoteEntity>
    suspend fun getNoteById(noteId: Long): NoteEntity?
    suspend fun deleteNoteById(noteId: Long)
}