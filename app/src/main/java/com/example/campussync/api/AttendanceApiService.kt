package com.example.campussync.api

import com.example.campussync.data.model.Attendance
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AttendanceApiService {
    @GET("attendance/all")
    suspend fun getAllAttendance(): Response<List<Attendance>>

    @GET("attendance")
    suspend fun getAttendanceById(
        @Query("attendanceId") attendanceId: Long
    ): Response<Attendance>

    @POST("attendance")
    suspend fun createAttendance(
        @Body attendance: Attendance
    ): Response<Attendance>
}