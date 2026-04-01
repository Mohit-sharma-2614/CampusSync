package com.example.campussync.domain.usecases.feature.user

import android.util.Patterns
import com.example.campussync.data.manager.TokenManager
import com.example.campussync.data.manager.UserCredentialManager
import com.example.campussync.data.remote.dto.user.UserLoginDto
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.model.User
import com.example.campussync.domain.usecases.base.BaseUseCase

class LoginUseCase(
    private val repo: UserRepo,
    private val tokenManager: TokenManager,
    private val userCredentialManager: UserCredentialManager
) : BaseUseCase<User, LoginUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): User {
        return when (params.loginType) {
            LoginType.TEACHER -> {
                val user = repo.loginTeacher(params.loginDto)
                val teacher = user as User.Teacher
                userCredentialManager.saveUserId(teacher.id.toString())
                tokenManager.saveToken(teacher.jwtToke)
                tokenManager.saveRefreshToken(teacher.refreshToken)
                user
            }
            LoginType.STUDENT -> {
                val user = repo.loginStudent(params.loginDto)
                val student = user as User.Student
                userCredentialManager.saveUserId(student.id.toString())
                tokenManager.saveToken(student.jwtToke)
                tokenManager.saveRefreshToken(student.refreshToken)
                user
            }
        }
    }

    data class Params private constructor(
        val loginDto: UserLoginDto,
        val loginType: LoginType
    ) {

        init {
            require(loginDto.email.isNotBlank()) {
                "Email cannot be blank"
            }
            require(loginDto.password.length >= 6) {
                "Password too short"
            }
        }

        companion object {

            fun student(dto: UserLoginDto) =
                Params(dto, LoginType.STUDENT)

            fun teacher(dto: UserLoginDto) =
                Params(dto, LoginType.TEACHER)

        }
    }

    enum class LoginType {
        STUDENT,
        TEACHER
    }

}

fun String.isCorrectEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
