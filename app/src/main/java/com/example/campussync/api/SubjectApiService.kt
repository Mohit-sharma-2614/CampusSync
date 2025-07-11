package com.example.campussync.api

import com.example.campussync.data.model.Subject
import retrofit2.Response
import retrofit2.http.*

interface SubjectApiService {

    @GET("subject/all")
    suspend fun getAllSubjects(): Response<List<Subject>>

    @GET("subject")
    suspend fun getSubjectById(
        @Query("subjectId") subjectId: Long
    ): Response<Subject>

    /*

    @POST("subject")
    suspend fun createSubject(
        @Body subject: Subject
    ): Response<Subject>

    @PUT("subject")
    suspend fun updateSubject(
        @Body subject: Subject
    ): Response<Subject>

    @DELETE("subject")
    suspend fun deleteSubject(
        @Query("subjectId") subjectId: Long
    ): Response<Void>

    */

}