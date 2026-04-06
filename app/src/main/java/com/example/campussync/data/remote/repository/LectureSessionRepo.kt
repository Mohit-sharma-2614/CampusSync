package com.example.campussync.data.remote.repository

import com.example.campussync.data.remote.dto.lecturesessions.LectureSessionsDto
import com.example.campussync.data.remote.dto.lecturesessions.LectureSessionsInputDto

interface LectureSessionRepo {
    suspend fun getAllLectureSessions(): List<LectureSessionsDto>
    suspend fun getLectureSessionById(id: Long): LectureSessionsDto
    // suspend fun getLectureSessionByCourseOfferingId(id: Long): List<LectureSessionsDto>
    suspend fun createLectureSession(lectureSessionDto: LectureSessionsInputDto): LectureSessionsDto
}

