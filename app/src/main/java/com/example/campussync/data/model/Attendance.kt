package com.example.campussync.data.model

import com.example.campussync.data.model.student.StudentLoginResponse
import java.time.LocalDate

data class Attendance(
    val id: Long,
    val student: StudentLoginResponse,
    val subject: Subject,
    val date: String,
    val status: String,
)
