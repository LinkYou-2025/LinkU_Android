package com.example.file

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.error.SameNameException
import com.example.core.error.UserIdNullException
import com.example.core.model.CategoryColorList
import com.example.core.model.FolderSimpleInfo
import com.example.core.model.LinkItemInfo
import com.example.core.model.SharedFolderInfo
import com.example.core.model.search.RecentQuery
import com.example.core.repository.CategoryRepository
import com.example.core.repository.FolderRepository
import com.example.core.repository.LinkuRepository
import com.example.core.repository.RecentSearchRepository
import com.example.core.repository.UserRepository
import com.example.data.api.dto.server.*
import com.example.data.preference.AuthPreference
import com.example.design.FastSearchItem
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
    private val authPreference: AuthPreference,

    private val recentRepo: RecentSearchRepository,
    private val linkuRepo: LinkuRepository,
) : ViewModel() {

    // *닉네임*
    private val _nickname = MutableStateFlow<String?>(null)
    val nickname: StateFlow<String?> = _nickname.asStateFlow()

    // 공유 폴더 리스트
    private val _sharedTopFolders = MutableStateFlow<List<SharedFolderInfo>>(emptyList())
    val sharedTopFolders: StateFlow<List<SharedFolderInfo>> = _sharedTopFolders.asStateFlow()

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

    private val _shareBottomSheetSubFolders = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    val shareBottomSheetSubFolders: StateFlow<List<FolderSimpleInfo>> = _shareBottomSheetSubFolders.asStateFlow()

    // 4-1. 하위 폴더 요청 커서
    private val _subFoldersCursor = MutableStateFlow<String?>(null)
    val subFoldersCursor: StateFlow<String?> = _subFoldersCursor.asStateFlow()

    // 5. 링크 리스트
    private val _links = MutableStateFlow<List<LinkItemInfo>>(emptyList())
    val links: StateFlow<List<LinkItemInfo>> = _links.asStateFlow()

    // 5-1. 분류되지않은 링크 리스트
    private val _notCategorizationLinks = MutableStateFlow<List<LinkItemInfo>>(emptyList())
    val notCategorizationLinks: StateFlow<List<LinkItemInfo>> = _notCategorizationLinks.asStateFlow()

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

                val userInfo = userRepository.getUserInfo(userId)
                _nickname.value = userInfo.nickname
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
                    onGetLinks = { list -> _links.value = list.map { it.copy() } }
                )

                Log.d("FileViewModel", "getLinks try result: ${_links.value}")

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
    fun getFoldersAndNotCategorizationLinks(folderId: Long) {
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

    // 공유 폴더 가져오기
    fun getSharedFolders(){
        Log.d("FileViewModel", "getSharedFolders")

        viewModelScope.launch {

            Log.d("FileViewModel", "getSharedFolders launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "getSharedFolders try")

                _sharedTopFolders.value = folderRepository.getSharedFolders()

                Log.d("FileViewModel", "getSharedFolders try result: ${_sharedTopFolders.value}")
            }catch (e: Exception){
                Log.d("FileViewModel", "getSharedFolders catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "getSharedFolders finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "getSharedFolders return")
    }

    // 공유 폴더 하위 폴더 가져오기
    fun getSharedBottomFolders(sharedFolder: SharedFolderInfo){
        Log.d("FileViewModel", "getSharedBottomFolders")

        viewModelScope.launch {
            Log.d("FileViewModel", "getSharedBottomFolders launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "getSharedBottomFolders try")

                _sharedBottomFolders.value = sharedFolder.folders

                Log.d("FileViewModel", "getSharedBottomFolders try result: ${_sharedBottomFolders.value}")
            }catch (e: Exception){
                Log.d("FileViewModel", "getSharedBottomFolders catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "getSharedBottomFolders finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "getSharedBottomFolders return")
    }
    // ---------- get method ----------

    // ---------- fetch method ----------
    // 외부로 소분류 폴더들을 반환하는 메소드
    fun fetchSubfolders(parentFolderId: Long){
        Log.d("FileViewModel", "fetchSubfolders")

        viewModelScope.launch {
            Log.d("FileViewModel", "fetchSubfolders launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "fetchSubfolders try")

                _shareBottomSheetSubFolders.value = folderRepository.getSubfolders(parentFolderId)

                Log.d("FileViewModel", "fetchSubfolders try result: $_shareBottomSheetSubFolders")

            }catch (e: Exception){
                Log.d("FileViewModel", "fetchSubfolders catch: $e.message")

                _errorMessage.value = e.message
            }
            finally {
                Log.d("FileViewModel", "fetchSubfolders finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "fetchSubfolders return")
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

                _subFolders.value = _subFolders.value.toMutableList().apply {
                    add(newFolder)
                }

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
    fun updateSubfolder(folderId: Long, folderName: String) {
        Log.d("FileViewModel", "updateSubfolder")

        viewModelScope.launch {
            Log.d("FileViewModel", "updateSubfolder launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "updateSubfolder try")

                val normalized = folderName.trim()
                require(normalized.isNotEmpty()) { "폴더 이름은 비어 있을 수 없습니다." }

                val snapshot = _subFolders.value
                if (snapshot.any { it.folderId != folderId && it.folderName.equals(normalized, ignoreCase = true) }) {
                    throw SameNameException()
                }

                folderRepository.updateSubfolder(folderId, normalized)
                Log.d("FileViewModel", "updateSubfolder try result")

                _subFolders.update { list ->
                    if (list.any { it.folderId != folderId && it.folderName.equals(normalized, ignoreCase = true) }) {
                        throw SameNameException()
                    }
                    list.map { folder ->
                        if (folder.folderId == folderId) folder.copy(folderName = normalized) else folder
                    }
                }
            } catch (e:SameNameException){
                Log.d("FileViewModel", "updateSubfolder catch: ${e.message}")

                throw e
            } catch (e: Exception) {
                Log.d("FileViewModel", "updateSubfolder catch: ${e.message}")
                _errorMessage.value = e.message
            } finally {
                Log.d("FileViewModel", "updateSubfolder finally")
                stopLoading()
            }
        }
        Log.d("FileViewModel", "updateSubfolder return")
    }


    // 링크 소분류
    fun updateLinkFolder(link: LinkItemInfo, folderId: Long){
        Log.d("FileViewModel", "updateLinkFolder")

        viewModelScope.launch {
            Log.d("FileViewModel", "updateLinkFolder launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "updateLinkFolder try")

                folderRepository.updateLinkFolder(link, folderId)

                _links.value = _links.value.toMutableList().apply {
                    add(link)
                }

                _notCategorizationLinks.value = _notCategorizationLinks.value.filter { it.linkuId != link.linkuId }

                Log.d("FileViewModel", "updateLinkFolder try result")
            }catch (e: Exception){
                Log.d("FileViewModel", "updateLinkFolder catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "updateLinkFolder finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "updateLinkFolder return")
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

    // 공유 폴더 삭제
    fun deleteSharedFolder(folderId: Long){
        Log.d("FileViewModel", "deleteSharedFolder")

        viewModelScope.launch {
            Log.d("FileViewModel", "deleteSharedFolder launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "deleteSharedFolder try")

                folderRepository.deleteSharedFolder(folderId)

                _sharedBottomFolders.update {
                    it.filter { it.folderId != folderId }
                }

                Log.d("FileViewModel", "deleteSharedFolder try result")
            }catch (e: Exception){
                Log.d("FileViewModel", "deleteSharedFolder catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "deleteSharedFolder finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "deleteSharedFolder return")
    }

    // ---------- delete method ----------

    // ---------- share method ----------
    // 폴더 공유하기
    fun shareFolder(folderId: Long):String{
        Log.d("FileViewModel", "shareFolder")

        return "linku://open?action=share&folderId=$folderId"
    }

    // 폴더 공유 받기
    fun receiveSharedFolder(folderId: Long){
        Log.d("FileViewModel", "receiveSharedFolder")

        viewModelScope.launch {
            Log.d("FileViewModel", "receiveSharedFolder launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "receiveSharedFolder try")

                val userId = authPreference.userId

                if(userId == null){
                    throw UserIdNullException()
                }

                val result = folderRepository.setFolderViewerPermission(folderId)

                Log.d("FileViewModel", "receiveSharedFolder try result: $result")
            }catch (e: Exception){
                Log.d("FileViewModel", "receiveSharedFolder catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "receiveSharedFolder finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "receiveSharedFolder return")
    }

    // 공개/비공개 전환
    fun changeSharing(folder: FolderSimpleInfo){
        Log.d("FileViewModel", "changeSharing")

        viewModelScope.launch {
            Log.d("FileViewModel", "changeSharing launch")

            startLoading()
            _errorMessage.value = null

            try{
                Log.d("FileViewModel", "changeSharing try")

                val isSharing = folder.isSharing == "share"

                if(!isSharing){
                    folderRepository.setFolderViewerPermission(folder.folderId)

                    _subFolders.update { list ->
                        list.map {
                            if (it.folderId == folder.folderId) {
                                it.copy(isSharing = "share")
                            } else {
                                it
                            }
                        }
                    }

                }else{
                    folderRepository.setFolderPrivatePermission(folder.folderId)

                    _subFolders.update { list ->
                        list.map {
                            if (it.folderId == folder.folderId) {
                                it.copy(isSharing = "private")
                            } else {
                                it
                            }
                        }
                    }
                }

                Log.d("FileViewModel", "changeSharing try result")
            }catch (e: Exception){
                Log.d("FileViewModel", "changeSharing catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "changeSharing finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "changeSharing return")
    }

    // ---------- share method ----------

    // ---------- search method ----------
    // 검색창 탑 시트 가시성 상태
    var searchTopSheetVisible by mutableStateOf(false)
        private set
    fun updateSearchTopSheetVisible(newState: Boolean) {
        Log.d("searchTopSheetVisible", newState.toString())
        searchTopSheetVisible = newState
    }

    // 빠른 링크 검색 목록
    private var _fastSearchItems = MutableStateFlow<List<FastSearchItem>>(emptyList())
    val fastSearchItems: StateFlow<List<FastSearchItem>> = _fastSearchItems.asStateFlow()

    // 빠른 링크 검색
    fun fastSearch(keyword: String){
        Log.d("FileViewModel", "fastSearch")

        viewModelScope.launch{
            Log.d("FileViewModel", "fastSearch launch")

            _errorMessage.value = null
            try{
                Log.d("FileViewModel", "fastSearch try")

                _fastSearchItems.value = linkuRepo.fastSearch(keyword).map{
                    FastSearchItem(
                        title = it.title,
                        url = it.linkUrl
                    )
                }

                Log.d("FileViewModel", "fastSearch try result: ${_fastSearchItems.value}")
            }catch (e: Exception){
                Log.d("FileViewModel", "fastSearch catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "fastSearch finally")
            }
        }
    }

    //최근 검색 목록
    val recentQueryList: StateFlow<List<RecentQuery>> =
        recentRepo.observe(limit = 20)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // 최근 검색 기록 추가
    fun addRecentQuery(query: String) {
        Log.d("FileViewModel", "addRecentQuery")

        viewModelScope.launch {
            Log.d("FileViewModel", "addRecentQuery launch")

            try{
                Log.d("FileViewModel", "addRecentQuery try")

                recentRepo.add(query)
            }catch (e: Exception){
                Log.d("FileViewModel", "addRecentQuery catch: $e.message")
            }finally {
                Log.d("FileViewModel", "addRecentQuery finally")
            }
        }
        Log.d("FileViewModel", "addRecentQuery return")
    }

    // 최근 검색 기록 삭제
    fun removeRecentQuery(query: String) {
        Log.d("FileViewModel", "removeRecentQuery")

        viewModelScope.launch {
            Log.d("FileViewModel", "removeRecentQuery launch")

            try{
                Log.d("FileViewModel", "removeRecentQuery try")

                recentRepo.remove(query)

            }catch (e: Exception){
                Log.d("FileViewModel", "removeRecentQuery catch: $e.message")
            }finally {
                Log.d("FileViewModel", "removeRecentQuery finally")
                }
            }
        Log.d("FileViewModel", "removeRecentQuery return")
    }


    // 최근 검색 기록 전체 삭제
    fun clearRecentQuery() {
        Log.d("FileViewModel", "clearRecentQuery")

        viewModelScope.launch {
            Log.d("FileViewModel", "clearRecentQuery launch")

            try{
                Log.d("FileViewModel", "clearRecentQuery try")

                recentRepo.clear()

            }catch (e: Exception){
                Log.d("FileViewModel", "clearRecentQuery catch: $e.message")
            }finally {
                Log.d("FileViewModel", "clearRecentQuery finally")
            }
            }
        Log.d("FileViewModel", "clearRecentQuery return")
    }
    // ---------- search method ----------

}