package com.example.campussync.domain.repository

import com.example.campussync.data.remote.api.SubjectApi
import com.example.campussync.data.remote.dto.subject.SubjectDto
import com.example.campussync.data.remote.dto.subject.SubjectInputDto
import com.example.campussync.data.remote.repository.SubjectRepo

class SubjectRepoImpl(
    private val api: SubjectApi
) : SubjectRepo {
    override suspend fun getAllSubjects(): List<SubjectDto> {
        return api.getAllSubjects()
    }

    override suspend fun getSubjectById(id: Long): SubjectDto {
        return api.getSubjectById(id)
    }

    override suspend fun createSubject(subjectDto: SubjectInputDto): SubjectDto {
        return api.createSubject(subjectDto)
    }
}