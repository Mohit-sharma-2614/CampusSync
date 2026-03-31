package com.example.campussync.di

import com.example.campussync.data.remote.api.StudentApi
import com.example.campussync.data.remote.api.TeacherApi
import com.example.campussync.data.remote.api.UserApi
import com.example.campussync.data.remote.repository.StudentRepo
import com.example.campussync.data.remote.repository.TeacherRepo
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.repository.StudentRepoImpl
import com.example.campussync.domain.repository.TeacherRepoImpl
import com.example.campussync.domain.repository.UserRepoImpl
import org.koin.dsl.module

val appModule = module {

    single { UserApi(client = get(PUBLIC_CLIENT)) }
    single { StudentApi(client = get(PUBLIC_CLIENT)) }
    single { TeacherApi(client = get(PUBLIC_CLIENT)) }


    single<UserRepo> { UserRepoImpl(get()) }
    single<StudentRepo> { StudentRepoImpl(get()) }
    single<TeacherRepo> { TeacherRepoImpl(get()) }

}

