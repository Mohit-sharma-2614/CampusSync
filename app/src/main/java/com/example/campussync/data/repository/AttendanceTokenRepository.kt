package com.example.campussync.data.repository

import com.example.campussync.data.model.attendanceToken.AttendanceToken
import com.example.campussync.data.model.attendanceToken.AttendanceTokenReq
import com.example.campussync.utils.Resource

interface AttendanceTokenRepository {
    suspend fun getAllAttendanceToken(): Resource<List<AttendanceToken>>
    suspend fun getAttendanceTokenById(attendanceTokenId: Long): Resource<AttendanceToken>
    suspend fun createAttendanceToken(attendanceToke: AttendanceTokenReq): Resource<AttendanceToken>
}