package com.example.campussync.data.repository.impl

import com.example.campussync.api.DepartmentApiService
import com.example.campussync.data.model.Department
import com.example.campussync.data.repository.DepartmentRepository
import com.example.campussync.utils.Resource
import okio.IOException
import javax.inject.Inject
import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension

class DepartmentRepositoryImpl @Inject constructor(
    private val departmentApiService: DepartmentApiService
) : DepartmentRepository {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getDepartments(): Resource<List<Department>> {
        val response = departmentApiService.getDepartments()
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
    override suspend fun getDepartmentById(departmentId: Long): Resource<Department> {
        val response = departmentApiService.getDepartmentById(departmentId)
        return try {
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.Success(it)
                } ?: Resource.Error("An unknown error occured")
            } else {
                Resource.Error("An unknown error occured")
            }
        } catch (e: HttpException) {
            Resource.Error("Failed to fetch department: ${e.message}", e)
        } catch (e: IOException) {
            Resource.Error("Network error fetching department: ${e.message}", e)
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred fetching department: ${e.message}", e)
        }
    }

}