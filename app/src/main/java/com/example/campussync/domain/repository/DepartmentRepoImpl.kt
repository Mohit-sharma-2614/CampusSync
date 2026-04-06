package com.example.campussync.domain.repository

import com.example.campussync.data.remote.api.DepartmentApi
import com.example.campussync.data.remote.dto.department.DepartmentDto
import com.example.campussync.data.remote.dto.department.DepartmentInputDto
import com.example.campussync.data.remote.repository.DepartmentRepo

class DepartmentRepoImpl(
    private val api: DepartmentApi
) : DepartmentRepo{
    override suspend fun getAllDepartments(): List<DepartmentDto> {
        return api.getAllDepartments()
    }

    override suspend fun getDepartmentById(id: Long): DepartmentDto {
        return api.getDepartmentById(id)
    }

    override suspend fun createDepartment(departmentDto: DepartmentInputDto): DepartmentDto {
        return api.createDepartment(departmentDto)
    }
}