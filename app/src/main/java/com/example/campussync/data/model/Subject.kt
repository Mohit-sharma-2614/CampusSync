package com.example.campussync.data.model

import com.example.campussync.data.model.teacher.TeacherLoginResponse

data class Subject(
    val id: Long,
    val name: String,
    val code: String,
    val semester: Int,
    val department: Department,
    val teacher: TeacherLoginResponse
)
