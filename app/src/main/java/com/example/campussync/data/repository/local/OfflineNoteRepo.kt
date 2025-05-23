package com.example.campussync.data.repository.local

import com.example.campussync.data.database.dao.NoteDao
import com.example.campussync.data.database.entitiy.NoteEntity
import com.example.campussync.data.repository.NoteRepo

class OfflineNoteRepo(
    private val noteDao: NoteDao
): NoteRepo {
    override suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)

    override suspend fun getNotesByCourse(courseId: Long): List<NoteEntity> = noteDao.getNotesByCourse(courseId)

    override suspend fun getNoteById(noteId: Long): NoteEntity? = noteDao.getNoteById(noteId)

    override suspend fun deleteNoteById(noteId: Long) = noteDao.deleteNoteById(noteId)
}