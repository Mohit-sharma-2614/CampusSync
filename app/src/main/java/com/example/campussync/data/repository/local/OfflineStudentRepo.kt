package com.example.campussync.data.repository.local

import com.example.campussync.data.database.dao.StudentDao
import com.example.campussync.data.database.entitiy.StudentsEntity
import com.example.campussync.data.models.StudentWithSemesters
import com.example.campussync.data.repository.StudentRepo

class OfflineStudentRepo(
    private val studentDao: StudentDao
): StudentRepo {
    override suspend fun insertStudent(student: StudentsEntity) = studentDao.insertStudent(student)

    override suspend fun getStudentsWithSemesters(): List<StudentWithSemesters> = studentDao.getStudentsWithSemesters()

    override suspend fun getStudentWithSemesters(studentId: String): StudentWithSemesters? = studentDao.getStudentWithSemesters(studentId)

}