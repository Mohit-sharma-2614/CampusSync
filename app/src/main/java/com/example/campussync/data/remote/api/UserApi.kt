package com.example.campussync.data.remote.api

import com.example.campussync.data.remote.dto.auth.AuthDto
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenResponseDto
import com.example.campussync.data.remote.dto.student.StudentDto
import com.example.campussync.data.remote.dto.teacher.TeacherDto
import com.example.campussync.data.remote.dto.user.UserDto
import com.example.campussync.data.remote.dto.user.UserLoginDto
import com.example.campussync.domain.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class UserApi(
    private val publicClient: HttpClient,
    private val authClient: HttpClient
) {

    private suspend fun ensureSuccess(response: HttpResponse) {
        if (!response.status.isSuccess()) {
            val errorBody = try { response.body<String>() } catch (e: Exception) { null }
            val errorCode = response.status.value
            throw Exception("API Error ($errorCode): ${errorBody ?: response.status.description}")
        }
    }

    suspend fun getUserById(id: Long): UserDto {
        val response = publicClient.get("/user") {
            url { parameters.append("id", id.toString()) }
        }
        ensureSuccess(response)
        return response.body()
    }

    suspend fun loginStudent(loginDto: UserLoginDto): StudentDto {
        val response = publicClient.post("student/login") {
            contentType(ContentType.Application.Json)
            setBody(loginDto)
        }
        ensureSuccess(response)
        return response.body()
    }

    suspend fun loginTeacher(loginDto: UserLoginDto): TeacherDto {
        val response = publicClient.post("teacher/login") {
            contentType(ContentType.Application.Json)
            setBody(loginDto)
        }
        ensureSuccess(response)
        return response.body()
    }

    suspend fun logOut(refreshTokenInputDto: RefreshTokenInputDto): AuthDto {
        val response = authClient.post("/api/auth/logout") {
            contentType(ContentType.Application.Json)
            setBody(refreshTokenInputDto)
        }
        ensureSuccess(response)
        return response.body()
    }

    suspend fun validateToken(): AuthDto {
        val response = authClient.post("/api/auth/validate-token")
        ensureSuccess(response)
        return response.body()
    }

    suspend fun refreshToken(refreshTokenInputDto: RefreshTokenInputDto): RefreshTokenResponseDto {
        val response = authClient.post("/api/auth/refreshtoken") {
            contentType(ContentType.Application.Json)
            setBody(refreshTokenInputDto)
        }
        ensureSuccess(response)
        return response.body()
    }
}

fun StudentDto.toDomain() =
    User.Student(
        id = id,
        name = name,
        email = email,
        jwtToke = jwtToken,
        refreshToken = refreshToken
    )

fun TeacherDto.toDomain() =
    User.Teacher(
        id = id,
        name = name,
        email = email,
        jwtToke = jwtToken,
        refreshToken = refreshToken
    )
