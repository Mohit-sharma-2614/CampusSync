package com.example.campussync.utils

import com.example.campussync.data.remote.dto.student.StudentDto
import com.example.campussync.data.remote.dto.teacher.TeacherDto

sealed class UserAuthResponse {
    data class Student(val data: StudentDto) : UserAuthResponse()
    data class Teacher(val data: TeacherDto) : UserAuthResponse()
    // object Admin : UserAuthResponse()
}