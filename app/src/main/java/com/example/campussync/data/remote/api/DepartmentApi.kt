package com.example.campussync.data.remote.api

import com.example.campussync.data.remote.dto.department.DepartmentDto
import com.example.campussync.data.remote.dto.department.DepartmentInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class DepartmentApi(
    private val client: HttpClient
) {
    suspend fun getAllDepartments(): List<DepartmentDto> {
        val departments = client.get("/department/all")
        return departments.body()
    }

    suspend fun getDepartmentById(id: Long): DepartmentDto {
        val department = client.get("/department") {
            url {
                parameters.append("id", id.toString())
            }
        }
        return department.body()
    }

    suspend fun createDepartment(departmentDto: DepartmentInputDto): DepartmentDto{
        return client.post("/department"){
            setBody(departmentDto)
        }.body()
    }
}
