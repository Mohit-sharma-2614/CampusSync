package com.example.campussync.data.remote.api

import com.example.campussync.data.remote.dto.attendance.AttendanceDto
import com.example.campussync.data.remote.dto.attendance.AttendanceInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AttendanceApi(
    private val client: HttpClient
) {
    suspend fun getAllAttendance(): List<AttendanceDto>{
        val attendance = client.get("/attendance/all")
        return attendance.body()
    }
    suspend fun getAttendanceById(id: Long): AttendanceDto {
        val attendance = client.get("/attendance") {
            url {
                parameters.append("id", id.toString())
            }
        }
        return attendance.body()
    }
    suspend fun getAttendanceByLectureSessionId(id: Long): List<AttendanceDto>{
        val attendance = client.get("/attendance/lecture-session/{lectureSessionId}") {
            url {
                parameters.append("lectureSessionId", id.toString())
            }
        }
        return attendance.body()
    }

    suspend fun getAttendanceByEnrollmentId(id: Long): List<AttendanceDto>{
        val attendance = client.get("/attendance/enrollment/{enrollmentId}") {
            url {
                parameters.append("enrollmentId", id.toString())
            }
        }
        return attendance.body()
    }

    suspend fun getAttendanceByLectureSessionAndEnrollmentId(lectureSessionId: Long, enrollmentId: Long): List<AttendanceDto>{
        val attendance = client.get("/attendance/lecture-session/{lectureSessionId}/enrollment/{enrollmentId}") {
            url {
                parameters.append("lectureSessionId", lectureSessionId.toString())
                parameters.append("enrollmentId", enrollmentId.toString())
            }
        }
        return attendance.body()
    }

    suspend fun createBulkAttendance(attendanceDto: List<AttendanceInputDto>): List<AttendanceDto>{
        return client.post("/attendance/bulk"){
            setBody(attendanceDto)
        }.body()
    }

    suspend fun createAttendance(attendanceDto: AttendanceInputDto): AttendanceDto{
        return client.post("/attendance"){
            setBody(attendanceDto)
        }.body()
    }
}