package com.example.campussync.api

import com.example.campussync.data.model.attendance.Attendance
import com.example.campussync.data.model.attendance.AttendanceReq
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AttendanceApiService {
    @GET("attendance/all")
    suspend fun getAllAttendance(): Response<List<Attendance>>

    @GET("attendance")
    suspend fun getAttendanceById(
        @Query("attendanceId") attendanceId: Long
    ): Response<Attendance>

    @GET("attendance/subject/{subjectId}")
    suspend fun getAttendanceBySubjectId(
        @Path("subjectId") subjectId: Long
    ): Response<List<Attendance>>

    @GET("attendance/student/{studentId}")
    suspend fun getAttendanceByStudentId(
        @Path("studentId") studentId: Long
    ): Response<List<Attendance>>

    @GET("attendance/subject/{subjectId}/student/{studentId}")
    suspend fun getAttendanceBySubjectAndStudentId(
        @Path("subjectId") subjectId: Long,
        @Path("studentId") studentId: Long
    ): Response<List<Attendance>>

    @POST("attendance")
    suspend fun createAttendance(
        @Body attendance: AttendanceReq
    ): Response<Attendance>

    @POST("attendance/bulk")
    suspend fun createBulkAttendance(@Body attendanceList: List<AttendanceReq>): Response<List<Attendance>>

    @GET("attendance/subject-date")
    suspend fun getAttendanceBySubjectAndDate(
        @Query("subjectId") subjectId: Long,
        @Query("date") date: String
    ): Response<List<Attendance>>
}