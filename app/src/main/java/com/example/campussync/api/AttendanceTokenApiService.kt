package com.example.campussync.api

import com.example.campussync.data.model.AttendanceToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AttendanceTokenApiService {

    @GET("attendance_token/all")
    suspend fun getAllAttendanceTokens(): Response<List<AttendanceToken>>

    @GET("attendance_token")
    suspend fun getAttendanceTokenById(
        @Query("attendanceTokenId") attendanceTokenId: Long
    ): Response<AttendanceToken>

    @POST("attendance_token")
    suspend fun createAttendanceToken(
        @Body attendanceToken: AttendanceToken
    ): Response<AttendanceToken>

}