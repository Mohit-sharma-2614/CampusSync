package com.example.campussync.data.repository.impl

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.campussync.api.AuthApiService
import com.example.campussync.api.TeacherApiService
import com.example.campussync.data.model.teacher.TeacherLoginRequest
import com.example.campussync.data.model.teacher.TeacherLoginResponse
import com.example.campussync.data.repository.TeacherRepository
import com.example.campussync.utils.Resource
import com.example.campussync.utils.TokenManager
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class TeacherRepositoryImpl @Inject constructor(
    private val teacherApiService: TeacherApiService,
    private val authApiService: AuthApiService,
): TeacherRepository {
        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        override suspend fun loginTeacher(teacher: TeacherLoginRequest): Resource<TeacherLoginResponse> {
            return try {
                val loginResponse: Response<TeacherLoginResponse> = authApiService.loginTeacher(teacher)
                // You might want to add additional checks here based on your API's success criteria
                if (loginResponse.isSuccessful) {
                    val teacherResponse = loginResponse.body()
                    if (teacherResponse != null) {
                        Resource.Success(teacherResponse)
                    } else {
                        Resource.Error("Login successful but no data received")
                    }
                    } else {
                    Resource.Error("Login failed: ${loginResponse.code()}")
                }
            } catch (e: HttpException) {
                // Handle HTTP errors (e.g., 401 Unauthorized, 404 Not Found)
                Resource.Error("Login failed: ${e.message}", e)
            } catch (e: IOException) {
                // Handle network errors (e.g., no internet connection)
                Resource.Error("Network error during login: ${e.message}", e)
            } catch (e: Exception) {
                // Handle other unexpected errors
                Resource.Error("An unexpected error occurred during login: ${e.message}", e)
            }
        }

        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        override suspend fun getTeacherById(teacherId: Long): Resource<TeacherLoginResponse> {
            return try {
                val teacherResponse = teacherApiService.getTeacherById(teacherId)
                if (teacherResponse.isSuccessful) {
                    val teacher = teacherResponse.body()
                    if (teacher != null) {
                        Resource.Success(teacher)
                    } else {
                        Resource.Error("Teacher data is null")
                    }
                } else {
                    Resource.Error("Failed to fetch teacher: ${teacherResponse.code()}")
                }
            } catch (e: HttpException) {
                Resource.Error("Failed to fetch teacher: ${e.message}", e)
            } catch (e: IOException) {
                Resource.Error("Network error fetching teacher: ${e.message}", e)
            } catch (e: Exception) {
                Resource.Error("An unexpected error occurred fetching teacher: ${e.message}", e)
            }
        }

        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        override suspend fun getAllTeachers(): Resource<List<TeacherLoginResponse>> {
            return try {
                val teachersList = teacherApiService.getAllTeachers()
                if (teachersList.isSuccessful) {
                    val teachers = teachersList.body()
                    if (teachers != null) {
                        Resource.Success(teachers)
                    } else {
                        Resource.Error("Teachers list is null")
                    }
                } else {
                    Resource.Error("Failed to fetch all teachers: ${teachersList.code()}")
                }
            } catch (e: HttpException) {
                Resource.Error("Failed to fetch all teachers: ${e.message}", e)
            } catch (e: IOException) {
                Resource.Error("Network error fetching all teachers: ${e.message}", e)
            } catch (e: Exception) {
                Resource.Error("An unexpected error occurred fetching all teachers: ${e.message}", e)
            }
        }

        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        override suspend fun updateTeacher(teacher: TeacherLoginRequest): Resource<TeacherLoginResponse> {
            return try {
                val updatedTeacher = teacherApiService.updateTeacher(teacher)
                if (updatedTeacher.isSuccessful) {
                    val updatedTeacherData = updatedTeacher.body()
                    if (updatedTeacherData != null) {
                        Resource.Success(updatedTeacherData)
                    } else {
                        Resource.Error("Updated teacher data is null")
                    }
                } else {
                    Resource.Error("Failed to update teacher: ${updatedTeacher.code()}")
                }
            } catch (e: HttpException) {
                Resource.Error("Failed to update teacher: ${e.message}", e)
            } catch (e: IOException) {
                Resource.Error("Network error updating teacher: ${e.message}", e)
            } catch (e: Exception) {
                Resource.Error("An unexpected error occurred updating teacher: ${e.message}", e)
            }
        }

        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        override suspend fun deleteTeacher(teacherId: Long): Resource<Unit> {
            return try {
                teacherApiService.deleteTeacher(teacherId) // Assuming this Retrofit call returns Unit on success
                Resource.Success(Unit)
            } catch (e: HttpException) {
                Resource.Error("Failed to delete teacher: ${e.message}", e)
            } catch (e: IOException) {
                Resource.Error("Network error deleting teacher: ${e.message}", e)
            } catch (e: Exception) {
                Resource.Error("An unexpected error occurred deleting teacher: ${e.message}", e)
            }
        }


}