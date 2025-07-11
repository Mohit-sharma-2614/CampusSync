package com.example.campussync.persentation.assignment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class AssignmentUiState(
    val showDailoug: Boolean = true,
)


@HiltViewModel
class AssignmentViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow(AssignmentUiState())
    val uiState: StateFlow<AssignmentUiState> = _uiState


}