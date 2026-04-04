package com.example.campussync.di

import com.example.campussync.CampusSyncViewModel
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
import com.example.campussync.domain.usecases.feature.network.CheckNetworkUseCase
import com.example.campussync.domain.usecases.feature.student.GetStudentByIdUseCase
import com.example.campussync.domain.usecases.feature.student.RegisterStudentUseCase
import com.example.campussync.domain.usecases.feature.student.UpdateStudentUseCase
import com.example.campussync.domain.usecases.feature.teacher.GetTeacherByIdUseCase
import com.example.campussync.domain.usecases.feature.teacher.RegisterTeacherUseCase
import com.example.campussync.domain.usecases.feature.teacher.UpdateTeacherUseCase
import com.example.campussync.domain.usecases.feature.user.GetUserByIdUseCase
import com.example.campussync.domain.usecases.feature.user.GetUserIdUseCase
import com.example.campussync.domain.usecases.feature.user.LogOutUseCase
import com.example.campussync.domain.usecases.feature.user.LoginUseCase
import com.example.campussync.domain.usecases.feature.user.RefreshTokenUseCase
import com.example.campussync.domain.usecases.feature.user.SaveTokenUseCase
import com.example.campussync.domain.usecases.feature.user.ValidateTokenUseCase
import com.example.campussync.persentation.auth.AuthViewModel
import com.example.campussync.persentation.base.AbsViewModel
import com.example.campussync.persentation.dashboard.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val managerModule = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
    single<StorageManager> { StorageManagerImpl(get()) }
    single<TokenManager> { TokenManagerImpl(get()) }
    single<NetworkObserver> { NetworkObserverImpl(get()) }
    single { SessionManager(get(), get(), get(), get(), get(),get()) }
    single<UserCredentialManager> { UserCredentialManagerImpl(get()) }
}

val appModule = module {
    single { StorageManagerImpl(get()) }
    viewModel { AuthViewModel(dependencies = get(), loginUseCase = get()) }
    single { AbsViewModel.AbsDependencies(get(), get()) }
    viewModel { CampusSyncViewModel(get(),get()) }
    viewModel { DashboardViewModel(get(),get(),get(),get(),get()) }
}

val useCaseModule = module {
    single { CheckNetworkUseCase(get()) }
    single { ValidateTokenUseCase(get()) }
    single { GetUserIdUseCase(get()) }
    single { LoginUseCase(get(),get(),get()) }
    single { GetUserByIdUseCase(get()) }
    single { RefreshTokenUseCase(get()) }
    single { LogOutUseCase(get(),get(),get()) }
    single { SaveTokenUseCase(get(),get()) }
    single { GetTeacherByIdUseCase(get()) }
    single { RegisterTeacherUseCase(get()) }
    single { UpdateTeacherUseCase(get()) }
    single { GetStudentByIdUseCase(get()) }
    single { RegisterStudentUseCase(get()) }
    single { UpdateStudentUseCase(get()) }
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
