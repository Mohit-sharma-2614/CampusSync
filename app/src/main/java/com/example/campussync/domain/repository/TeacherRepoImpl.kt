package com.example.campussync.domain.repository

import com.example.campussync.data.remote.api.TeacherApi
import com.example.campussync.data.remote.dto.teacher.TeacherDto
import com.example.campussync.data.remote.dto.teacher.TeacherInputDto
import com.example.campussync.data.remote.repository.TeacherRepo

class TeacherRepoImpl(
    private val api: TeacherApi
) : TeacherRepo {
    override suspend fun getTeacherById(id: Long): TeacherDto {
        return api.getTeacherById(id)
    }

    override suspend fun getAllTeachers(): List<TeacherDto> {
        return api.getAllTeachers()
    }

    override suspend fun registerTeacher(teacherDto: TeacherInputDto): TeacherDto {
        return api.registerTeacher(teacherDto)
    }

    override suspend fun updateTeacher(
        id: Long,
        teacherDto: TeacherInputDto
    ): TeacherDto {
        return api.updateTeacher(id,teacherDto)
    }

    override suspend fun deleteTeacher(id: Long) {
        return api.deleteTeacher(id)
    }
}
