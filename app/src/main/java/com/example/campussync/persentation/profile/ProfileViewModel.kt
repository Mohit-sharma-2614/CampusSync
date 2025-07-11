//package com.example.campussync.persentation.profile
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.campussync.data.model.student.StudentLoginResponse
//import com.example.campussync.data.model.teacher.TeacherLoginResponse
//import com.example.campussync.data.repository.AuthRepository
//import com.example.campussync.data.repository.StudentRepository
//import com.example.campussync.data.repository.TeacherRepository
//import com.example.campussync.utils.Resource
//import com.example.campussync.utils.TokenManager
//import com.example.campussync.utils.UserPreferences
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//
//data class ProfileUiState(
//    val isLoading: Boolean = true,
//    val errorMessage: String? = null,
//    val name: String = "",
//    val email: String = "",
//    val role: String = "", // "Student" or "Teacher"
//    val id: Long = 0L,
//    val studentUid: String? = null, // Specific to student
//    val semester: Int? = null,      // Specific to student
//    val department: String? = null, // Specific to teacher
//    val isLoggedIn: Boolean = false // Reflects login status from UserPreferences
//)
//
//@HiltViewModel
//class ProfileViewModel @Inject constructor(
//    private val studentRepository: StudentRepository,
//    private val teacherRepository: TeacherRepository, // Ensure you have this repository
//    private val userPreferences: UserPreferences,
//    private val tokenManager: TokenManager
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(ProfileUiState())
//    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
//
//    init {
//        // Collect user login status, ID, and role from UserPreferences
//        // This ensures the ViewModel reacts to changes in login state or user details.
//        viewModelScope.launch {
//            userPreferences.isLoggedIn.collectLatest { loggedIn ->
//                Log.d("ProfileViewModel", "isLoggedIn collected: $loggedIn")
//                _uiState.update { it.copy(isLoggedIn = loggedIn) }
//                // If logged out, reset state and potentially navigate
//                if (!loggedIn) {
//                    _uiState.update { ProfileUiState(isLoading = false) } // Reset to default, not loading
//                }
//            }
//        }
//
//        viewModelScope.launch {
//            userPreferences.userId.collectLatest { idString ->
//                val currentUserId = idString?.toLongOrNull() ?: 0L
//                Log.d("ProfileViewModel", "userId collected: $currentUserId")
//                _uiState.update { it.copy(id = currentUserId) }
//                // Trigger data fetch when userId is available
//                fetchProfileData()
//            }
//        }
//
//        viewModelScope.launch {
//            userPreferences.isTeacher.collectLatest { isTeacherValue ->
//                Log.d("ProfileViewModel", "isTeacher collected: $isTeacherValue")
//                _uiState.update { it.copy(role = if (isTeacherValue) "Teacher" else "Student") }
//                // Trigger data fetch when role is available
//                fetchProfileData()
//            }
//        }
//
//        // Initial data fetch when ViewModel is created
//        fetchProfileData()
//    }
//
//    /**
//     * Fetches the detailed profile data (student or teacher) based on the current user's role and ID.
//     * Updates the [_uiState] with the fetched information or an error message.
//     */
//    private fun fetchProfileData() {
//        viewModelScope.launch {
//            // Only fetch if user is logged in and has a valid ID
//            val currentUserId = _uiState.value.id
//            val currentUserRole = _uiState.value.role // "Student" or "Teacher"
//            val isLoggedIn = _uiState.value.isLoggedIn
//
//            if (!isLoggedIn || currentUserId == 0L) {
//                Log.d("ProfileViewModel", "Not logged in or invalid ID, skipping profile data fetch.")
//                _uiState.update { it.copy(isLoading = false, errorMessage = "Please log in to view profile.") }
//                return@launch
//            }
//
//            _uiState.update { it.copy(isLoading = true, errorMessage = null) } // Start loading, clear previous errors
//
//            val result = if (currentUserRole == "Teacher") {
//                teacherRepository.getTeacherById(currentUserId)
//            } else {
//                studentRepository.getStudentById(currentUserId)
//            }
//
//            _uiState.update { currentState ->
//                when (result) {
//                    is Resource.Success -> {
//                        if (currentUserRole == "Teacher") {
//                            val teacher = result.data as TeacherLoginResponse
//                            currentState.copy(
//                                isLoading = false,
//                                name = teacher.name,
//                                email = teacher.email,
//                                department = teacher.department,
//                                studentUid = null,
//                                semester = null,
//                                errorMessage = null
//                            )
//                        } else {
//                            val student = result.data as StudentLoginResponse
//                            currentState.copy(
//                                isLoading = false,
//                                name = student.name,
//                                email = student.email,
//                                studentUid = student.studentUid,
//                                semester = student.semester,
//                                department = null,
//                                errorMessage = null
//                            )
//                        }
//                    }
//                    is Resource.Error -> {
//                        Log.e("ProfileViewModel", "Failed to fetch profile: ${result.message}")
//                        currentState.copy(
//                            isLoading = false,
//                            errorMessage = result.message ?: "Failed to load profile data."
//                        )
//                    }
//                    is Resource.Loading -> currentState // Should not happen here, but for completeness
//                }
//            }
//        }
//    }
//
//    /**
//     * Handles the logout process by clearing user preferences and token.
//     */
//    fun onLogout() {
//        viewModelScope.launch {
//            Log.d("ProfileViewModel", "Logging out user.")
//            tokenManager.clearToken()
//            userPreferences.clearUserInfo() // Clears isLoggedIn, userId, isTeacher
//            // The collectLatest blocks will react to these changes and update _uiState
//        }
//    }
//}