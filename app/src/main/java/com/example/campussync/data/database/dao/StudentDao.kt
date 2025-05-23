package com.example.campussync.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.campussync.data.database.entitiy.SemesterEntity
import com.example.campussync.data.database.entitiy.StudentsEntity
import com.example.campussync.data.models.StudentWithSemesters
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Transaction
    @Query("SELECT * FROM students")
    suspend fun getStudentsWithSemesters(): List<StudentWithSemesters>

    @Transaction
    @Query("SELECT * FROM students WHERE student_id = :studentId")
    suspend fun getStudentWithSemesters(studentId: String): StudentWithSemesters?

    @Insert
    suspend fun insertStudent(student: StudentsEntity)
}