package com.example.campussync.di


import com.example.campussync.data.repository.*
import com.example.campussync.data.repository.impl.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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



//    @Binds
//    @Singleton
//    abstract fun provideNoticeRepository(impl: NoticeRepositoryImpl): NoticeRepository

}