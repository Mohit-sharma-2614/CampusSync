package com.example.campussync.di

import android.content.Context
import androidx.compose.ui.tooling.preview.Preview
import com.example.campussync.api.AttendanceApiService
import com.example.campussync.api.AttendanceTokenApiService
import com.example.campussync.api.AuthApiService
import com.example.campussync.api.DepartmentApiService
import com.example.campussync.api.EnrollmentApiService
import com.example.campussync.api.StudentApiService
import com.example.campussync.api.SubjectApiService
import com.example.campussync.api.TeacherApiService
import com.example.campussync.utils.AuthInterceptor
import com.example.campussync.utils.TokenManager
import com.example.campussync.utils.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val BASE_URL = "http://10.0.2.2:8080/"


    @Provides
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor = AuthInterceptor(tokenManager)


    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideTeacherApiService(retrofit: Retrofit): TeacherApiService {
        return retrofit.create(TeacherApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideStudentApiService(retrofit: Retrofit): StudentApiService {
        return retrofit.create(StudentApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAttendanceApiService(retrofit: Retrofit): AttendanceApiService {
        return retrofit.create(AttendanceApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideDepartmentApiService(retrofit: Retrofit): DepartmentApiService {
        return retrofit.create(DepartmentApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideSubjectApiService(retrofit: Retrofit): SubjectApiService {
        return retrofit.create(SubjectApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideEnrollmentApiService(retrofit: Retrofit): EnrollmentApiService {
        return retrofit.create(EnrollmentApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAttendanceTokenApiService(retrofit: Retrofit): AttendanceTokenApiService {
        return retrofit.create(AttendanceTokenApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

}