package com.example.campussync.data.remote.repository

import com.example.campussync.data.remote.dto.subject.SubjectDto
import com.example.campussync.data.remote.dto.subject.SubjectInputDto

interface SubjectRepo {
    suspend fun getAllSubjects(): List<SubjectDto>
    suspend fun getSubjectById(id: Long): SubjectDto
    suspend fun createSubject(subjectDto: SubjectInputDto): SubjectDto
}

