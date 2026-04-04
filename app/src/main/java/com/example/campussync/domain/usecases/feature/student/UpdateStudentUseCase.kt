package com.example.campussync.domain.usecases.feature.student

import com.example.campussync.data.remote.dto.student.StudentDto
import com.example.campussync.data.remote.dto.student.StudentInputDto
import com.example.campussync.data.remote.repository.StudentRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class UpdateStudentUseCase(
    private val repository : StudentRepo
): BaseUseCase<StudentDto, UpdateStudentUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): StudentDto {
        return repository.updateStudent(params.id,params.studentDto)
    }


    class Params private constructor(val id: Long, val studentDto: StudentInputDto){
        companion object{
            fun forUpdateStudent(id: Long, studentDto: StudentInputDto) = Params(id, studentDto)
        }
    }
}