package com.example.campussync.domain.usercase.auth

import com.example.campussync.data.model.UserCredential
import com.example.campussync.data.repository.StudentRepository
import com.example.campussync.data.repository.TeacherRepository
import com.example.campussync.domain.usercase.base.AbsUseCase
import com.example.campussync.domain.usercase.auth.AuthUseCase.Params.AuthAction.*
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val studentRepo: StudentRepository,
    private val teacherRepo: TeacherRepository
): AbsUseCase<UserCredential?, AuthUseCase.Params >() {
    
    override suspend fun execute(params: Params): UserCredential? {
        
        return when(params.action){
            LOGIN -> {
                UserCredential()
            }
            
            SIGNUP -> {
                UserCredential()
            }
            
            LOGOUT -> {
                null
            }
        }
    }
    
    
    class Params private constructor(
        val action: AuthAction,
        val email: String? = null,
        val password: String? = null,
        val isTeacher: Boolean = false,
    ){
        enum class AuthAction {
            LOGIN,
            LOGOUT,
            SIGNUP
        }
        companion object {
            fun loginUser(email: String, password: String, isTeacher: Boolean): Params {
                return Params(LOGIN, email, password, isTeacher)
            }
            fun signUpUser(email: String, password: String, isTeacher: Boolean): Params {
                return Params(SIGNUP, email, password, isTeacher)
            }
            fun logoutUser(): Params {
                return Params(LOGOUT)
            }
        }
    }
}