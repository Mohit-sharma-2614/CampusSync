package com.example.campussync.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.campussync.data.database.entitiy.NoteEntity

@Dao
interface NoteDao {
    @Insert
    suspend fun insertNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE course_id = :courseId")
    suspend fun getNotesByCourse(courseId: Long): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE note_id = :noteId")
    suspend fun getNoteById(noteId: Long): NoteEntity?

    @Query("DELETE FROM notes WHERE note_id = :noteId")
    suspend fun deleteNoteById(noteId: Long)
}