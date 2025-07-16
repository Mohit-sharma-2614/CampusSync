package com.example.campussync.data.repository.impl

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.campussync.api.AttendanceApiService
import com.example.campussync.data.model.attendance.Attendance
import com.example.campussync.data.model.attendance.AttendanceReq
import com.example.campussync.data.repository.AttendanceRepository
import com.example.campussync.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.io.IOException
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceApiService: AttendanceApiService
): AttendanceRepository {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun getAttendanceBySubjectAndDate(subjectId: Long, date: String): Flow<Resource<List<Attendance>>> =
        flow {
            // Make the API call and emit raw data or throw an exception
            val response = attendanceApiService.getAttendanceBySubjectAndDate(subjectId, date)
            if (response.isSuccessful) {
                response.body()?.let { emit(it) }
                    ?: throw NoSuchElementException("No attendance records found (empty body)")
            } else {
                throw retrofit2.HttpException(response) // Throw Retrofit's HttpException
            }
        }
            .map<List<Attendance>, Resource<List<Attendance>>> { data ->
                // Map successful raw data to Resource.Success
                Resource.Success(data)
            }
            .onStart {
                // Emit Resource.Loading before the flow starts collecting from upstream
                emit(Resource.Loading)
            }
            .catch { cause ->
                // Catch any exceptions from the flow or map, and emit Resource.Error
                val errorMessage = when (cause) {
                    is HttpException -> "Server error: ${cause.message} "
                    is IOException -> "Network connection error. Please check your internet."
                    is NoSuchElementException -> cause.message ?: "Data not found"
                    else -> "An unexpected error occurred: ${cause.message ?: "Unknown error"}"
                }
                emit(Resource.Error(errorMessage, cause))
            }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun createBulkAttendance(attendanceList: List<AttendanceReq>): Resource<List<Attendance>> {
        val response = attendanceApiService.createBulkAttendance(attendanceList)
        return try {
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Failed to create attendance records")
            } else {
                Resource.Error("Failed to create attendance records: ${response.message()}")
            }
        } catch (e: HttpException) {
            Resource.Error("HTTP error: ${e.message}", e)
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.message}", e)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getAllAttendance(): Resource<List<Attendance>> {
        val response = attendanceApiService.getAllAttendance()
        return try {
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.Success(it)
                } ?: Resource.Error("An unknown error occured")
            } else {
                Resource.Error("An unknown error occured")
            }
        } catch (e: HttpException) {
            // Handle HTTP errors (e.g., 401 Unauthorized, 404 Not Found)
            Resource.Error("Login failed: ${e.message}", e)
        } catch (e: java.io.IOException) {
            // Handle network errors (e.g., no internet connection)
            Resource.Error("Network error during login: ${e.message}", e)
        } catch (e: Exception) {
            // Handle other unexpected errors
            Resource.Error("An unexpected error occurred during login: ${e.message}", e)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getAttendanceById(attendanceId: Long): Resource<Attendance> {
        val response = attendanceApiService.getAttendanceById(attendanceId)
        return try {
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.Success(it)
                } ?: Resource.Error("An unknown error occured")
            } else {
                Resource.Error("An unknown error occured")
            }
        } catch (e: HttpException) {
            // Handle HTTP errors (e.g., 401 Unauthorized, 404 Not Found)
            Resource.Error("Login failed: ${e.message}", e)
        } catch (e: java.io.IOException) {
            // Handle network errors (e.g., no internet connection)
            Resource.Error("Network error during login: ${e.message}", e)
        } catch (e: Exception) {
            // Handle other unexpected errors
            Resource.Error("An unexpected error occurred during login: ${e.message}", e)
        }
    }

    // Fetch by Subject ID
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getAttendanceBySubjectId(subjectId: Long): Resource<List<Attendance>> {
        return try {
            val response = attendanceApiService.getAttendanceBySubjectId(subjectId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("An unknown error occurred")
            } else {
                Resource.Error("Failed with status code ${response.code()}")
            }
        } catch (e: HttpException) {
            Resource.Error("HTTP error: ${e.message}", e)
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.message}", e)
        }
    }

    // Fetch by Student ID
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getAttendanceByStudentId(studentId: Long): Resource<List<Attendance>> {
        return try {
            val response = attendanceApiService.getAttendanceByStudentId(studentId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("An unknown error occurred")
            } else {
                Resource.Error("Failed with status code ${response.code()}")
            }
        } catch (e: HttpException) {
            Resource.Error("HTTP error: ${e.message}", e)
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.message}", e)
        }
    }

    // Fetch by Subject + Student
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getAttendanceBySubjectAndStudentId(subjectId: Long, studentId: Long): Resource<List<Attendance>> {
        return try {
            val response = attendanceApiService.getAttendanceBySubjectAndStudentId(subjectId, studentId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("An unknown error occurred")
            } else {
                Resource.Error("Failed with status code ${response.code()}")
            }
        } catch (e: HttpException) {
            Resource.Error("HTTP error: ${e.message}", e)
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.message}", e)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun createAttendance(attendance: AttendanceReq): Resource<Attendance> {
        val response = attendanceApiService.createAttendance(attendance)
        return try {
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.Success(it)
                } ?: Resource.Error("An unknown error occured")
            } else {
                Resource.Error("An unknown error occured")
            }
        } catch (e: HttpException) {
            // Handle HTTP errors (e.g., 401 Unauthorized, 404 Not Found)
            Resource.Error("Login failed: ${e.message}", e)
        } catch (e: java.io.IOException) {
            // Handle network errors (e.g., no internet connection)
            Resource.Error("Network error during login: ${e.message}", e)
        } catch (e: Exception) {
            // Handle other unexpected errors
            Resource.Error("An unexpected error occurred during login: ${e.message}", e)
        }
    }

}