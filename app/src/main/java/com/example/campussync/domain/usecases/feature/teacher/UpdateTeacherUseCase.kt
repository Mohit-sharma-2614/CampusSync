package com.example.campussync.domain.usecases.feature.teacher

import com.example.campussync.data.remote.dto.teacher.TeacherDto
import com.example.campussync.data.remote.dto.teacher.TeacherInputDto
import com.example.campussync.data.remote.repository.TeacherRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class UpdateTeacherUseCase(
    private val repository : TeacherRepo
): BaseUseCase<TeacherDto, UpdateTeacherUseCase.Params>() {
    override suspend fun buildUseCase(params: Params): TeacherDto {
        return repository.updateTeacher(params.id,params.teacherDto)
    }

    class Params private constructor(val id: Long,val teacherDto: TeacherInputDto){
        companion object {
            fun forUpdateTeacher(id: Long, teacherDto: TeacherInputDto) = Params(id, teacherDto)
        }
    }
}