package com.example.campussync.domain.usecases.feature.subjects

import com.example.campussync.data.remote.dto.courseofferings.CourseOfferingsDto
import com.example.campussync.data.remote.dto.subject.SubjectDto
import com.example.campussync.data.remote.repository.CourseOfferingsRepo
import com.example.campussync.data.remote.repository.EnrollmentRepo
import com.example.campussync.data.remote.repository.SubjectRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class GetSubjectsByStudentIdUseCase(
    private val courseOfferingsRepo: CourseOfferingsRepo,
    private val subjectRepo: SubjectRepo,
    private val enrollmentRepo: EnrollmentRepo,
): BaseUseCase<List<Subject>, GetSubjectsByStudentIdUseCase.Params>() {


    override suspend fun buildUseCase(params: Params): List<Subject> {
        val enrollments = enrollmentRepo.getEnrollmentByStudentId(params.studentId)
        val courseOfferings: List<CourseOfferingsDto> = enrollments.map { enrollment ->
            courseOfferingsRepo.getCourseOfferingById(enrollment.courseOfferingId)
        }
        val subjects: List<SubjectDto> = courseOfferings.map { courseOffering ->
            subjectRepo.getSubjectById(courseOffering.subjectId)
        }
        val subject: List<Subject> = subjects.map { subject ->
            Subject(
                id = subject.id,
                name = subject.name,
                code = subject.code,
                credits = subject.credits
            )
        }
        return subject
    }

    class Params private constructor(val studentId: Long) {
        companion object {
            fun forGetSubjectsByStudentId(studentId: Long) = Params(studentId)
        }
    }

}

data class Subject(
    val id: Long,
    val name: String,
    val code: String,
    val credits: Int
)
