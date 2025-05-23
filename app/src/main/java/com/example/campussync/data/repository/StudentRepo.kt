package com.example.campussync.data.repository

import com.example.campussync.data.database.entitiy.StudentsEntity
import com.example.campussync.data.models.StudentWithSemesters

interface StudentRepo {
    suspend fun insertStudent(student: StudentsEntity)
    suspend fun getStudentsWithSemesters(): List<StudentWithSemesters>
    suspend fun getStudentWithSemesters(studentId: String): StudentWithSemesters?
}