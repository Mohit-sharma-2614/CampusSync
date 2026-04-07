package com.example.campussync.data.remote.api

import android.util.Log
import com.example.campussync.data.remote.dto.auth.AuthDto
import com.example.campussync.data.remote.dto.enums.UserRole
import com.example.campussync.data.remote.dto.enums.UserStatus
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
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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

    @OptIn(ExperimentalTime::class)
    suspend fun getUserById(id: Long): UserDto {
        Log.d("UserApi", "getUserById: $id")
        try {
            val response = authClient.get("/users") {
                url { parameters.append("userId", id.toString()) }
            }
            ensureSuccess(response)
            return response.body()
        } catch (e: Exception){
            Log.e("UserApi", "getUserById: ${e.message}")
            return UserDto(
                id = 0,
                name = "",
                email = "",
                role = UserRole.ADMIN,
                status = UserStatus.INACTIVE,
                createdAt = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
            )
        }
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
