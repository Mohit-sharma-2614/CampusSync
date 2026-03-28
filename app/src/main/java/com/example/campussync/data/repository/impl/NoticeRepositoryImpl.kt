package com.example.campussync.data.repository.impl

//import android.util.Log
//import com.example.campussync.api.NoticeApiService
//import com.example.campussync.data.repository.NoticeRepository
//import com.example.campussync.persentation.notice.Notice
//import com.example.campussync.utils.Resource
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import javax.inject.Inject
//
//class NoticeRepositoryImpl @Inject constructor(
//    private val apiService: NoticeApiService
//) : NoticeRepository {
//
//    override suspend fun getNotices(department: String): Resource<List<Notice>> {
//        return withContext(Dispatchers.IO) {
//            try {
//                // The AuthInterceptor automatically adds the Bearer token here
//                val response = apiService.syncNotices(department, null)
//                Resource.Success(response)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Resource.Error(e.message ?: "Failed to fetch notices")
//            }
//        }
//    }
//
//    override suspend fun sendNotice(notice: Notice): Resource<Notice> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = apiService.sendNotice(notice)
//                Resource.Success(response)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Resource.Error(e.message ?: "Failed to send notice")
//            }
//        }
//    }
//}