package com.example.campussync.domain.repository

import com.example.campussync.data.remote.api.EnrollmentApi
import com.example.campussync.data.remote.dto.enrollment.EnrollmentDto
import com.example.campussync.data.remote.dto.enrollment.EnrollmentInputDto
import com.example.campussync.data.remote.repository.EnrollmentRepo

class EnrollmentRepoImpl(
    private val api: EnrollmentApi
) : EnrollmentRepo{
    override suspend fun getAllEnrollments(): List<EnrollmentDto> {
        return api.getAllEnrollments()
    }

    override suspend fun getEnrollmentById(id: Long): EnrollmentDto {
        return api.getEnrollmentById(id)
    }

    override suspend fun getEnrollmentByStudentId(id: String): List<EnrollmentDto> {
        return api.getEnrollmentByStudentId(id.toLong())
    }

    override suspend fun getEnrollmentByCourseOfferingId(id: Long): List<EnrollmentDto> {
        return api.getEnrollmentByCourseOfferingId(id)
    }

    override suspend fun getEnrollmentByStudentIdAndCourseOfferingId(
        studentId: Long,
        courseOfferingId: Long
    ): List<EnrollmentDto> {
        return api.getEnrollmentByStudentIdAndCourseOfferingId(studentId,courseOfferingId)
    }

    override suspend fun createEnrollment(enrollmentDto: EnrollmentInputDto): EnrollmentDto {
        return api.createEnrollment(enrollmentDto)
    }
}