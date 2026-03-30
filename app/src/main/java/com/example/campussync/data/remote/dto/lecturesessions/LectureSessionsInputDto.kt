package com.example.campussync.data.remote.dto.lecturesessions

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.sql.Date
import java.sql.Timestamp

@Serializable
data class LectureSessionsInputDto(
    val courseOfferingId: Long,
    @Contextual
    val sessionDate: Date,
    val startTime: Timestamp,
    val endTime: Timestamp,
    val room: String,
    val topic: String
)
