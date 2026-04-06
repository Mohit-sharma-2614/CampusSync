package com.example.campussync.domain.usecases.feature.subjects

import com.example.campussync.data.remote.repository.CourseOfferingsRepo
import com.example.campussync.data.remote.repository.EnrollmentRepo
import com.example.campussync.data.remote.repository.StudentRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

// Extracts list of students enrolled in a subject by subject id, semester, and teacher id.
class GetStudentsListBySubjectTeacherIdAndSemesterUseCase(
    private val courseOfferingsRepo: CourseOfferingsRepo,
    private val enrollmentRepo: EnrollmentRepo,
    private val studentRepo: StudentRepo
): BaseUseCase<List<GetStudentsListBySubjectTeacherIdAndSemesterUseCase.Student>, GetStudentsListBySubjectTeacherIdAndSemesterUseCase.Params>() {


    override suspend fun buildUseCase(params: Params): List<Student> {
        val courseOfferings = courseOfferingsRepo.search(
            subjectId = params.subjectId,
            teacherId = params.teacherId,
            semester = params.semester
        )
        val enrollments = courseOfferings.flatMap { courseOffering ->
            enrollmentRepo.getEnrollmentByCourseOfferingId(courseOffering.id)
        }

        val students = enrollments.map { enrollment ->
            studentRepo.getStudentById(enrollment.studentId)
        }
        return students.map { student ->
            Student(
                id = student.id,
                email = student.email,
                name = student.name,
                rollNumber = student.rollNumber
            )
        }
    }

    data class Student(
        val id: Long,
        val email: String,
        val name: String,
        val rollNumber: String
    )

    class Params private constructor(val subjectId: Long, val semester: String, val teacherId: Long){
        companion object{
            fun forGetTotalStudentsBySubjectId(subjectId: Long, semester: String, teacherId: Long) = Params(subjectId, semester, teacherId)
        }
    }
}