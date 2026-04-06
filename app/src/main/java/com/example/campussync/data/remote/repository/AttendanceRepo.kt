package com.example.campussync.data.remote.repository

import com.example.campussync.data.remote.dto.attendance.AttendanceDto
import com.example.campussync.data.remote.dto.attendance.AttendanceInputDto

interface AttendanceRepo {
    suspend fun getAllAttendance(): List<AttendanceDto>
    suspend fun getAttendanceById(id: Long): AttendanceDto
    suspend fun getAttendanceByLectureSessionId(id: Long): List<AttendanceDto>
    suspend fun getAttendanceByEnrollmentId(id: Long): List<AttendanceDto>
    suspend fun getAttendanceByLectureSessionAndEnrollmentId(lectureSessionId: Long, enrollmentId: Long): List<AttendanceDto>
    suspend fun createBulkAttendance(attendanceDtos: List<AttendanceInputDto>): List<AttendanceDto>
    suspend fun createAttendance(attendanceDto: AttendanceInputDto): AttendanceDto
}