package com.example.campussync.data.remote.api

import com.example.campussync.data.remote.dto.student.StudentDto
import com.example.campussync.data.remote.dto.student.StudentInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class StudentApi (
    private val client: HttpClient
) {
    suspend fun getStudentById(id: Long): StudentDto {
        val student = client.get("/student") {
            url {
                parameters.append("id", id.toString())
            }
        }
        return student.body()
    }

    suspend fun getAllStudents(): List<StudentDto> {
        val students = client.get("/student/all")
        return students.body()
    }

    suspend fun registerStudent(studentDto: StudentInputDto): StudentDto {
        return client.post("/student/register") {
            setBody(studentDto)
        }.body()
    }

    suspend fun updateStudent(id: Long, studentDto: StudentInputDto): StudentDto {
        return client.post("/student") {
            url {
                parameters.append("id", id.toString())
            }
            setBody(studentDto)
        }.body()
    }

    suspend fun deleteStudent(id: Long) {
        client.delete("/student") {
            url {
                parameters.append("id", id.toString())
            }
        }
    }
}
