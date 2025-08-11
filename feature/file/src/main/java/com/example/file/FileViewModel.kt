package com.example.file

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.CategoryColorList
import com.example.core.model.FolderSimpleInfo
import com.example.core.model.LinkSimpleInfo
import com.example.core.repository.CategoryRepository
import com.example.core.repository.FolderRepository
import com.example.core.repository.UserRepository
import com.example.data.api.dto.server.*
import com.example.data.preference.AuthPreference
import com.example.file.ui.theme.CategoryColorStyle
import com.example.file.ui.theme.toCategoryColorStyleMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val folderRepository: FolderRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference
) : ViewModel() {

    // *닉네임*
    private val _nickname = MutableStateFlow<String?>(null)
    val nickname: StateFlow<String?> = _nickname.asStateFlow()

    // 공유 폴더 리스트
    private val _sharedTopFolders = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    val sharedTopFolders: StateFlow<List<FolderSimpleInfo>> = _sharedTopFolders.asStateFlow()

    private val _sharedBottomFolders = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    val sharedBottomFolders: StateFlow<List<FolderSimpleInfo>> = _sharedBottomFolders.asStateFlow()

    // 1. 카테고리 리스트
    private val _categoryList = MutableStateFlow<List<CategoryColorList>>(emptyList())
    val categoryList: StateFlow<List<CategoryColorList>> = _categoryList.asStateFlow()

    // 1-1. 색깔 리스트
    private val _categoryColorMap = MutableStateFlow<Map<String, CategoryColorStyle>>(emptyMap())
    val categoryColorMap: StateFlow<Map<String, CategoryColorStyle>> = _categoryColorMap.asStateFlow()

    // 2. 내 폴더 트리
    private val _folderTree = MutableStateFlow<List<FolderTreeResponseDTO>>(emptyList())
    val folderTree: StateFlow<List<FolderTreeResponseDTO>> = _folderTree.asStateFlow()

    // 3. 상위 폴더 리스트
    private val _parentFolders = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    // 매번 북마크 우선 정렬
    val parentFolders: StateFlow<List<FolderSimpleInfo>> =
        _parentFolders
            .map { list ->
                list.sortedByDescending { it.isBookmarked }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // 4. 하위 폴더 리스트
    private val _subFolders = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    val subFolders: StateFlow<List<FolderSimpleInfo>> = _subFolders.asStateFlow()

    // 4-1. 하위 폴더 요청 커서
    private val _subFoldersCursor = MutableStateFlow<String?>(null)
    val subFoldersCursor: StateFlow<String?> = _subFoldersCursor.asStateFlow()

    // 5. 링크 리스트
    private val _links = MutableStateFlow<List<LinkSimpleInfo>>(emptyList())
    val links: StateFlow<List<LinkSimpleInfo>> = _links.asStateFlow()

    // 5-1. 분류되지않은 링크 리스트
    private val _notCategorizationLinks = MutableStateFlow<List<LinkSimpleInfo>>(emptyList())
    val notCategorizationLinks: StateFlow<List<LinkSimpleInfo>> = _notCategorizationLinks.asStateFlow()

    // 6. 로딩/에러 상태
    private val _loadingCount = MutableStateFlow(0)

    fun startLoading() {
        _loadingCount.update { it + 1 }
    }

    fun stopLoading() {
        _loadingCount.update { count -> (count - 1).coerceAtLeast(0) }
    }

    // 6-1. 로딩중
    private val _loading = _loadingCount
        .map { it > 0 } // 카운트가 0보다 크면 true
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val loading: StateFlow<Boolean> = _loading

    // 6-2. 에러 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ---------- get method ----------
    // 유저 닉네임 가져오기
    fun loadNickname() {
        Log.d("FileViewModel", "loadNickname")

        viewModelScope.launch {
            Log.d("FileViewModel", "loadNickname launch")

            startLoading()
            _errorMessage.value = null

            try{
                val userId = authPreference.userId!!

                val name = userRepository.getUserInfo(userId)
                _nickname.value = name
            }catch (e: Exception){
                Log.d("FileViewModel", "loadNickname catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "loadNickname finally")

                stopLoading()
            }

            Log.d("FileViewModel", "loadNickname end")
        }
        Log.d("FileViewModel", "loadNickname return")
    }

    // 중분류 전체 불러오기
    fun getParentfolders() {
        Log.d("FileViewModel", "getParentfolders")

        viewModelScope.launch {
            Log.d("FileViewModel", "getParentfolders launch")

            startLoading()
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

                stopLoading()

            }

            Log.d("FileViewModel", "getParentfolders end")
        }
        Log.d("FileViewModel", "getParentfolders return")
    }

    // 중분류 색상 리스트 불러오기
    fun getCategoryColor(){
        Log.d("FileViewModel", "getCategoryColor")

        viewModelScope.launch {
            Log.d("FileViewModel", "getCategoryColor launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "getCategoryColor try")

                _categoryColorMap.value = categoryRepository.getCategoryColor().toCategoryColorStyleMap()

                Log.d("FileViewModel", "getCategoryColor try result: ${_categoryColorMap.value}")

            }catch (e: Exception){
                Log.d("FileViewModel", "getCategoryColor catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "getCategoryColor finally")

                stopLoading()
            }
        }

        Log.d("FileViewModel", "getCategoryColor return")
    }

    // 하위 폴더 전체 불러오기
    fun getSubfolders(parentFolderId: Long) {
        Log.d("FileViewModel", "getSubfolders")

        viewModelScope.launch {
            Log.d("FileViewModel", "getSubfolders launch")

            startLoading()
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

                stopLoading()
            }
        }
        Log.d("FileViewModel", "getSubfolders return")
    }

    // 링크 불러오기
    fun getLinks(folderId: Long) {
        Log.d("FileViewModel", "getLinks")

        viewModelScope.launch {
            Log.d("FileViewModel", "getLinks launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "getLinks try")

                _subFoldersCursor.value = folderRepository.getLinksFolders(
                    folderId = folderId,
                    limit = null,
                    cursor = _subFoldersCursor.value,
                    onGetFolders = { },
                    onGetLinks = { list -> _notCategorizationLinks.value = list.map { it.copy() } }
                )

                Log.d("FileViewModel", "getLinks try result: ${_notCategorizationLinks.value}")

            } catch (e: Exception) {
                Log.d("FileViewModel", "getLinks catch: $e.message")

                _errorMessage.value = e.message

            } finally {
                Log.d("FileViewModel", "getLinks finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "getLinks return")
    }

    // 링크, 폴더 불러오기
    fun getLinksFolders(folderId: Long) {
        Log.d("FileViewModel", "getNotCategorizationLinks")

        viewModelScope.launch {
            Log.d("FileViewModel", "getNotCategorizationLinks launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "getNotCategorizationLinks try")

                _subFoldersCursor.value = folderRepository.getLinksFolders(
                    folderId = folderId,
                    limit = null,
                    cursor = _subFoldersCursor.value,
                    onGetFolders = { list -> _subFolders.value = list.map { it.copy() } },
                    onGetLinks = { list -> _notCategorizationLinks.value = list.map { it.copy() } }
                )

                Log.d("FileViewModel", "getNotCategorizationLinks try result: ${_notCategorizationLinks.value}")

            } catch (e: Exception) {
                Log.d("FileViewModel", "getNotCategorizationLinks catch: $e.message")

                _errorMessage.value = e.message

            } finally {
                Log.d("FileViewModel", "getNotCategorizationLinks finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "getNotCategorizationLinks return")
    }
    // ---------- get method ----------

    // ---------- fetch method ----------
    // 외부로 소분류 폴더들을 반환하는 메소드
    fun fetchSubfolders(parentFolderId: Long):List<FolderSimpleInfo>{
        Log.d("FileViewModel", "fetchSubfolders")

        var folders: List<FolderSimpleInfo> = emptyList()

        viewModelScope.launch {
            Log.d("FileViewModel", "fetchSubfolders launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "fetchSubfolders try")

                folders = folderRepository.getSubfolders(parentFolderId)

                Log.d("FileViewModel", "fetchSubfolders try result: $folders")

            }catch (e: Exception){
                Log.d("FileViewModel", "fetchSubfolders catch: $e.message")

                _errorMessage.value = e.message

                folders = emptyList()
            }
            finally {
                Log.d("FileViewModel", "fetchSubfolders finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "fetchSubfolders return")

        return folders
    }
    // ---------- fetch method ----------

    // ---------- create method ----------
    fun createSubfolder(
        parentFolderId: Long,
        folderName: String
    ){
        Log.d("FileViewModel", "createSubfolder")

        viewModelScope.launch {
            Log.d("FileViewModel", "createSubfolder launch")

            startLoading()
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

                stopLoading()
            }
        }
        Log.d("FileViewModel", "createSubfolder return")
    }
    // ---------- create method ----------

    // ---------- update method ----------
    // 중분류 폴더 색상 수정
    fun updateCategoryColor(
        categoryName: String,
        colorId: Long,
        colorStyle: CategoryColorStyle
    ){
        Log.d("FileViewModel", "updateCategoryColor")

        val categoryId = (categoryColorMap.value.keys.indexOf(categoryName)+1).toLong()

        viewModelScope.launch {
            Log.d("FileViewModel", "updateCategoryColor launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "updateCategoryColor try")

                categoryRepository.updateCategoryColor(categoryId, colorId)

                _categoryColorMap.update { map ->
                    map.mapValues { (key, value) ->
                        if (key == categoryName) {
                            colorStyle
                        } else {
                            value
                        }
                    }
                }

                Log.d("FileViewModel", "updateCategoryColor try result")

            }catch (e: Exception){
                Log.d("FileViewModel", "updateCategoryColor catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "updateCategoryColor finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "updateCategoryColor return")
    }
    // 북마크 등록/해제
    fun updateBookmark(
        folderId: Long,
        updateBookmarked: Boolean
    ): Boolean {
        Log.d("FileViewModel", "updateBookmark")

        var result = updateBookmarked

        viewModelScope.launch {
            Log.d("FileViewModel", "updateBookmark launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "updateBookmark try")

                result = folderRepository.updateBookmark(folderId, updateBookmarked)

                Log.d("FileViewModel", "updateBookmark try result: $result")

                _parentFolders.update { list ->
                    list.map { folder ->
                        if (folder.folderId == folderId) {
                            folder.copy(isBookmarked = updateBookmarked)
                        } else {
                            folder
                        }
                    }
                }

                Log.d("FileViewModel", "updateBookmark update well _parentFolders")

            } catch (e: Exception) {
                Log.d("FileViewModel", "updateBookmark catch: $e.message")

                _errorMessage.value = e.message

                result = updateBookmarked
            }finally {
                Log.d("FileViewModel", "updateBookmark finally")

                stopLoading()
            }

            Log.d("FileViewModel", "updateBookmark end")
        }
        Log.d("FileViewModel", "updateBookmark return")

        return result
    }

    // 소분류 폴더 이름 수정
    fun updateSubfolder(folderId: Long, folderName: String){
        Log.d("FileViewModel", "updateSubfolder")

        viewModelScope.launch {
            Log.d("FileViewModel", "updateSubfolder launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "updateSubfolder try")

                folderRepository.updateSubfolder(folderId, folderName)

                Log.d("FileViewModel", "updateSubfolder try result")

                _subFolders.update { list ->
                    list.map { folder ->
                        if (folder.folderId == folderId) {
                            folder.copy(folderName = folderName)
                        } else {
                            folder
                        }
                    }
                }
            }catch (e: Exception){
                Log.d("FileViewModel", "updateSubfolder catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "updateSubfolder finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "updateSubfolder return")
    }
    // ---------- update method ----------

    // ---------- delete method ----------
    // 소분류 폴더 삭제
    fun deleteSubfolder(folderId: Long, index: Int) {
        Log.d("FileViewModel", "deleteSubfolder")

        viewModelScope.launch {
            Log.d("FileViewModel", "deleteSubfolder launch")

            startLoading()
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

                stopLoading()
            }

            Log.d("FileViewModel", "deleteSubfolder end")
            }
        Log.d("FileViewModel", "deleteSubfolder return")
    }
    // ---------- delete method ----------

    // ---------- share method ----------
    // 폴더 공유하기
    fun shareFolder(folderId: Long){
        Log.d("FileViewModel", "shareFolder")

        viewModelScope.launch {
            Log.d("FileViewModel", "shareFolder launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "shareFolder try")

                folderRepository.setFolderViewerPermission(folderId)

                Log.d("FileViewModel", "shareFolder try result")
            }catch (e: Exception){
                Log.d("FileViewModel", "shareFolder catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "shareFolder finally")

                stopLoading()
            }

            Log.d("FileViewModel", "shareFolder end")
        }
        Log.d("FileViewModel", "shareFolder return")
    }
    // ---------- share method ----------
}