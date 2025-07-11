package com.example.campussync.data.repository.impl

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.campussync.api.AttendanceApiService
import com.example.campussync.data.model.Attendance
import com.example.campussync.data.repository.AttendanceRepository
import com.example.campussync.utils.Resource
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceApiService: AttendanceApiService
): AttendanceRepository {
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

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun createAttendance(attendance: Attendance): Resource<Attendance> {
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