package com.example.campussync.data.repository

import com.example.campussync.data.database.entitiy.AttendanceRecordEntity

interface AttendanceRecordRepo {
    suspend fun insertAttendanceRecord(attendanceRecord: AttendanceRecordEntity)
    suspend fun updateAttendanceRecord(attendanceRecord: AttendanceRecordEntity)
    suspend fun deleteAttendanceRecord(attendanceRecord: AttendanceRecordEntity)
    suspend fun getAttendanceRecordsByCourse(courseId: Long): List<AttendanceRecordEntity>
    suspend fun getAttendanceRecordsBySession(sessionId: Long): List<AttendanceRecordEntity>
}