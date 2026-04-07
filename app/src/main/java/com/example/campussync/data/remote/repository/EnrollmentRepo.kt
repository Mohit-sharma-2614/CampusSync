package com.example.campussync.data.remote.repository

import com.example.campussync.data.remote.dto.enrollment.EnrollmentDto
import com.example.campussync.data.remote.dto.enrollment.EnrollmentInputDto

interface EnrollmentRepo {
    suspend fun getAllEnrollments(): List<EnrollmentDto>
    suspend fun getEnrollmentById(id: Long): EnrollmentDto
    suspend fun getEnrollmentByStudentId(id: Long): List<EnrollmentDto>
    suspend fun getEnrollmentByCourseOfferingId(id: Long): List<EnrollmentDto>
    suspend fun getEnrollmentByStudentIdAndCourseOfferingId(studentId: Long, courseOfferingId: Long): List<EnrollmentDto>
    suspend fun createEnrollment(enrollmentDto: EnrollmentInputDto): EnrollmentDto

}