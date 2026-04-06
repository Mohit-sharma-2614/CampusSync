package com.example.campussync.data.remote.api

import com.example.campussync.data.remote.dto.lecturesessions.LectureSessionsDto
import com.example.campussync.data.remote.dto.lecturesessions.LectureSessionsInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class LectureSessionApi(
    private val client: HttpClient
) {
    suspend fun getAllLectureSessions(): List<LectureSessionsDto> {
        val lectureSessions = client.get("/lecturesessions/all")
        return lectureSessions.body()
    }
    suspend fun getLectureSessionById(id: Long): LectureSessionsDto {
        val lectureSession = client.get("/lecturesessions"){
            url{
                parameters.append("id", id.toString())
            }
        }
        return lectureSession.body()
    }
    suspend fun createLectureSession(lectureSessionDto: LectureSessionsInputDto): LectureSessionsDto{
        return client.post("/lecturesessions"){
            setBody(lectureSessionDto)
        }.body()
    }
}

