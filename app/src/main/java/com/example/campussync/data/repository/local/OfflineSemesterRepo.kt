package com.example.campussync.data.repository.local

import com.example.campussync.data.database.dao.SemesterDao
import com.example.campussync.data.database.entitiy.SemesterEntity
import com.example.campussync.data.models.SemesterWithCourses
import com.example.campussync.data.repository.SemesterRepo

class OfflineSemesterRepo(
    private val semesterDao: SemesterDao
): SemesterRepo {
    override suspend fun insertSemester(semester: SemesterEntity) = semesterDao.insertSemester(semester)

    override suspend fun updateSemester(semester: SemesterEntity) = semesterDao.updateSemester(semester)

    override suspend fun getSemestersWithCourses(studentId: String): List<SemesterWithCourses> = semesterDao.getSemestersWithCourses(studentId)
}