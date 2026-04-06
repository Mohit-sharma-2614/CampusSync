package com.example.campussync.domain.repository

import com.example.campussync.data.remote.api.CourseOfferingApi
import com.example.campussync.data.remote.dto.courseofferings.CourseOfferingsDto
import com.example.campussync.data.remote.dto.courseofferings.CourseOfferingsInputDto
import com.example.campussync.data.remote.repository.CourseOfferingsRepo

class CourseOfferingsRepoImpl(
    private val api: CourseOfferingApi
) : CourseOfferingsRepo{
    override suspend fun getAllCourseOfferings(): List<CourseOfferingsDto> {
        return api.getAllCourseOfferings()
    }

    override suspend fun getCourseOfferingById(id: Long): CourseOfferingsDto {
        return api.getCourseOfferingById(id)
    }

    override suspend fun getCourseOfferingsByTeacherId(id: Long): List<CourseOfferingsDto> {
        return api.getCourseOfferingsByTeacherId(id)
    }

    override suspend fun search(subjectId: Long,teacherId: Long,semester: String): List<CourseOfferingsDto> {
        return api.search(subjectId,teacherId,semester)
    }

    override suspend fun createCourseOffering(courseOfferingDto: CourseOfferingsInputDto): CourseOfferingsDto {
        return api.createCourseOffering(courseOfferingDto)
    }
}