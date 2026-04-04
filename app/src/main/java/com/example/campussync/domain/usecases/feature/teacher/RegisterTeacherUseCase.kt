package com.example.campussync.domain.usecases.feature.teacher

import com.example.campussync.data.remote.dto.teacher.TeacherDto
import com.example.campussync.data.remote.dto.teacher.TeacherInputDto
import com.example.campussync.data.remote.repository.TeacherRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class RegisterTeacherUseCase(
    private val repository: TeacherRepo
) : BaseUseCase<TeacherDto, RegisterTeacherUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): TeacherDto {
        return repository.registerTeacher(params.teacherDto)
    }

    class Params private constructor(val teacherDto: TeacherInputDto){
        companion object{
            fun forRegisterStudent(teacherDto: TeacherInputDto) = Params(teacherDto)
        }
    }
}