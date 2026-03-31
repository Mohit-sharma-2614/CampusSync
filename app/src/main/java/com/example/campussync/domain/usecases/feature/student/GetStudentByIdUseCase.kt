package com.example.campussync.domain.usecases.feature.student

import com.example.campussync.data.remote.dto.student.StudentDto
import com.example.campussync.data.remote.repository.StudentRepo
import com.example.campussync.data.repository.StudentRepository
import com.example.campussync.domain.usecases.base.BaseUseCase

class GetStudentByIdUseCase(
    private val repository: StudentRepo
) : BaseUseCase<StudentDto, GetStudentByIdUseCase.Params>(){

    override suspend fun buildUseCase(params: Params): StudentDto {
        return repository.getStudentById(params.id)
    }


    class Params private constructor(val id: Long) {
        companion object{
            fun forGetStudentById(id: Long) = Params(id)
        }
    }
}