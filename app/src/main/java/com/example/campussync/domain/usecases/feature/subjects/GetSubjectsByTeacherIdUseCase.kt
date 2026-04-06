package com.example.campussync.domain.usecases.feature.subjects

import com.example.campussync.data.remote.repository.CourseOfferingsRepo
import com.example.campussync.data.remote.repository.SubjectRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class GetSubjectsByTeacherIdUseCase(
    private val courseOfferingsRepo: CourseOfferingsRepo,
    private val subjectRepo: SubjectRepo,
): BaseUseCase<List<GetSubjectsByTeacherIdUseCase.Subject>, GetSubjectsByTeacherIdUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): List<Subject> {
        val courseOfferings = courseOfferingsRepo.getCourseOfferingsByTeacherId(params.teacherId.toLong())
        val subjects = courseOfferings.map { courseOffering ->
            subjectRepo.getSubjectById(courseOffering.subjectId)
        }

        return subjects.map { subject ->
            Subject(
                id = subject.id,
                name = subject.name,
                code = subject.code,
                credits = subject.credits
            )
        }
    }

    data class Subject(
        val id: Long,
        val name: String,
        val code: String,
        val credits: Int,
    )
    class Params private constructor(val teacherId: String){
        companion object{
            fun forGetSubjectsByTeacherId(teacherId: String) = Params(teacherId)
        }
    }
}