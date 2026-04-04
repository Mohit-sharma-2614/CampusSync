package com.example.campussync.data.remote.api

import com.example.campussync.data.remote.dto.teacher.TeacherDto
import com.example.campussync.data.remote.dto.teacher.TeacherInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.parameters

class TeacherApi(
    private val client: HttpClient
) {
    suspend fun getTeacherById(id: Long): TeacherDto {
        val teacher = client.get("/teacher") {
            url {
                parameters.append("id", id.toString())
            }
        }
        return teacher.body()
    }

    suspend fun getAllTeachers(): List<TeacherDto> {
        val teachers = client.get("/teacher/all")
        return teachers.body()
    }

    suspend fun registerTeacher(teacherDto: TeacherInputDto): TeacherDto {
        return client.post("/teacher/register") {
            setBody(teacherDto)
        }.body()
    }

    suspend fun updateTeacher(id: Long, teacherDto: TeacherInputDto): TeacherDto {
        return client.post("/teacher") {
            url {
                parameters.append("id", id.toString())
            }
            setBody(teacherDto)
        }.body()
    }

    suspend fun deleteTeacher(id: Long) {
        client.delete("/teacher") {
            url {
                parameters.append("id", id.toString())
            }
        }
    }

}