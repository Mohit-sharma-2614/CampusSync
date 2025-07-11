package com.example.campussync.data.repository

import com.example.campussync.data.model.student.StudentLoginRequest
import com.example.campussync.data.model.student.StudentLoginResponse
import com.example.campussync.utils.Resource
import retrofit2.Response

interface StudentRepository {
    suspend fun loginStudent(student: StudentLoginRequest): Resource<StudentLoginResponse>
    suspend fun getStudentById(studentId: Long): Resource<StudentLoginResponse>
    suspend fun getAllStudents(): Resource<List<StudentLoginResponse>>
    suspend fun updateStudent(student: StudentLoginRequest): Resource<StudentLoginResponse>
    suspend fun deleteStudent(studentId: Long): Resource<Unit> // Returns Response<Unit> for success/failure
}