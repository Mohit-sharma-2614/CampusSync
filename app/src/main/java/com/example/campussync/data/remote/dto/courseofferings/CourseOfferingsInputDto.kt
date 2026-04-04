package com.example.campussync.data.remote.dto.courseofferings

import kotlinx.serialization.Serializable

@Serializable
data class CourseOfferingsInputDto(
    val subjectId: Long,
    val teacherId: Long,
    val academicYear: Int,
    val semester: Int,
    val section: String
)
