package com.example.file

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.CategorySimpleInfo
import com.example.core.model.FolderSimpleInfo
import com.example.core.model.LinkSimpleInfo
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

    // 3. 상위 폴더 리스트
    private val _parentFolders = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    val parentFolders: StateFlow<List<FolderSimpleInfo>> = _parentFolders.asStateFlow()

    // 4. 하위 폴더 리스트
    private val _subFolders = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    val subFolders: StateFlow<List<FolderSimpleInfo>> = _subFolders.asStateFlow()

    // 5. 링크 리스트
    private val _links = MutableStateFlow<List<LinkSimpleInfo>>(emptyList())
    val links: StateFlow<List<LinkSimpleInfo>> = _links.asStateFlow()

    // 6. 로딩/에러 상태 예시 (원하면 커스텀하게)
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ---------- get method ----------
    // 중분류 전체 불러오기
    fun getParentfolders() {
        Log.d("FileViewModel", "getParentfolders")

        viewModelScope.launch {
            Log.d("FileViewModel", "getParentfolders launch")

            _loading.value = true
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "getParentfolders try")

                val result = folderRepository.getParentfolders()

                Log.d("FileViewModel", "getParentfolders try result: $result")

                _parentFolders.value = result

            } catch (e: Exception) {
                Log.d("FileViewModel", "getParentfolders catch: $e.message")

                _errorMessage.value = e.message

            } finally {
                Log.d("FileViewModel", "getParentfolders finally")

                _loading.value = false

            }

            Log.d("FileViewModel", "getParentfolders end")
        }
        Log.d("FileViewModel", "getParentfolders return")
    }

    // 하위 폴더 전체 불러오기
    fun getSubfolders(parentFolderId: Long) {
        Log.d("FileViewModel", "getSubfolders")
        viewModelScope.launch {
            Log.d("FileViewModel", "getSubfolders launch")

            _loading.value = true
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "getSubfolders try")

                val result = folderRepository.getSubfolders(parentFolderId)

                Log.d("FileViewModel", "getSubfolders try result: $result")

                _subFolders.value = result

            } catch (e: Exception) {
                Log.d("FileViewModel", "getSubfolders catch: $e.message")

                _errorMessage.value = e.message

            } finally {
                Log.d("FileViewModel", "getSubfolders finally")

                _loading.value = false
            }
        }
        Log.d("FileViewModel", "getSubfolders return")
    }

    // 링크 불러오기
    fun getLinks(folderId: Long) {
        Log.d("FileViewModel", "getLinks")

        viewModelScope.launch {
            Log.d("FileViewModel", "getLinks launch")

            _loading.value = true
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "getLinks try")

                folderRepository.getLinksFolders(folderId, null, null,
                    onGetFolders = { /*_subFolders.value = it*/ },
                    onGetLinks = { _links.value = it }
                )

                Log.d("FileViewModel", "getLinks try result: ${_links.value}")

            } catch (e: Exception) {
                Log.d("FileViewModel", "getLinks catch: $e.message")

                _errorMessage.value = e.message

            } finally {
                Log.d("FileViewModel", "getLinks finally")

                _loading.value = false
            }
        }
        Log.d("FileViewModel", "getLinks return")
    }
    // ---------- get method ----------

    // ---------- create method ----------
    fun createSubfolder(
        parentFolderId: Long,
        folderName: String
    ){
        Log.d("FileViewModel", "createSubfolder")

        viewModelScope.launch {
            Log.d("FileViewModel", "createSubfolder launch")

            _loading.value = true
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "createSubfolder try")

                val newFolder = folderRepository.createSubfolder(parentFolderId, folderName).run{
                    FolderSimpleInfo(
                        folderId = this.folderId,
                        folderName = this.folderName,
                        parentFolderId = this.parentFolderId,
                        isBookmarked = false
                    )
                }

                _subFolders.value = _subFolders.value + newFolder

                Log.d("FileViewModel", "createSubfolder try result: $newFolder")

            } catch (e: Exception) {
                Log.d("FileViewModel", "createSubfolder catch: $e.message")

                _errorMessage.value = e.message

            } finally {
                Log.d("FileViewModel", "createSubfolder finally")

                _loading.value = false
            }
        }
        Log.d("FileViewModel", "createSubfolder return")
    }
    // ---------- create method ----------

    // ---------- update method ----------
    // 북마크 등록/해제
    fun updateBookmark(
        folderId: Long,
        isBookmarked: Boolean
    ): Boolean {
        Log.d("FileViewModel", "updateBookmark")

        var result = isBookmarked

        viewModelScope.launch {
            Log.d("FileViewModel", "updateBookmark launch")

            _loading.value = true
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "updateBookmark try")

                result = folderRepository.updateBookmark(folderId, isBookmarked)

                Log.d("FileViewModel", "updateBookmark try result: $result")

            } catch (e: Exception) {
                Log.d("FileViewModel", "updateBookmark catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "updateBookmark finally")

                _loading.value = false
            }

            Log.d("FileViewModel", "updateBookmark end")
        }
        Log.d("FileViewModel", "updateBookmark return")

        return result
    }
    // ---------- update method ----------

    // ---------- delete method ----------
    // 소분류 폴더 삭제
    fun deleteSubfolder(folderId: Long, index: Int) {
        Log.d("FileViewModel", "deleteSubfolder")

        viewModelScope.launch {
            Log.d("FileViewModel", "deleteSubfolder launch")

            _loading.value = true
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "deleteSubfolder try")

                folderRepository.deleteSubfolder(folderId)
                _subFolders.value = _subFolders.value.filterIndexed { i, _ -> i != index }

                Log.d("FileViewModel", "deleteSubfolder try result")

            } catch (e: Exception) {
                Log.d("FileViewModel", "deleteSubfolder catch: $e.message")

                _errorMessage.value = e.message

            } finally {
                Log.d("FileViewModel", "deleteSubfolder finally")

                _loading.value = false
            }

            Log.d("FileViewModel", "deleteSubfolder end")
            }
        Log.d("FileViewModel", "deleteSubfolder return")
    }
    // ---------- delete method ----------
}