package com.example.campussync.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.campussync.data.database.entitiy.ClassSessionEntity

@Dao
interface ClassSessionDao {
    @Insert
    suspend fun insertClassSession(classSession: ClassSessionEntity)

    @Query("SELECT * FROM class_sessions WHERE courses_id = :courseId")
    suspend fun getClassSessionsByCourse(courseId: Long): List<ClassSessionEntity>

    @Update
    suspend fun updateClassSession(classSession: ClassSessionEntity)

    @Delete
    suspend fun deleteClassSession(classSession: ClassSessionEntity)

}