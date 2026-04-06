package com.example.campussync.data.remote.repository

import com.example.campussync.data.remote.dto.courseofferings.CourseOfferingsDto
import com.example.campussync.data.remote.dto.courseofferings.CourseOfferingsInputDto

interface CourseOfferingsRepo {
    suspend fun getAllCourseOfferings(): List<CourseOfferingsDto>
    suspend fun getCourseOfferingById(id: Long): CourseOfferingsDto

    suspend fun getCourseOfferingsByTeacherId(id: Long): List<CourseOfferingsDto>

    suspend fun search(subjectId: Long,teacherId: Long,semester: String): List<CourseOfferingsDto>
    suspend fun createCourseOffering(courseOfferingDto: CourseOfferingsInputDto): CourseOfferingsDto
}