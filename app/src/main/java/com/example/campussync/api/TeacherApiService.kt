package com.example.campussync.api

import com.example.campussync.data.model.teacher.TeacherLoginRequest
import com.example.campussync.data.model.teacher.TeacherLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TeacherApiService {
//    @POST("teacher/login")
//    suspend fun loginTeacher(@Body request: TeacherLoginRequest): TeacherLoginResponse // Retrofit returns the direct type

    @GET("teacher")
    suspend fun getTeacherById(@Query("teacherId") teacherId: Long): Response<TeacherLoginResponse>

    @GET("teachers")
    suspend fun getAllTeachers(): Response<List<TeacherLoginResponse>>

    @PUT("teacher/update")
    suspend fun updateTeacher(@Body teacher: TeacherLoginRequest): Response<TeacherLoginResponse>

    @DELETE("teacher/{id}")
    suspend fun deleteTeacher(@Path("id") teacherId: Long): Unit // Or some confirmation response
 }