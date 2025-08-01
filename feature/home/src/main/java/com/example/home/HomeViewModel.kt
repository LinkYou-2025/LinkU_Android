package com.example.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.LinkSimpleInfo
import com.example.core.repository.LinkuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository
) : ViewModel() {

    var recentLinks by mutableStateOf<List<LinkSimpleInfo>>(emptyList())
        private set

    init {
        loadRecentLinks()
    }

    fun loadRecentLinks() {
        viewModelScope.launch {
            runCatching {
                linkuRepository.getRecentLinks()
            }.onSuccess {
                recentLinks = it
            }.onFailure {
                // TODO: 에러 처리 필요시 여기에 추가
                recentLinks = emptyList()
            }
        }
    }
}
