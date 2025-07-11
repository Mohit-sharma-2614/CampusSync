package com.example.campussync.persentation.classes

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


data class ClassesUiState(
    val showDailoug: Boolean = true,
)


@HiltViewModel
class ClassesViewModel @Inject constructor(): ViewModel() {
    private val _uiStat = MutableStateFlow(ClassesUiState())
    val uiState: StateFlow<ClassesUiState> = _uiStat

}