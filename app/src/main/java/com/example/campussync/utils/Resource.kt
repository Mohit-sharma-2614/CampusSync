package com.example.campussync.utils

sealed class Resource<out T> { // Made it generic at the class level
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Resource<Nothing>() // Nothing for non-data case
    object Loading : Resource<Nothing>() // Nothing for non-data case
}

sealed class LoginError {
    object InvalidCredentials : LoginError()
    object NetworkError : LoginError()
    data class ServerError(val message: String) : LoginError()
}