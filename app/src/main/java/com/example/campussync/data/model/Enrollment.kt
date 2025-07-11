package com.example.campussync.data.model

import com.example.campussync.data.model.student.StudentLoginResponse

data class Enrollment(
    val id: Long,
    val student: StudentLoginResponse,
    val subject: Subject
)
