package com.example.curation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.core.repository.CurationRepository

@HiltViewModel
class CurationViewModel @Inject constructor(
    private val repository: CurationRepository
) : ViewModel() {

    private val _isGenerating = MutableStateFlow(false)   // 로딩 상태만 관리
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _errorMessage = MutableStateFlow<String?>(null) // 오류 메시지
    val errorMessage: StateFlow<String?> = _errorMessage

    fun generateMonthlyCuration(userId: Long) {
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            try {
                repository.generateMonthlyCuration(userId)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "큐레이션 생성 실패"
            } finally {
                _isGenerating.value = false
            }
        }
    }
}