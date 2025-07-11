package com.example.campussync.data.model

import java.sql.Timestamp
import java.util.UUID

data class AttendanceToken(
    val token: UUID,
    val subject: Subject,
    val generatedAt: Timestamp,
    val expiresAt: Timestamp
)
