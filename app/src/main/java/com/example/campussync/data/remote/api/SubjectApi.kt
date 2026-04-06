package com.example.campussync.data.remote.api

import com.example.campussync.data.remote.dto.subject.SubjectDto
import com.example.campussync.data.remote.dto.subject.SubjectInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class SubjectApi(
    private val client: HttpClient
) {
    suspend fun getAllSubjects(): List<SubjectDto> {
        val subjects = client.get("/subject/all")
        return subjects.body()
    }
    suspend fun getSubjectById(id: Long): SubjectDto {
        val subject = client.get("/subject"){
            url{
                parameters.append("id", id.toString())
            }
        }
        return subject.body()
    }
    suspend fun createSubject(subjectInputDto: SubjectInputDto): SubjectDto{
        return client.post("/subject"){
            setBody(subjectInputDto)
        }.body()
    }
}