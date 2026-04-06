package com.example.campussync.data.remote.repository

import com.example.campussync.data.remote.dto.department.DepartmentDto
import com.example.campussync.data.remote.dto.department.DepartmentInputDto

interface DepartmentRepo {
    suspend fun getAllDepartments(): List<DepartmentDto>
    suspend fun getDepartmentById(id: Long): DepartmentDto
    suspend fun createDepartment(departmentDto: DepartmentInputDto): DepartmentDto
}