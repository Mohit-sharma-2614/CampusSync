package com.example.campussync.domain.usecases.feature.user

import com.example.campussync.data.remote.dto.user.UserLoginDto
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.model.User
import com.example.campussync.domain.usecases.base.BaseUseCase

class LoginUseCase(
    private val repo: UserRepo
) : BaseUseCase<User, LoginUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): User {
        return when (params.loginType) {
            LoginType.TEACHER -> repo.loginTeacher(params.loginDto)
            LoginType.STUDENT -> repo.loginStudent(params.loginDto)
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

fun String.isCorrectEmail(){
    if(!this.contains("@")){
        throw Exception("Invalid email")
    }
}
