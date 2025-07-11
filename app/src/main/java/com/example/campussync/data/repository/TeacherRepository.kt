package com.example.campussync.data.repository

import com.example.campussync.data.model.teacher.TeacherLoginRequest
import com.example.campussync.data.model.teacher.TeacherLoginResponse
import com.example.campussync.utils.Resource

interface TeacherRepository {
    suspend fun loginTeacher(teacher: TeacherLoginRequest): Resource<TeacherLoginResponse>
    suspend fun getTeacherById(teacherId: Long): Resource<TeacherLoginResponse>
    suspend fun getAllTeachers(): Resource<List<TeacherLoginResponse>>
    suspend fun updateTeacher(teacher: TeacherLoginRequest): Resource<TeacherLoginResponse>
    suspend fun deleteTeacher(teacherId: Long): Resource<Unit> // Returns Response<Unit> for success/failure
}