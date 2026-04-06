package com.example.campussync.domain.repository

import com.example.campussync.data.remote.api.AttendanceApi
import com.example.campussync.data.remote.dto.attendance.AttendanceDto
import com.example.campussync.data.remote.dto.attendance.AttendanceInputDto
import com.example.campussync.data.remote.repository.AttendanceRepo

class AttendanceRepoImpl(
    private val api: AttendanceApi
): AttendanceRepo {
    override suspend fun getAllAttendance(): List<AttendanceDto> {
        return api.getAllAttendance()
    }

    override suspend fun getAttendanceById(id: Long): AttendanceDto {
        return api.getAttendanceById(id)
    }

    override suspend fun getAttendanceByLectureSessionId(id: Long): List<AttendanceDto> {
        return api.getAttendanceByLectureSessionId(id)
    }

    override suspend fun getAttendanceByEnrollmentId(id: Long): List<AttendanceDto> {
        return api.getAttendanceByEnrollmentId(id)
    }

    override suspend fun getAttendanceByLectureSessionAndEnrollmentId(
        lectureSessionId: Long,
        enrollmentId: Long
    ): List<AttendanceDto> {
        return api.getAttendanceByLectureSessionAndEnrollmentId(lectureSessionId,enrollmentId)
    }

    override suspend fun createBulkAttendance(attendanceDtos: List<AttendanceInputDto>): List<AttendanceDto> {
        return api.createBulkAttendance(attendanceDtos)
    }

    override suspend fun createAttendance(attendanceDto: AttendanceInputDto): AttendanceDto {
        return api.createAttendance(attendanceDto)
    }
}