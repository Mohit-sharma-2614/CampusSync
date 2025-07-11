package com.example.campussync.api

import com.example.campussync.data.model.student.StudentLoginRequest
import com.example.campussync.data.model.student.StudentLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface StudentApiService {
//    @POST("student/login")
//    suspend fun loginStudent(@Body request: StudentLoginRequest): StudentLoginResponse
//
    @GET("student")
    suspend fun getStudentById(@Query("studentId") studentId: Long): Response<StudentLoginResponse>

    @GET("students")
    suspend fun getAllStudents(): Response<List<StudentLoginResponse>>

    @PUT("student/update")
    suspend fun updateStudent(@Body student: StudentLoginRequest): Response<StudentLoginResponse>

    @DELETE("student/{id}")
    suspend fun deleteStudent(@Path("id") studentId: Long): Response<Unit> // Or some confirmation response
}