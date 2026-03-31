package com.example.campussync.domain.repository

import com.example.campussync.data.remote.api.StudentApi
import com.example.campussync.data.remote.dto.student.StudentDto
import com.example.campussync.data.remote.dto.student.StudentInputDto
import com.example.campussync.data.remote.repository.StudentRepo

class StudentRepoImpl(
    private val api: StudentApi
) : StudentRepo {
    override suspend fun getStudentById(id: Long): StudentDto {
        return api.getStudentById(id)
    }

    override suspend fun getAllStudents(): List<StudentDto> {
        return api.getAllStudents()
    }

    override suspend fun registerStudent(studentDto: StudentInputDto): StudentDto {
        return api.registerStudent(studentDto)
    }

    override suspend fun updateStudent(
        id: Long,
        studentDto: StudentInputDto
    ): StudentDto {
        return api.updateStudent(id,studentDto)
    }

    override suspend fun deleteStudent(id: Long) {
        api.deleteStudent(id)
    }
}