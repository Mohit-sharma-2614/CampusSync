package com.example.campussync.data.repository.impl

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.campussync.api.AuthApiService
import com.example.campussync.api.StudentApiService
import com.example.campussync.data.model.student.StudentLoginRequest
import com.example.campussync.data.model.student.StudentLoginResponse
import com.example.campussync.data.repository.StudentRepository
import com.example.campussync.utils.Resource
import java.io.IOException
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val studentApiService: StudentApiService,
    private val authApiService: AuthApiService,
) : StudentRepository {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun loginStudent(student: StudentLoginRequest): Resource<StudentLoginResponse> {
        val loginResponse = authApiService.loginStudent(student)
        return try {
            if(loginResponse.isSuccessful){
                val studentResponse = loginResponse.body()
                if(studentResponse != null){
                    Resource.Success(studentResponse)
                }else{
                    Resource.Error("Login successful but no data received")
                }
            }else{
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
    override suspend fun getStudentById(studentId: Long): Resource<StudentLoginResponse> {
        val studentResponse = studentApiService.getStudentById(studentId)
        return try {
            if(studentResponse.isSuccessful){
                val student = studentResponse.body()
                if(student != null){
                    Resource.Success(student)
                }else{
                    Resource.Error("Student data is null")
                }
            }else{
                Resource.Error("Failed to fetch student: ${studentResponse.code()}")
            }
        } catch (e: HttpException) {
            Resource.Error("Failed to fetch student: ${e.message}", e)
        } catch (e: IOException) {
            Resource.Error("Network error fetching student: ${e.message}", e)
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred fetching student: ${e.message}", e)
        }

    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getAllStudents(): Resource<List<StudentLoginResponse>> {
        val studentsList = studentApiService.getAllStudents()
        return try {
            if (studentsList.isSuccessful){
                val students = studentsList.body()
                if(students != null){
                    return Resource.Success(students)
                }else{
                    return Resource.Error("Students list is null")
                }
            } else{
                return Resource.Error("Failed to fetch all students: ${studentsList.code()}")
            }
        } catch (e: HttpException) {
            Resource.Error("Failed to fetch all students: ${e.message}", e)
        } catch (e: IOException) {
            Resource.Error("Network error fetching all students: ${e.message}", e)
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred fetching all students: ${e.message}", e)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun updateStudent(student: StudentLoginRequest): Resource<StudentLoginResponse> {
        val updatedStudent = studentApiService.updateStudent(student)
        return try {
            if (updatedStudent.isSuccessful){
                val updatedStudentData = updatedStudent.body()
                if(updatedStudentData != null){
                    return Resource.Success(updatedStudentData)
                }else{
                    return Resource.Error("Updated student data is null")
                }
            }else{
                return Resource.Error("Failed to update student: ${updatedStudent.code()}")
            }
            } catch (e: HttpException) {
                Resource.Error("Failed to update student: ${e.message}", e)
            } catch (e: IOException) {
                Resource.Error("Network error updating student: ${e.message}", e)
            } catch (e: Exception) {
                Resource.Error("An unexpected error occurred updating student: ${e.message}", e)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun deleteStudent(studentId: Long): Resource<Unit> {
        val deleteResponse = studentApiService.deleteStudent(studentId)
        return try {
            if (deleteResponse.isSuccessful){
                Resource.Success(Unit)
            }else{
                Resource.Error("Failed to delete student: ${deleteResponse.code()}")
            }
        } catch (e: HttpException) {
            Resource.Error("Failed to delete student: ${e.message}", e)
        } catch (e: IOException) {
            Resource.Error("Network error deleting student: ${e.message}", e)
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred deleting student: ${e.message}", e)
        }
    }
}