package com.example.campussync.data.remote.repository

import com.example.campussync.data.remote.dto.attendancetoken.AttendanceTokenDto
import com.example.campussync.data.remote.dto.attendancetoken.AttendanceTokenInputDto

interface AttendanceTokenRepo {
    suspend fun getAllAttendanceToken(): List<AttendanceTokenDto>
    suspend fun getAttendanceTokenById(id: Long): AttendanceTokenDto
    suspend fun createAttendanceToken(attendanceTokenDto: AttendanceTokenInputDto): AttendanceTokenDto
}
