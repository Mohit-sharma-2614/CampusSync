package com.example.campussync.data.model.attendanceToken

import com.example.campussync.data.model.Subject
import com.example.campussync.data.model.teacher.TeacherLoginResponse
import java.util.UUID

data class AttendanceToken(
    val token: UUID,
    val subject: Subject,
    val teacher: TeacherLoginResponse,
    val generatedAt: String,
    val expiresAt: String
)
