package com.example.campussync.api

import com.example.campussync.data.model.Department
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DepartmentApiService {
    @GET("department/all")
    suspend fun getDepartments(): Response<List<Department>>

    @GET("department")
    suspend fun getDepartmentById(
        @Query("departmentId") departmentId: Long
    ): Response<Department>

}