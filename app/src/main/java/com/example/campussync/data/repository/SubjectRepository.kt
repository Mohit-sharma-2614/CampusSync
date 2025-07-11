package com.example.campussync.data.repository

import com.example.campussync.data.model.Subject
import com.example.campussync.utils.Resource

interface SubjectRepository {
    suspend fun getAllSubjects(): Resource<List<Subject>>
    suspend fun getSubjectById(subjectId: Long): Resource<Subject>
/*
    suspend fun createSubject(subject: Subject): Resource<Subject>
    suspend fun updateSubject(subject: Subject): Resource<Subject>
    suspend fun deleteSubject(subjectId: Long): Resource<Unit>
*/
}