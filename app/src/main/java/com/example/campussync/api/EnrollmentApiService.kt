package com.example.campussync.api

import com.example.campussync.data.model.Enrollment
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EnrollmentApiService {
    @GET("enrollment/all")
    suspend fun getAllEnrollments(): Response<List<Enrollment>>

    @GET("enrollment")
    suspend fun getEnrollmentById(
        @Query("enrollmentId") enrollmentId: Long
    ): Response<Enrollment>

    @GET("enrollment/student")
    suspend fun getEnrollmentsByStudentId(
        @Query("studentId") studentId: Long
    ): Response<List<Enrollment>>

    @GET("enrollment/subject")
    suspend fun getEnrollmentsBySubjectId(
        @Query("subjectId") subjectId: Long
    ): Response<List<Enrollment>>

    @GET("enrollment/student-subject")
    suspend fun getEnrollmentsByStudentIdAndSubjectId(
        @Query("studentId") studentId: Long,
        @Query("subjectId") subjectId: Long
    ): Response<List<Enrollment>>

}