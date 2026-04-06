package com.example.campussync.domain.repository

import com.example.campussync.data.remote.api.LectureSessionApi
import com.example.campussync.data.remote.dto.lecturesessions.LectureSessionsDto
import com.example.campussync.data.remote.dto.lecturesessions.LectureSessionsInputDto
import com.example.campussync.data.remote.repository.LectureSessionRepo

class LectureSessionRepoImpl(
    private val api: LectureSessionApi
) : LectureSessionRepo {
    override suspend fun getAllLectureSessions(): List<LectureSessionsDto> {
        return api.getAllLectureSessions()
    }

    override suspend fun getLectureSessionById(id: Long): LectureSessionsDto {
        return api.getLectureSessionById(id)
    }

//    override suspend fun getLectureSessionByCourseOfferingId(id: Long): List<LectureSessionsDto> {
//        return api.getLectureSessionByCourseOfferingId(id)
//    }

    override suspend fun createLectureSession(lectureSessionDto: LectureSessionsInputDto): LectureSessionsDto {
        return api.createLectureSession(lectureSessionDto)
    }
}