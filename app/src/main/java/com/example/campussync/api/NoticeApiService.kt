package com.example.campussync.api

//import com.example.campussync.persentation.notice.Notice
//import retrofit2.http.Body
//import retrofit2.http.GET
//import retrofit2.http.Header
//import retrofit2.http.POST
//import retrofit2.http.Query
//
//interface NoticeApiService {
//
//    @POST("api/notices/send")
//    suspend fun sendNotice(
//        @Body notice: Notice
//    ): Notice
//
//    @GET("api/notices/sync")
//    suspend fun syncNotices(
//        @Query("department") department: String,
//        @Query("lastSyncTime") lastSyncTime: String?
//    ): List<Notice>
//}