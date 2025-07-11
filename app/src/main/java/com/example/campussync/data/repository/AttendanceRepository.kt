package com.example.campussync.data.repository

import com.example.campussync.data.model.Attendance
import com.example.campussync.utils.Resource

interface AttendanceRepository {
    suspend fun getAllAttendance(): Resource<List<Attendance>>
    suspend fun getAttendanceById(attendanceId: Long): Resource<Attendance>
    suspend fun createAttendance(attendance: Attendance): Resource<Attendance>
}