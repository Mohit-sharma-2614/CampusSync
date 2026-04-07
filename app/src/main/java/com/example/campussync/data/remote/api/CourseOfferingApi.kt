package com.example.campussync.data.remote.api

import com.example.campussync.data.remote.dto.courseofferings.CourseOfferingsDto
import com.example.campussync.data.remote.dto.courseofferings.CourseOfferingsInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class CourseOfferingApi(
    private val client: HttpClient
) {
    suspend fun getAllCourseOfferings(): List<CourseOfferingsDto> {
        val courseOfferings = client.get("/course-offerings/all")
        return courseOfferings.body()
    }

    suspend fun getCourseOfferingById(id: Long): CourseOfferingsDto {
        val courseOffering = client.get("/course-offerings") {
            url {
                parameters.append("courseOfferingId", id.toString())
            }
        }
        return courseOffering.body()
    }

    suspend fun getCourseOfferingsByTeacherId(id: Long): List<CourseOfferingsDto> {
        val courseOfferings = client.get("/course-offerings/teacher") {
            url {
                parameters.append("teacherId", id.toString())
            }
        }
        return courseOfferings.body()
    }

    suspend fun search(subjectId: Long,teacherId: Long,semester: String): List<CourseOfferingsDto>{
        val courseOfferings = client.get("/course-offerings/search"){
            url{
                parameters.append("subjectId", subjectId.toString())
                parameters.append("teacherId", teacherId.toString())
                parameters.append("semester", semester)
            }
        }
        return courseOfferings.body()
    }

    suspend fun createCourseOffering(courseOfferingDto: CourseOfferingsInputDto): CourseOfferingsDto{
        return client.post("/course-offering"){
            setBody(courseOfferingDto)
        }.body()
    }

}