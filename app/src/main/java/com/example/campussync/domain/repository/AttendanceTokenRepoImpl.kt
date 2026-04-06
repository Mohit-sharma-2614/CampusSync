package com.example.campussync.domain.repository

import com.example.campussync.data.remote.api.AttendanceTokenApi
import com.example.campussync.data.remote.dto.attendancetoken.AttendanceTokenDto
import com.example.campussync.data.remote.dto.attendancetoken.AttendanceTokenInputDto
import com.example.campussync.data.remote.repository.AttendanceTokenRepo

class AttendanceTokenRepoImpl(
    private val api: AttendanceTokenApi
) : AttendanceTokenRepo{
    override suspend fun getAllAttendanceToken(): List<AttendanceTokenDto> {
        return api.getAllAttendanceToken()
    }

    override suspend fun getAttendanceTokenById(id: Long): AttendanceTokenDto {
        return api.getAttendanceTokenById(id)
    }

    override suspend fun createAttendanceToken(attendanceTokenDto: AttendanceTokenInputDto): AttendanceTokenDto {
        return api.createAttendanceToken(attendanceTokenDto)
    }
}