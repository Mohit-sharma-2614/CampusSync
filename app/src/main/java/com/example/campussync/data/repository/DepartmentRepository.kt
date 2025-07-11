package com.example.campussync.data.repository

import com.example.campussync.data.model.Department
import com.example.campussync.utils.Resource
import retrofit2.Response

interface DepartmentRepository {
    suspend fun getDepartments(): Resource<List<Department>>
    suspend fun getDepartmentById(departmentId: Long): Resource<Department>
//    suspend fun createDepartment(department: Department): Resource<Department>
//    suspend fun updateDepartment(department: Department): Resource<Department>
//    suspend fun deleteDepartment(departmentId: Long): Resource<Unit>
}