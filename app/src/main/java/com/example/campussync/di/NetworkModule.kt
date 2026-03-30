package com.example.campussync.di

//import com.example.campussync.api.NoticeApiService
import android.content.Context
import com.example.campussync.api.AttendanceApiService
import com.example.campussync.api.AttendanceTokenApiService
import com.example.campussync.api.AuthApiService
import com.example.campussync.api.DepartmentApiService
import com.example.campussync.api.EnrollmentApiService
import com.example.campussync.api.StudentApiService
import com.example.campussync.api.SubjectApiService
import com.example.campussync.api.TeacherApiService
import com.example.campussync.data.remote.client.HttpClientFactory
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.data.manager.*
import com.example.campussync.domain.manager.TokenManagerImpl
import com.example.campussync.utils.AuthInterceptor
import com.example.campussync.utils.ConnectivityObserver
import com.example.campussync.utils.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val BASE_URL = "http://13.60.252.20:8080" //"http://10.0.2.2:8080"

    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {
        return ConnectivityObserver(context)
    }

//    @Singleton
//    @Provides
//    fun provideNoticeApiService(retrofit: Retrofit): NoticeApiService {
//        return retrofit.create(NoticeApiService::class.java)
//    }

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

val networkModule = module {
    val BASE_URL = "http://13.60.252.20:8080" //"http://10.0.2.2:8080"

    single<TokenManager> { TokenManagerImpl(get()) }

    single(named("baseUrl")) {
        /*"http://13.60.252.20:8080"*/"http://10.0.2.2:8080"
    }

    // Public client no auth
    single(named("publicClient")) {
        HttpClientFactory.create(baseUrl = get(named("baseUrl")))
    }

    // Auth client
    single(named("authClient")) {
        val tokenManager = get<TokenManager>()

        HttpClientFactory.create(
            baseUrl = get(named("baseUrl")),
            tokenProvider = { tokenManager.getToken() },
            onRefreshToken = {
                try {
                    val publicClient = get<HttpClient>(named("publicClient"))

                    val response = publicClient.post("/api/auth/refresh-token") {
                        setBody(mapOf("refreshToken" to tokenManager.getRefreshToken()))
                    }

                    val newToken = response.body<RefreshTokenInputDto>().token
                    tokenManager.saveToken(newToken)

                    newToken
                } catch (e: Exception) {
                    null
                }
            }
        )
    }
}
