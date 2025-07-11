package com.example.campussync.api

import com.example.campussync.data.model.AuthToken
import com.example.campussync.data.model.student.StudentLoginRequest
import com.example.campussync.data.model.student.StudentLoginResponse
import com.example.campussync.data.model.teacher.TeacherLoginRequest
import com.example.campussync.data.model.teacher.TeacherLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface AuthApiService {

    @POST("student/login")
    suspend fun loginStudent(
        @Body request: StudentLoginRequest
    ): Response<StudentLoginResponse>

    @POST("teacher/login")
    suspend fun loginTeacher(
        @Body request: TeacherLoginRequest
    ): Response<TeacherLoginResponse>


        @POST("/api/auth/validate-token")
        suspend fun validateToken(
            @Header("Authorization") token: String
        ): Response<AuthToken>


}