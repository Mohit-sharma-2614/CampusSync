package com.example.campussync.data.remote.api

import com.example.campussync.data.remote.dto.attendancetoken.AttendanceTokenDto
import com.example.campussync.data.remote.dto.attendancetoken.AttendanceTokenInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AttendanceTokenApi(
    private val client: HttpClient
) {
    suspend fun getAllAttendanceToken(): List<AttendanceTokenDto>{
        val attendanceToken = client.get("/attendance_token/all")
        return attendanceToken.body()
    }
    suspend fun getAttendanceTokenById(id: Long): AttendanceTokenDto {
        val attendanceToken = client.get("/attendance_token") {
            url {
                parameters.append("id", id.toString())
            }
        }
        return attendanceToken.body()
    }
    suspend fun createAttendanceToken(attendanceTokenDto: AttendanceTokenInputDto): AttendanceTokenDto{
        return client.post("/attendance_token") {
            setBody(attendanceTokenDto)
        }.body()
    }
}