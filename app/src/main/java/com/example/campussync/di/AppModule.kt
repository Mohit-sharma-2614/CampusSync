package com.example.campussync.di

import com.example.campussync.data.manager.StorageManager
import com.example.campussync.data.manager.TokenManager
import com.example.campussync.data.manager.UserCredentialManager
import com.example.campussync.data.observer.NetworkObserver
import com.example.campussync.data.remote.api.StudentApi
import com.example.campussync.data.remote.api.TeacherApi
import com.example.campussync.data.remote.api.UserApi
import com.example.campussync.data.remote.repository.StudentRepo
import com.example.campussync.data.remote.repository.TeacherRepo
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.manager.SessionManager
import com.example.campussync.domain.manager.StorageManagerImpl
import com.example.campussync.domain.manager.TokenManagerImpl
import com.example.campussync.domain.manager.UserCredentialManagerImpl
import com.example.campussync.domain.observer.NetworkObserverImpl
import com.example.campussync.domain.repository.StudentRepoImpl
import com.example.campussync.domain.repository.TeacherRepoImpl
import com.example.campussync.domain.repository.UserRepoImpl
import com.example.campussync.persentation.auth.AuthViewModel
import com.example.campussync.persentation.base.AbsViewModel
import org.koin.dsl.module

val managerModule = module {
    single<StorageManager> { StorageManagerImpl(get()) }
    single<TokenManager> { TokenManagerImpl(get()) }
    single<NetworkObserver> { NetworkObserverImpl(get()) }
    single { SessionManager(get(), get(), get(), get(), get(),get()) }
    single<UserCredentialManager> { UserCredentialManagerImpl(get()) }
}

val appModule = module {
    single { AbsViewModel.AbsDependencies(get(), get()) }
    single { AuthViewModel(dependencies = get(), loginUseCase = get()) }

}


val apiModule = module {
    single { UserApi(publicClient = get(PUBLIC_CLIENT), authClient = get(AUTH_CLIENT)) }
    single { StudentApi(client = get(PUBLIC_CLIENT)) }
    single { TeacherApi(client = get(PUBLIC_CLIENT)) }
}


val repoModule = module {

    single<UserRepo> { UserRepoImpl(get()) }
    single<StudentRepo> { StudentRepoImpl(get()) }
    single<TeacherRepo> { TeacherRepoImpl(get()) }

}

