package com.example.campussync.domain.usecases.feature.user

import android.util.Log
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
        Log.d("LoginUseCase", "buildUseCase: called")
        return when (params.loginType) {
            LoginType.TEACHER -> {
                Log.d("LoginUseCase", "buildUseCase: teacher login attempt")
                val user = repo.loginTeacher(params.loginDto)
                val teacher = user as User.Teacher
                userCredentialManager.saveUserId(teacher.id.toString())
                tokenManager.saveToken(teacher.jwtToke)
                tokenManager.saveRefreshToken(teacher.refreshToken)
                user
            }
            LoginType.STUDENT -> {
                Log.d("LoginUseCase", "buildUseCase: student login attempt")
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
fun String.isCorrectPassword(): Boolean{
    if (length < 8) return false

    val hasUpperCase = any { it.isUpperCase() }
    val hasLowerCase = any { it.isLowerCase() }
    val hasDigit = any { it.isDigit() }
    val hasSpecialChar = any { !it.isLetterOrDigit() }

    return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
}
