package com.example.campussync.data.repository

import com.example.campussync.data.model.Enrollment
import com.example.campussync.utils.Resource
import retrofit2.Response

interface EnrollmentRepository {
    suspend fun getAllEnrollments(): Resource<List<Enrollment>>
    suspend fun getEnrollmentById(enrollmentId: Long): Resource<Enrollment>
    suspend fun getEnrollmentsByStudentId(studentId: Long): Resource<List<Enrollment>>
    suspend fun getEnrollmentsBySubjectId(subjectId: Long): Resource<List<Enrollment>>
    suspend fun getEnrollmentsByStudentIdAndSubjectId(studentId: Long, subjectId: Long): Resource<List<Enrollment>>
}