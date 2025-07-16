package com.example.campussync.di

import android.content.Context
import com.example.campussync.data.repository.AttendanceRepository
import com.example.campussync.data.repository.AttendanceTokenRepository
import com.example.campussync.data.repository.AuthRepository
import com.example.campussync.data.repository.DepartmentRepository
import com.example.campussync.data.repository.EnrollmentRepository
import com.example.campussync.data.repository.StudentRepository
import com.example.campussync.data.repository.SubjectRepository
import com.example.campussync.data.repository.TeacherRepository
import com.example.campussync.data.repository.impl.AttendanceRepositoryImpl
import com.example.campussync.data.repository.impl.AttendanceTokenRepositoryImpl
import com.example.campussync.data.repository.impl.AuthRepositoryImpl
import com.example.campussync.data.repository.impl.DepartmentRepositoryImpl
import com.example.campussync.data.repository.impl.EnrollmentRepositoryImpl
import com.example.campussync.data.repository.impl.StudentRepositoryImpl
import com.example.campussync.data.repository.impl.SubjectRepositoryImpl
import com.example.campussync.data.repository.impl.TeacherRepositoryImpl
import com.example.campussync.utils.ConnectivityObserver
import com.example.campussync.utils.UserPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {



    @Binds
    @Singleton
    abstract fun bindTeacherRepository(
        impl: TeacherRepositoryImpl
    ): TeacherRepository

    @Binds
    @Singleton
    abstract fun bindStudentRepository(
        impl: StudentRepositoryImpl
    ): StudentRepository

    @Binds
    @Singleton
    abstract fun bindsDepartmentRepository(
        impl: DepartmentRepositoryImpl
    ): DepartmentRepository

    @Binds
    @Singleton
    abstract fun bindsSubjectRepository(
        impl: SubjectRepositoryImpl
    ): SubjectRepository

    @Binds
    @Singleton
    abstract fun bindsEnrollmentRepository(
        impl: EnrollmentRepositoryImpl
    ): EnrollmentRepository

    @Binds
    @Singleton
    abstract fun bindsAttendanceRepository(
        impl: AttendanceRepositoryImpl
    ): AttendanceRepository

    @Binds
    @Singleton
    abstract fun bindsAttendanceTokenRepository(
        impl: AttendanceTokenRepositoryImpl
    ): AttendanceTokenRepository

    @Binds
    @Singleton
    abstract fun bindsAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository


}