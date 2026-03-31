package com.example.campussync.domain.usecases.feature.teacher

import com.example.campussync.data.remote.dto.teacher.TeacherDto
import com.example.campussync.data.remote.repository.TeacherRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class GetTeacherByIdUseCase(
    private val repository: TeacherRepo
): BaseUseCase<TeacherDto, GetTeacherByIdUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): TeacherDto {
        return repository.getTeacherById(params.id)
    }

    class Params private constructor(val id: Long) {
        companion object {
            fun forGetTeacherById(id: Long) = Params(id)
        }
    }
}