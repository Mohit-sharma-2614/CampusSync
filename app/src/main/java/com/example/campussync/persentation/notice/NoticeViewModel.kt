//package com.example.campussync.persentation.notice
//
//import android.annotation.SuppressLint
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.campussync.data.repository.NoticeRepository
//import com.example.campussync.data.repository.StudentRepository
//import com.example.campussync.data.repository.TeacherRepository
//import com.example.campussync.utils.Resource
//import com.example.campussync.utils.UserPreferences
//import com.google.gson.GsonBuilder
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.filterNotNull
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch
//import okhttp3.Response
//import ua.naiksoftware.stomp.Stomp
//import ua.naiksoftware.stomp.StompClient
//import ua.naiksoftware.stomp.dto.LifecycleEvent
//import ua.naiksoftware.stomp.dto.StompHeader
//import java.time.LocalDateTime
//import javax.inject.Inject
//
//// Data Classes needed for the ViewModel logic
//enum class NoticeScope { COLLEGE, DEPARTMENT }
//
//data class Notice(
//    val id: Long = 0,
//    val title: String,
//    val content: String,
//    val scope: NoticeScope,
//    val targetDepartment: String? = null, // e.g. "CS", "MECH"
//    val authorName: String? = null, // Backend fills this
//    val timestamp: String = LocalDateTime.now().toString()
//)
//
//@HiltViewModel
//class NoticeViewModel @Inject constructor(
//    private val userPreferences: UserPreferences,
//    private val teacherRepository: TeacherRepository,
//    private val studentRepository: StudentRepository,
//    private val noticeRepository: NoticeRepository
//): ViewModel() {
//
//    // --- User Preferences Flows ---
//    val isLoggedIn: StateFlow<Boolean> = userPreferences.isLoggedIn.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = false
//    )
//
//    val userId: StateFlow<String?> = userPreferences.userId.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = null
//    )
//
//    val isTeacher: StateFlow<Boolean> = userPreferences.isTeacher.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = false
//    )
//
//    // --- Internal State ---
//    private val gson = GsonBuilder().create()
//
//    // State for the Student View (List of notices)
//    private val _notices = MutableStateFlow<List<Notice>>(emptyList())
//    val notices: StateFlow<List<Notice>> = _notices.asStateFlow()
//
//    // State for the Teacher View (Loading/Success status)
//    private val _sendingStatus = MutableStateFlow<String?>(null)
//    val sendingStatus: StateFlow<String?> = _sendingStatus.asStateFlow()
//
//    // WebSocket Configuration
//    private lateinit var stompClient: StompClient
//
//    // Cache to avoid multiple DB calls
//    private var cachedToken: String? = null
//
//    init {
//        // 1. Load initial history (Offline recovery)
//        syncNotices()
//
//        // 2. Initialize WebSocket logic
//        initializeWebSocketConnection()
//    }
//
//    // Helper Function to Get Token (Used by WebSocket & SendNotice)
//    private suspend fun getAuthToken(): String? {
//        // Return cached token if available
//        if (cachedToken != null) return cachedToken
//
//        val currentUserId = userId.filterNotNull().first()
//        val isTeacherMode = isTeacher.first()
//
//        val token = if (isTeacherMode) {
//            when (val res = teacherRepository.getTeacherById(currentUserId.toLong())) {
//                is Resource.Success -> res.data.token
//                else -> null
//            }
//        } else {
//            when (val res = studentRepository.getStudentById(currentUserId.toLong())) {
//                is Resource.Success -> res.data.token
//                else -> null
//            }
//        }
//
//        if (token != null) cachedToken = token
//        return token
//    }
//
//    private fun initializeWebSocketConnection() {
//        viewModelScope.launch {
//            try {
//                val token = getAuthToken()
//
//                if (token == null) {
//                    Log.e("NoticeVM", "Authentication failed: No token found. Cannot connect to WebSocket.")
//                    return@launch
//                }
//
//                // Initialize Client
//                stompClient = Stomp.over(
//                    Stomp.ConnectionProvider.OKHTTP,
//                    "ws://10.0.2.2:8080/ws-connect/websocket"
//                )
//
//                // Connect with Token
//                connectWebSocket(token)
//
//            } catch (e: Exception) {
//                Log.e("NoticeVM", "Setup failed: ${e.message}")
//            }
//        }
//    }
//
//    @SuppressLint("CheckResult")
//    private fun connectWebSocket(token: String) {
//        if (!::stompClient.isInitialized) return
//
//        val headers = listOf(
//            StompHeader("Authorization", "Bearer $token")
//        )
//
//        stompClient.connect(headers)
//
//        stompClient.lifecycle().subscribe { lifecycleEvent ->
//            when (lifecycleEvent.type) {
//                LifecycleEvent.Type.OPENED -> Log.d("NoticeVM", "Stomp connection opened")
//                LifecycleEvent.Type.ERROR -> Log.e("NoticeVM", "Stomp connection error", lifecycleEvent.exception)
//                LifecycleEvent.Type.CLOSED -> Log.d("NoticeVM", "Stomp connection closed")
//                else -> {}
//            }
//        }
//
//        stompClient.topic("/topic/college").subscribe({ topicMessage ->
//            try {
//                val newNotice = gson.fromJson(topicMessage.payload, Notice::class.java)
//                addNoticeInternal(newNotice)
//            } catch (e: Exception) {
//                Log.e("NoticeVM", "Parse error: ${e.message}")
//            }
//        }, { error ->
//            Log.e("NoticeVM", "Subscribe error", error)
//        })
//
//        // In a real app, subscribe dynamically based on user Dept
//        stompClient.topic("/topic/dept.CS").subscribe({ topicMessage ->
//            try {
//                val newNotice = gson.fromJson(topicMessage.payload, Notice::class.java)
//                addNoticeInternal(newNotice)
//            } catch (e: Exception) {
//                Log.e("NoticeVM", "Parse error: ${e.message}")
//            }
//        }, { error ->
//            Log.e("NoticeVM", "Subscribe error", error)
//        })
//    }
//
//    fun syncNotices() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                Log.d("NoticeVM", "Syncing notices from API...")
//
//                val token = getAuthToken()
//                if(token == null){
//                    Log.e("NoticeVM", "Authentication failed: No token found. Cannot sync notices.")
//                    return@launch
//                }
//
//                val currentUserId = userId.first() ?: return@launch
//                val isTeacherMode = isTeacher.first()
//                var dept = "CS" // Default fallback
//
//                if(isTeacherMode) {
//                    val res = teacherRepository.getTeacherById(currentUserId.toLong())
//                    if (res is Resource.Success) dept = res.data.department
//                } else {
//                    val res = studentRepository.getStudentById(currentUserId.toLong())
//                    //if (res is Resource.Success) dept = res.data.department
//                }
//
//                val result = noticeRepository.getNotices(token,dept)
//
//                when(result) {
//                    is Resource.Success -> {
//                        _notices.value = result.data
//                        Log.d("NoticeVM", "Synced ${result.data.size} notices")
//                    }
//                    is Resource.Error -> {
//                        Log.e("NoticeVM", "Sync failed: ${result.message}")
//                    }
//                    is Resource.Loading -> {}
//                }
//            } catch (e: Exception) {
//                Log.e("NoticeVM", "Sync failed with exception: ${e.message}")
//            }
//        }
//    }
//
//    // Helper to update state flow safely
//    private fun addNoticeInternal(notice: Notice) {
//        val currentList = _notices.value.toMutableList()
//        currentList.add(0, notice)
//        _notices.value = currentList
//    }
//
//    fun sendNotice(title: String, content: String, scope: NoticeScope, dept: String?) {
//        viewModelScope.launch(Dispatchers.IO) {
//            _sendingStatus.value = "Sending..."
//            try {
//                // 1. Get Token
//                val token = getAuthToken()
//
//                if (token == null) {
//                    _sendingStatus.value = "Error: Authentication Token Missing. Try logging in again."
//                    return@launch
//                }
//
//                val notice = Notice(
//                    title = title,
//                    content = content,
//                    scope = scope,
//                    targetDepartment = dept,
//                    authorName = "" // Backend handles this via JWT
//                )
//
//                // 2. Call Repository WITH TOKEN
//                val result = noticeRepository.sendNotice(token, notice)
//
//                when(result) {
//                    is Resource.Success -> {
//                        _sendingStatus.value = "Sent Successfully!"
//                    }
//                    is Resource.Error -> {
//                        _sendingStatus.value = "Error: ${result.message}"
//                        Log.e("NoticeVM", "Send Error: ${result.message}")
//                    }
//                    is Resource.Loading -> {
//                        _sendingStatus.value = "Sending..."
//                    }
//                }
//            } catch (e: Exception) {
//                _sendingStatus.value = "Error: ${e.message}"
//            }
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        if (::stompClient.isInitialized) {
//            stompClient.disconnect()
//        }
//    }
//}