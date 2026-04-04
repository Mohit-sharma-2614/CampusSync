package com.example.campussync.data.remote.api

sealed class ApiError {
    object InvalidCredentials : ApiError()
    object UserNotFound : ApiError()
    object Network : ApiError()
    object Unauthorized : ApiError()
    object Forbidden : ApiError()
    object BadRequest : ApiError()
    object NotFound : ApiError()
    object Conflict : ApiError()
    object InternalServerError : ApiError()
    object ServiceUnavailable : ApiError()
    object GatewayTimeout : ApiError()
    object Unknown : ApiError()
}


fun mapError(status: Int): ApiError {
    return when (status) {
        400 -> ApiError.BadRequest
        401 -> ApiError.Unauthorized
        403 -> ApiError.Forbidden
        404 -> ApiError.NotFound
        409 -> ApiError.Conflict
        500 -> ApiError.InternalServerError
        503 -> ApiError.ServiceUnavailable
        504 -> ApiError.GatewayTimeout
        else -> ApiError.Unknown
    }
}

fun ApiError.toMessage(): String {
    return when (this) {
        ApiError.InvalidCredentials -> "Invalid credentials"
        ApiError.UserNotFound -> "User not found"
        ApiError.Network -> "Network error"
        ApiError.Unauthorized -> "Unauthorized"
        ApiError.Forbidden -> "Forbidden"
        ApiError.BadRequest -> "Bad request"
        ApiError.NotFound -> "Not found"
        ApiError.Conflict -> "Conflict"
        ApiError.InternalServerError -> "Internal server error"
        ApiError.ServiceUnavailable -> "Service unavailable"
        ApiError.GatewayTimeout -> "Gateway timeout"
        ApiError.Unknown -> "Unknown error"
    }
}

