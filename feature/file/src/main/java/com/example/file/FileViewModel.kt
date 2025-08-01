package com.example.file

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.CategorySimpleInfo
import com.example.core.repository.CategoryRepository
import com.example.core.repository.FolderRepository
import com.example.data.api.dto.server.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val folderRepository: FolderRepository,
) : ViewModel() {

    // 1. 카테고리 리스트
    private val _categoryList = MutableStateFlow<List<CategorySimpleInfo>>(emptyList())
    val categoryList: StateFlow<List<CategorySimpleInfo>> = _categoryList.asStateFlow()

    // 2. 내 폴더 트리
    private val _folderTree = MutableStateFlow<List<FolderTreeResponseDTO>>(emptyList())
    val folderTree: StateFlow<List<FolderTreeResponseDTO>> = _folderTree.asStateFlow()

    // 3. 하위 폴더 리스트
    private val _subFolders = MutableStateFlow<List<FolderListResponseDTO>>(emptyList())
    val subFolders: StateFlow<List<FolderListResponseDTO>> = _subFolders.asStateFlow()

    // 4. 로딩/에러 상태 예시 (원하면 커스텀하게)
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ------------- 주요 함수 -------------

    /** 카테고리 전체 불러오기 */
    fun fetchCategories() {
        Log.d("fetchCategories", "fetchCategories")
        viewModelScope.launch {
            Log.d("fetchCategories", "fetchCategories launch")
            _loading.value = true
            _errorMessage.value = null
            try {
                Log.d("fetchCategories", "fetchCategories try")
                val result = categoryRepository.getCategoryList()
                Log.d("fetchCategories", "fetchCategories try result: $result")
                _categoryList.value = result
            } catch (e: Exception) {
                Log.d("fetchCategories", "fetchCategories catch: $e.message")
                _errorMessage.value = e.message
            } finally {
                Log.d("fetchCategories", "fetchCategories finally")
                _loading.value = false
            }
            Log.d("fetchCategories", "fetchCategories end")
        }
        Log.d("fetchCategories", "fetchCategories return")
    }
}
