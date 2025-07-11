package com.example.campussync.data.repository

import com.example.campussync.data.model.AttendanceToken
import com.example.campussync.utils.Resource

interface AttendanceTokenRepository {
    suspend fun getAllAttendanceToken(): Resource<List<AttendanceToken>>
    suspend fun getAttendanceTokenById(attendanceTokenId: Long): Resource<AttendanceToken>
    suspend fun createAttendanceToken(attendanceToke: AttendanceToken): Resource<AttendanceToken>
}