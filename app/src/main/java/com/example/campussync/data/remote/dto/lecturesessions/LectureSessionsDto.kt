package com.example.campussync.data.remote.dto.lecturesessions

import com.example.campussync.utils.SqlDateSerializer
import com.example.campussync.utils.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Date
import java.sql.Timestamp

@Serializable
data class LectureSessionsDto(
    val id: Long,
    val courseOfferingId: Long,
    @Serializable(with = SqlDateSerializer::class)
    val sessionDate: Date,
    @Serializable(with = TimestampSerializer::class)
    val startTime: Timestamp,
    @Serializable(with = TimestampSerializer::class)
    val endTime: Timestamp,
    val room: String,
    val topic: String
)
