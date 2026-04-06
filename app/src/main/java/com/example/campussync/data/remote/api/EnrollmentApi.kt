package com.example.campussync.data.remote.api

import com.example.campussync.data.remote.dto.enrollment.EnrollmentDto
import com.example.campussync.data.remote.dto.enrollment.EnrollmentInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class EnrollmentApi(
    private val client: HttpClient
) {
    suspend fun getAllEnrollments(): List<EnrollmentDto> {
        val enrollments = client.get("/enrollment/all")
        return enrollments.body()
    }
    suspend fun getEnrollmentById(id: Long): EnrollmentDto {
        val enrollment = client.get("/enrollment") {
            url {
                parameters.append("id", id.toString())
            }
        }
        return enrollment.body()
    }
    suspend fun getEnrollmentByStudentId(id: Long): List<EnrollmentDto>{
        val enrollments = client.get("/enrollment/student") {
            url {
                parameters.append("id", id.toString())
            }
        }
        return enrollments.body()
    }
    suspend fun getEnrollmentByCourseOfferingId(id: Long): List<EnrollmentDto>{
        val enrollments = client.get("/enrollment/course-offering") {
            url {
                parameters.append("id", id.toString())
            }
        }
        return enrollments.body()
    }
    suspend fun getEnrollmentByStudentIdAndCourseOfferingId(studentId: Long, courseOfferingId: Long): List<EnrollmentDto> {
        val enrollments = client.get("/enrollment/student-course-offering") {
            url{
                parameters.append("studentId", studentId.toString())
                parameters.append("courseOfferingId", courseOfferingId.toString())
            }
        }
        return enrollments.body()
    }
    suspend fun createEnrollment(enrollmentDto: EnrollmentInputDto): EnrollmentDto {
        return client.post("/enrollment"){
            setBody(enrollmentDto)
        }.body()
    }
}