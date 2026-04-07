package com.example.campussync.persentation.attendance.subject


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campussync.data.manager.UserCredentialManager
import com.example.campussync.data.remote.dto.enums.UserRole
import com.example.campussync.domain.usecases.feature.subjects.GetSubjectsByStudentIdUseCase
import com.example.campussync.domain.usecases.feature.subjects.GetSubjectsByTeacherIdUseCase
import com.example.campussync.domain.usecases.feature.subjects.Subject
import com.example.campussync.domain.usecases.feature.user.GetUserByIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class SubjectAttendanceUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val subjects: List<Subject> = emptyList(),
    val currentUserId: Long = 0L,
    val isTeacher: Boolean = false,
)


class SubjectListViewModel (
    private val getSubjectByStudentIdUseCase: GetSubjectsByStudentIdUseCase,
    private val getSubjectsByTeacherIdUseCase: GetSubjectsByTeacherIdUseCase,
    private val userCredentialManager: UserCredentialManager,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectAttendanceUiState())
    val uiState: StateFlow<SubjectAttendanceUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Step 1: Get userId
            val userIdStr = userCredentialManager.getUserId()

            if (userIdStr.isBlank() || userIdStr == "0") {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Invalid user")
                }
                return@launch
            }

            val userId = userIdStr.toLong()

            _uiState.update { it.copy(currentUserId = userId) }

            // Step 2: Get user details
            val params = GetUserByIdUseCase.Params.forGetUserById(userIdStr)
            val user = getUserByIdUseCase.execute(params)

            val isTeacher = user.role == UserRole.TEACHER
            _uiState.update { it.copy(isTeacher = isTeacher) }

            // Step 3: Fetch subjects
            fetchSubjects(userId, isTeacher)

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun fetchSubjects(userId: Long,isTeacher: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            if(isTeacher){
                val params = GetSubjectsByTeacherIdUseCase.Params.forGetSubjectsByTeacherId(userId.toString())
                    getSubjectsByTeacherIdUseCase.execute(
                        params,
                        viewModelScope,
                        onSuccess = { subjects ->
                            _uiState.update { it.copy(subjects = subjects) }
                        },
                        onError = {
                            Log.e("SubjectListViewModel", "getSubjectsByUserId: Error fetching subjects")
                            _uiState.update { it.copy(errorMessage = it.errorMessage) }
                        },
                        onCancel = {
                            Log.e("SubjectListViewModel", "getSubjectsByUserId: subject fetching cancled.")
                        }
                    )
            } else {
                val params = GetSubjectsByStudentIdUseCase.Params.forGetSubjectsByStudentId(userId)
                    getSubjectByStudentIdUseCase.execute(
                        params,
                        onSuccess = { subjects ->
                            _uiState.update { it.copy(subjects = subjects) }
                        },
                        onError = {
                            Log.e("SubjectListViewModel", "getSubjectsByUserId: Error fetching subjects")
                            _uiState.update { it.copy(errorMessage = it.errorMessage) }
                        }
                    )
            }
        }
    }
}