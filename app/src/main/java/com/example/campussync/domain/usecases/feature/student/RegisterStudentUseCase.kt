package com.example.campussync.domain.usecases.feature.student

import com.example.campussync.data.remote.dto.student.StudentDto
import com.example.campussync.data.remote.dto.student.StudentInputDto
import com.example.campussync.data.remote.repository.StudentRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class RegisterStudentUseCase(
    private val repository: StudentRepo
) : BaseUseCase<StudentDto, RegisterStudentUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): StudentDto {
        return repository.registerStudent(params.studentDto)
    }
    class Params private constructor(val studentDto: StudentInputDto){
        companion object{
            fun forRegisterStudent(studentDto: StudentInputDto) = Params(studentDto)
        }
    }
}