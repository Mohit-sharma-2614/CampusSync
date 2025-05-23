package com.example.campussync.data.repository

import com.example.campussync.data.database.entitiy.SemesterEntity
import com.example.campussync.data.models.SemesterWithCourses

interface SemesterRepo {
    suspend fun insertSemester(semester: SemesterEntity)
    suspend fun updateSemester(semester: SemesterEntity)
    suspend fun getSemestersWithCourses(studentId: String): List<SemesterWithCourses>
}