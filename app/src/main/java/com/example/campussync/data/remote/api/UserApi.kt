package com.example.campussync.data.remote.api

import com.example.campussync.data.remote.dto.auth.AuthDto
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenResponseDto
import com.example.campussync.data.remote.dto.student.StudentDto
import com.example.campussync.data.remote.dto.teacher.TeacherDto
import com.example.campussync.data.remote.dto.user.UserLoginDto
import com.example.campussync.domain.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserApi(
    private val publicClient: HttpClient,
    private val authClient: HttpClient
) {

    suspend fun loginStudent(loginDto: UserLoginDto): StudentDto {
        return publicClient.post("student/login") {
            setBody(loginDto)
        }.body()
    }

    suspend fun loginTeacher(loginDto: UserLoginDto): TeacherDto {
        return publicClient.post("teacher/login") {
            contentType(ContentType.Application.Json)
            setBody(loginDto)
        }.body()
    }

    suspend fun logOut(refreshTokenInputDto: RefreshTokenInputDto): AuthDto {
        return authClient.post("/api/auth/logout") {
            contentType(ContentType.Application.Json)
            setBody(refreshTokenInputDto)
        }.body()
    }

    suspend fun validateToken(): AuthDto {
        return authClient.post("/api/auth/validate-token").body()
    }

    suspend fun refreshToken(refreshTokenInputDto: RefreshTokenInputDto): RefreshTokenResponseDto {
        return authClient.post("/api/auth/refreshtoken") {
            contentType(ContentType.Application.Json)
            setBody(refreshTokenInputDto)
        }.body()
    }
}

/**
 * Extension functions to easily wrap the DTOs into the sealed response.
 */
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
