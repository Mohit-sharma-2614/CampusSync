package com.example.campussync.data.remote.repository

import com.example.campussync.data.remote.dto.teacher.TeacherDto
import com.example.campussync.data.remote.dto.teacher.TeacherInputDto

interface TeacherRepo {
    suspend fun getTeacherById(id: Long): TeacherDto
    suspend fun getAllTeachers(): List<TeacherDto>
    suspend fun registerTeacher(teacherDto: TeacherInputDto): TeacherDto
    suspend fun updateTeacher(id: Long, teacherDto: TeacherInputDto): TeacherDto
    suspend fun deleteTeacher(id: Long)
}