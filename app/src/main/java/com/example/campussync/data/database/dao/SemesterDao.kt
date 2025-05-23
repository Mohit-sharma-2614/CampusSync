package com.example.campussync.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.campussync.data.database.entitiy.SemesterEntity
import com.example.campussync.data.models.SemesterWithCourses

@Dao
interface SemesterDao {
    @Insert
    suspend fun insertSemester(semester: SemesterEntity)

    @Update
    suspend fun updateSemester(semester: SemesterEntity)

    @Transaction
    @Query("SELECT * FROM semesters WHERE student_owner_id = :studentId")
    suspend fun getSemestersWithCourses(studentId: String): List<SemesterWithCourses>

}