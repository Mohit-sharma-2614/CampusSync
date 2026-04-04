package com.example.campussync.data.remote.repository

import com.example.campussync.data.remote.dto.student.StudentDto
import com.example.campussync.data.remote.dto.student.StudentInputDto

interface StudentRepo {
    suspend fun getStudentById(id: Long): StudentDto
    suspend fun getAllStudents(): List<StudentDto>
    suspend fun registerStudent(studentDto: StudentInputDto): StudentDto
    suspend fun updateStudent(id: Long, studentDto: StudentInputDto): StudentDto
    suspend fun deleteStudent(id: Long)
}