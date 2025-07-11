package com.example.campussync.data.repository.impl

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.campussync.api.EnrollmentApiService
import com.example.campussync.data.model.Enrollment
import com.example.campussync.data.repository.EnrollmentRepository
import com.example.campussync.utils.Resource
import retrofit2.Response
import javax.inject.Inject

class EnrollmentRepositoryImpl @Inject constructor(
    private val enrollmentApiService: EnrollmentApiService
): EnrollmentRepository{
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getAllEnrollments(): Resource<List<Enrollment>> {
        val response = enrollmentApiService.getAllEnrollments()
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
            Resource.Error("Failed: ${e.message}", e)
        } catch (e: java.io.IOException) {
            // Handle network errors (e.g., no internet connection)
            Resource.Error("Network error during login: ${e.message}", e)
        } catch (e: Exception) {
            // Handle other unexpected errors
            Resource.Error("An unexpected error occurred during login: ${e.message}", e)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getEnrollmentById(enrollmentId: Long): Resource<Enrollment> {
        val response = enrollmentApiService.getEnrollmentById(enrollmentId)
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