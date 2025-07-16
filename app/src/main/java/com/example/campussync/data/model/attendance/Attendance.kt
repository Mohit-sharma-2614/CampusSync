package com.example.campussync.data.model.attendance

import com.example.campussync.data.model.Subject
import com.example.campussync.data.model.student.StudentLoginResponse

data class Attendance(
    val id: Long,
    val student: StudentLoginResponse,
    val subject: Subject,
    val date: String,
    val status: String,
)
