package com.linku.file

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.SameNameException
import com.linku.core.error.UserIdNullException
import com.linku.core.model.AiArticle
import com.linku.core.model.CategoryColorList
import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.InvitationInfo
import com.linku.core.model.LinkItemInfo
import com.linku.core.model.LinkResultInfo
import com.linku.core.model.SharedFolderInfo
import com.linku.core.model.search.RecentQuery
import com.linku.core.repository.AIArticleRepository
import com.linku.core.repository.CategoryRepository
import com.linku.core.repository.FolderRepository
import com.linku.core.repository.InvitationRepository
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.RecentSearchRepository
import com.linku.core.repository.UserRepository
import com.linku.data.preference.AuthPreference
import com.linku.data.util.DomainIdMapper
import com.linku.data.util.toCategoryColorStyleMap
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.top.search.FastSearchItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val folderRepository: FolderRepository,
    private val invitationRepository: InvitationRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,

    private val recentRepository: RecentSearchRepository,
    private val linkuRepository: LinkuRepository,

    private val aiArticleRepository: AIArticleRepository,
) : ViewModel() {

    // ---------- field ----------
    // *닉네임*
    private val _nickname = MutableStateFlow<String?>(null)
    val nickname: StateFlow<String?> = _nickname.asStateFlow()

    // 공유 폴더 리스트
    private val _sharedTopFolders = MutableStateFlow<List<SharedFolderInfo>>(emptyList())
    val sharedTopFolders: StateFlow<List<SharedFolderInfo>> = _sharedTopFolders.asStateFlow()

    private val _sharedBottomFolders = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    val sharedBottomFolders: StateFlow<List<FolderSimpleInfo>> = _sharedBottomFolders.asStateFlow()

    private val _invitationInfo = MutableStateFlow<InvitationInfo?>(null)
    val invitationInfo: StateFlow<InvitationInfo?> = _invitationInfo.asStateFlow()

    // 카테고리 리스트
    private val _categoryList = MutableStateFlow<List<CategoryColorList>>(emptyList())
    val categoryList: StateFlow<List<CategoryColorList>> = _categoryList.asStateFlow()

    // 색깔 리스트
    private val _categoryColorMap = MutableStateFlow<Map<String, CategoryColorStyle>>(emptyMap())
    val categoryColorMap: StateFlow<Map<String, CategoryColorStyle>> = _categoryColorMap.asStateFlow()

    // 내 폴더 트리
    private val _folderTree = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    val folderTree: StateFlow<List<FolderSimpleInfo>> = _folderTree.asStateFlow()

    // 상위 폴더 리스트
    private val _parentFolders = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    // 매번 북마크 우선 정렬
    val parentFolders: StateFlow<List<FolderSimpleInfo>> =
        _parentFolders.asStateFlow()
            .map { list ->
                list.sortedByDescending { it.isBookmarked }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // 하위 폴더 리스트
    private val _subFolders = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    val subFolders: StateFlow<List<FolderSimpleInfo>> = _subFolders.asStateFlow()

    private val _shareBottomSheetSubFolders = MutableStateFlow<List<FolderSimpleInfo>>(emptyList())
    val shareBottomSheetSubFolders: StateFlow<List<FolderSimpleInfo>> = _shareBottomSheetSubFolders.asStateFlow()

    // 하위 폴더 요청 커서
    private val _subFoldersCursor = MutableStateFlow<String?>(null)
    val subFoldersCursor: StateFlow<String?> = _subFoldersCursor.asStateFlow()

    // 링크 리스트
    private val _links = MutableStateFlow<List<LinkItemInfo>>(emptyList())
    val links: StateFlow<List<LinkItemInfo>> = _links.asStateFlow()

    // 분류되지않은 링크 리스트
    private val _notCategorizationLinks = MutableStateFlow<List<LinkItemInfo>>(emptyList())
    val notCategorizationLinks: StateFlow<List<LinkItemInfo>> = _notCategorizationLinks.asStateFlow()

    // 누른 링크 정보
    private val _linkDetail = MutableStateFlow<LinkResultInfo?>(null)
    val linkDetail: StateFlow<LinkResultInfo?> = _linkDetail.asStateFlow()

    private val _aiArticleDetail = MutableStateFlow<AiArticle?>(null)
    val aiArticleDetail: StateFlow<AiArticle?> = _aiArticleDetail.asStateFlow()

    // 링크 클릭 콜백
    var onLinkClick: ((Long) -> Unit)? = null
    fun registeronLinkClick(callback: (Long) -> Unit) {
        onLinkClick = callback
    }

    // 로딩/에러 상태
    private val _loadingCount = MutableStateFlow(0)

    fun startLoading() {
        _loadingCount.update { it + 1 }
    }

    fun stopLoading() {
        _loadingCount.update { count -> (count - 1).coerceAtLeast(0) }
    }

    // 로딩중
    private val _loading = _loadingCount
        .map { it > 0 } // 카운트가 0보다 크면 true
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val loading: StateFlow<Boolean> = _loading

    // 에러 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ==== [로딩 상태: 상세/AI 분리] ====
    private val _isLoadingLinkDetail = MutableStateFlow(false)
    val isLoadingLinkDetail: StateFlow<Boolean> = _isLoadingLinkDetail.asStateFlow()

    private val _isLoadingAiArticle = MutableStateFlow(false)
    val isLoadingAiArticle: StateFlow<Boolean> = _isLoadingAiArticle.asStateFlow()

    // ==== [AI 진행률] ====
    private val _aiProgress = MutableStateFlow(0f)
    val aiProgress: StateFlow<Float> = _aiProgress.asStateFlow()

    private var aiJob: Job? = null
    private var aiProgressJob: Job? = null
    // ---------- field ----------

//    // ==== [카테고리 색상 불러오기 - HomeVM과 이름을 맞춘 alias] ====
//    fun loadCategoryColors() = getCategoryColor()
//
//    // ==== [상세 불러오기 - AI는 자동 호출하지 않고 초기화만] ====
//    fun loadLinkDetail(linkuId: Long) {
//        Log.d("FileVM", "loadLinkDetail 시작 -> linkuId=$linkuId")
//        viewModelScope.launch {
//            _isLoadingLinkDetail.value = true
//            try {
//                val info = linkuRepository.getLinkDetail(linkuId)
//                Log.d("FileVM", "상세 응답 -> $info")
//                _linkDetail.value = info
//                _aiArticleDetail.value = null // 상세 갱신 시 AI 요약 초기화
//            } catch (e: Exception) {
//                Log.e("FileVM", "상세 조회 실패", e)
//                _linkDetail.value = null
//                _errorMessage.value = e.message
//            } finally {
//                _isLoadingLinkDetail.value = false
//            }
//        }
//    }

    // ==== [AI 요약 호출] ====
    fun loadAiArticle(linkuId: Long) {
        Log.d("FileVM", "loadAiArticle 진입: linkuId=$linkuId, 현재 isLoading=${_isLoadingAiArticle.value}")
        if (_isLoadingAiArticle.value) {
            Log.d("FileVM", "이미 로딩중 → 리턴")
            return
        }

        _isLoadingAiArticle.value = true
        _aiProgress.value = 0.1f

        // 진행률 타이머 (0.85f까지 서서히 상승)
        aiProgressJob?.cancel()
        aiProgressJob = viewModelScope.launch {
            val cap = 0.85f
            while (isActive && _aiProgress.value < cap) {
                delay(100)
                _aiProgress.value = (_aiProgress.value + 0.02f).coerceAtMost(cap)
            }
        }

        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            runCatching { aiArticleRepository.getAiArticle(linkuId) }
                .onSuccess { article ->
                    Log.d("FileVM", "AI API 성공: $article")
                    _aiArticleDetail.value = article
                }
                .onFailure { e ->
                    Log.e("FileVM", "AI API 실패", e)
                    _aiArticleDetail.value = null
                    _errorMessage.value = e.message
                }

            // 완료 처리
            aiProgressJob?.cancel()
            _aiProgress.value = 1f
            _isLoadingAiArticle.value = false

            // 100% 잠깐 보여준 뒤 초기화(선택)
            launch {
                delay(300)
                _aiProgress.value = 0f
            }
        }
    }

    // ==== [AI 작업 취소] ====
    fun cancelAiArticleJob() {
        Log.d("FileVM", "cancelAiArticleJob 호출")
        aiJob?.cancel()
        aiProgressJob?.cancel()
        _isLoadingAiArticle.value = false
        _aiProgress.value = 0f
    }

    // ==== [링크 수정] ====
    private var isUpdatingLink = false
    fun updateLink(
        title: String,
        memo: String?,
        categoryId: Long?,
        emotionId: Long?,
        situationId: Long? = null,  // TODO: 도메인 모델과 DTO 수정하면서 상황이 추가가 되어 넣었습니다. 지민님께서 확인 후 수정 부탁드립니다.
        onSucceed: (LinkResultInfo) -> Unit = {},
        onFailed: (Throwable) -> Unit = {},
    ) {
        val current = _linkDetail.value ?: run {
            onFailed(IllegalStateException("링크 상세가 없습니다."))
            return
        }
        if (isUpdatingLink) return

        val fixedLinkuId = current.linkuId
        val fixedLinku   = current.linku

        // domainId 계산 (URL/도메인 기반)
        val computedDomainId = DomainIdMapper.resolve(
            url = fixedLinku,
            domain = current.domain
        )

        viewModelScope.launch {
            isUpdatingLink = true
            try {
                val updated = linkuRepository.updateLink(
                    linkuId    = fixedLinkuId,
                    categoryId = categoryId ?: (current.categoryId ?: 0L),
                    linku      = fixedLinku,                     // 서버 URL 고정
                    memo       = memo,                           // null/"" 그대로
                    emotionId  = emotionId ?: (current.emotionId ?: 0L),
                    situationId = situationId ?: current.situationId ?: 0L,    // TODO: 도메인 모델과 DTO 수정하면서 상황이 추가가 되어 넣었습니다. 지민님께서 확인 후 수정 부탁드립니다.
                    domainId   = computedDomainId,
                    title      = title.ifBlank { current.title } // 빈 제목이면 기존 유지
                )
                _linkDetail.value = updated
                onSucceed(updated)
            } catch (e: Throwable) {
                onFailed(e)
                _errorMessage.value = e.message
            } finally {
                isUpdatingLink = false
            }
        }
    }

    // ---------- get method ----------
    fun setLinkDetail(linkuId: Long){
        Log.d("FileViewModel", "setLinkDetail")

        viewModelScope.launch {
            Log.d("FileViewModel", "setLinkDetail launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "상세 요청 -> linkuId = $linkuId")

                val userId = authPreference.getUserId()

                if(userId == null){
                    throw UserIdNullException()
                }

                // 상세만 로드 (AI 자동 호출 제거)
                val detail = linkuRepository.getLinkDetailWithShared(userId, linkuId)
                Log.d("FileViewModel", "상세 응답 -> $detail")
                _linkDetail.value = detail

                // 이전 AI 결과는 초기화 (버튼 눌렀을 때만 따로 호출)
                _aiArticleDetail.value = null

            } catch (e: Exception) {
                Log.e("FileViewModel", "상세 조회 실패", e)
                _linkDetail.value = null
                _errorMessage.value = e.message
            } finally {
                Log.d("FileViewModel", "setLinkDetail finally")
                stopLoading()
            }
        }
        Log.d("FileViewModel", "setLinkDetail return")
    }

    // 유저 닉네임 가져오기
    fun loadNickname() {
        Log.d("FileViewModel", "loadNickname")

        viewModelScope.launch {
            Log.d("FileViewModel", "loadNickname launch")

            startLoading()
            _errorMessage.value = null

            try{
                val userId = authPreference.getUserId() ?: throw UserIdNullException()

                val userInfo = userRepository.getUserInfo(userId).getOrThrow()
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

                Log.d("FileViewModel", "getNotCategorizationLinks try result: ${_subFolders.value}")

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

    // 폴더 트리 조회
    fun getFolderTree(){
        Log.d("FileViewModel", "getFolderTree")

        viewModelScope.launch {
            Log.d("FileViewModel", "getFolderTree launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "getFolderTree try")

                _folderTree.value = folderRepository.getMyFolderTree()

                Log.d("FileViewModel", "getFolderTree try result: ${folderTree.value}")
            } catch (e: Exception) {
                Log.d("FileViewModel", "getFolderTree catch: $e.message")

                _errorMessage.value = e.message
            } finally {
                Log.d("FileViewModel", "getFolderTree finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "getFolderTree return")
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
    // 소분류 생성
    fun createSubfolder(
        parentFolderId: Long,
        folderName: String
    ) = viewModelScope.async {
        Log.d("FileViewModel", "createSubfolderAsync start")

        startLoading()
        _errorMessage.value = null

        try {
            val normalized = folderName.trim()
            require(normalized.isNotEmpty()) { "폴더 이름은 비어 있을 수 없습니다." }

            // 1) 로컬 선제 중복 체크 (같은 부모 폴더 내에서만 비교)
            val snapshot = _subFolders.value
            if (snapshot.any {
                    it.parentFolderId == parentFolderId &&
                            it.folderName.equals(normalized, ignoreCase = true)
                }) {
                throw SameNameException()
            }

            // 2) 서버 생성 (여기서 409 가능)
            val created = folderRepository.createSubfolder(parentFolderId, normalized)

            // 3) 로컬 상태 반영 (원자적 업데이트)
            val newFolder = FolderSimpleInfo(
                folderId = created.folderId,
                folderName = created.folderName,
                parentFolderId = created.parentFolderId,
                isBookmarked = false
            )

            _subFolders.update { list -> list + newFolder }

            Log.d("FileViewModel", "createSubfolderAsync success: $newFolder")

            // Unit 반환 → await() 성공 시 아무 것도 안 던짐
        } catch (e: SameNameException) {
            Log.d("FileViewModel", "createSubfolderAsync SameName: ${e.message}")
            // ✅ 로컬 중복은 호출자가 처리하게 전파
            throw e

        } catch (e: HttpException) {
            Log.d("FileViewModel", "createSubfolderAsync HttpException: code=${e.code()} msg=${e.message()}")
            if (e.code() == 409) {
                // ✅ 409만 상위로 전파 → 호출자가 await()에서 잡음
                throw e
            } else {
                // 그 외 HTTP 에러는 내부 처리
                _errorMessage.value = e.message()
            }

        } catch (e: Exception) {
            Log.d("FileViewModel", "createSubfolderAsync Exception: ${e.message}")
            _errorMessage.value = e.message

        } finally {
            stopLoading()
            Log.d("FileViewModel", "createSubfolderAsync finally")
        }
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
    fun updateSubfolder(folderId: Long, folderName: String) = viewModelScope.async {
        Log.d("FileViewModel", "updateSubfolderAsync start")
        startLoading()
        _errorMessage.value = null

        try {
            val normalized = folderName.trim()
            require(normalized.isNotEmpty()) { "폴더 이름은 비어 있을 수 없습니다." }

            // 1) 로컬 선제 체크 (UX 개선용)
            val snapshot = _subFolders.value
            if (snapshot.any { it.folderId != folderId && it.folderName.equals(normalized, ignoreCase = true) }) {
                throw SameNameException()
            }

            // 2) 서버 변경 (여기서 409 가능)
            folderRepository.updateSubfolder(folderId, normalized)

            // 3) 로컬 상태 반영 + 마지막 방어선
            _subFolders.update { list ->
                if (list.any { it.folderId != folderId && it.folderName.equals(normalized, ignoreCase = true) }) {
                    throw SameNameException()
                }
                list.map { f -> if (f.folderId == folderId) f.copy(folderName = normalized) else f }
            }

            Log.d("FileViewModel", "updateSubfolderAsync success")
            // 반환값이 없다면 Unit로 끝 (await() 성공)
        } catch (e: SameNameException) {
            Log.d("FileViewModel", "updateSubfolderAsync SameNameException: ${e.message}")
            // ✅ 비즈니스 예외는 상위에서 처리하도록 그대로 전파
            throw e
        } catch (e: retrofit2.HttpException) {
            Log.d("FileViewModel", "updateSubfolderAsync HttpException: code=${e.code()} msg=${e.message()}")
            if (e.code() == 409) {
                // ✅ 409만 상위로 전파 (호출자가 await()에서 catch)
                throw SameNameException()
            } else {
                // 그 외 HTTP 에러는 내부에서 메시지 처리 (전파하지 않음)
                _errorMessage.value = e.message()
            }
        } catch (e: Exception) {
            Log.d("FileViewModel", "updateSubfolderAsync Exception: ${e.message}")
            _errorMessage.value = e.message
            // 필요하면 전파하지 않고 내부 처리로 끝냄
        } finally {
            stopLoading()
            Log.d("FileViewModel", "updateSubfolderAsync finally")
        }
    }

    // 링크 소분류
    suspend fun updateLinkFolder(link: LinkItemInfo, folderId: Long){
        Log.d("FileViewModel", "updateLinkFolder")

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

        Log.d("FileViewModel", "updateLinkFolder return")
    }
    // ---------- update method ----------

    // ---------- delete method ----------
    // 소분류 폴더 삭제
    fun deleteSubfolder(folderId: Long) {
        Log.d("FileViewModel", "deleteSubfolder")

        viewModelScope.launch {
            Log.d("FileViewModel", "deleteSubfolder launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "deleteSubfolder try")

                folderRepository.deleteSubfolder(folderId)
                _subFolders.value = _subFolders.value.filterIndexed { i, folder -> folder.folderId != folderId }

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

    fun deleteLink(linkuId: Long){
        Log.d("FileViewModel", "deleteLink")

        startLoading()
        _errorMessage.value = null

        viewModelScope.launch{
            Log.d("FileViewModel", "deleteLink launch")

            try {
                Log.d("FileViewModel", "deleteLink try")

                folderRepository.deleteLink(linkuId)

                _links.update {
                    it.filter { it.linkuId != linkuId }
                }

                Log.d("FileViewModel", "deleteLink try result")

            } catch (e: Exception) {
                Log.d("FileViewModel", "deleteLink catch: $e.message")

                _errorMessage.value = e.message

            } finally {
                Log.d("FileViewModel", "deleteLink finally")

                stopLoading()
            }
        }

        Log.d("FileViewModel", "deleteLink return")
    }


    fun deleteNotCategorizationLink(linkuId: Long) {
        Log.d("FileViewModel", "deleteLink")

        startLoading()
        _errorMessage.value = null

        viewModelScope.launch {
            Log.d("FileViewModel", "deleteLink launch")

            try {
                Log.d("FileViewModel", "deleteLink try")

                folderRepository.deleteLink(linkuId)

                _notCategorizationLinks.update {
                    it.filter { it.linkuId != linkuId }
                }

                Log.d("FileViewModel", "deleteLink try result")

            } catch (e: Exception) {
                Log.d("FileViewModel", "deleteLink catch: $e.message")

                _errorMessage.value = e.message

            } finally {
                Log.d("FileViewModel", "deleteLink finally")

                stopLoading()
            }
        }
    }
    // ---------- delete method ----------

    // ---------- share method ----------
    // 폴더 공유하기
    fun makeInvitationLink(folderId: Long): String? {
        Log.d("FileViewModel", "makeInvitationLink")

        var link: String? = null

        viewModelScope.launch {
            Log.d("FileViewModel", "makeInvitationLink launch")

            startLoading()
            _errorMessage.value = null

            try {

                Log.d("FileViewModel", "makeInvitationLink try")

                link = folderRepository.makeInvitationLink(folderId)

                Log.d("FileViewModel", "makeInvitationLink try result: $link")

            } catch (e: Exception) {
                Log.e("FileViewModel", "makeInvitationLink catch: $e.message")
                _errorMessage.value = e.message
                link = null

            } finally {
                Log.d("FileViewModel", "makeInvitationLink finally")
                stopLoading()
            }
        }

        Log.d("FileViewModel", "makeInvitationLink return")

        return link
    }

    fun createInvitationLink(
        folderId: Long,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit = {}
    ) {
        Log.d("FileViewModel", "createInvitationLink")

        viewModelScope.launch {
            Log.d("FileViewModel", "createInvitationLink launch")

            startLoading()
            _errorMessage.value = null

            try {
                val link = folderRepository.makeInvitationLink(folderId)
                onSuccess(link)

                Log.d("FileViewModel", "createInvitationLink result: $link")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("FileViewModel", "createInvitationLink catch: $e")
                _errorMessage.value = e.message
                onFailure(e)
            } finally {
                stopLoading()
            }
        }
    }

    fun deactivateInvitationLink(folderId: Long) {
        Log.d("FileViewModel", "deactivateInvitationLink")

        viewModelScope.launch {
            startLoading()
            _errorMessage.value = null

            try {
                folderRepository.deactivateInvitationLink(folderId)
            } catch (e: Exception) {
                Log.e("FileViewModel", "deactivateInvitationLink catch: $e")
                _errorMessage.value = e.message
            } finally {
                stopLoading()
            }
        }
    }

    fun getInvitationInfo(token: String) {
        Log.d("FileViewModel", "getInvitationInfo")

        viewModelScope.launch {
            startLoading()
            _errorMessage.value = null

            try {
                val userId = authPreference.getUserId()

                if (userId == null) {
                    throw UserIdNullException()
                }

                _invitationInfo.value = invitationRepository.getInvitationInfo(token)
            } catch (e: Exception) {
                Log.e("FileViewModel", "getInvitationInfo catch: $e")
                _errorMessage.value = e.message
            } finally {
                stopLoading()
            }
        }
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

                val userId = authPreference.getUserId()

                if(userId == null){
                    throw UserIdNullException()
                }

                folderRepository.setFolderViewerPermission(folderId)

                _sharedTopFolders.value = folderRepository.getSharedFolders()

                Log.d("FileViewModel", "receiveSharedFolder well done")
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

    fun receiveSharedFolderInvitation(token: String) {
        Log.d("FileViewModel", "receiveSharedFolderInvitation")

        viewModelScope.launch {
            Log.d("FileViewModel", "receiveSharedFolderInvitation launch")

            startLoading()
            _errorMessage.value = null

            try {
                val userId = authPreference.getUserId()

                if (userId == null) {
                    throw UserIdNullException()
                }

                invitationRepository.acceptInvitation(token)
                _sharedTopFolders.value = folderRepository.getSharedFolders()

                Log.d("FileViewModel", "receiveSharedFolderInvitation well done")
            } catch (e: Exception) {
                Log.d("FileViewModel", "receiveSharedFolderInvitation catch: $e")
                _errorMessage.value = e.message
            } finally {
                stopLoading()
            }
        }
    }
    // 공개 전환
    fun folderToShare(folder: FolderSimpleInfo){
        Log.d("FileViewModel", "folderToShare")

        viewModelScope.launch {
            Log.d("FileViewModel", "folderToShare launch")

            startLoading()
            _errorMessage.value = null

            try {
                Log.d("FileViewModel", "folderToShare try")

                val isPrivate = folder.isSharing == "private"

                if (isPrivate) {
                    Log.d("FileViewModel", "folderToShare isPrivate true")

                    //folderRepository.setFolderPublicPermission(folder.folderId)

                    _subFolders.update { list ->
                        list.map {
                            if (it.folderId == folder.folderId) {
                                it.copy(isSharing = "share")
                            } else {
                                it
                            }
                        }
                    }
                }
                Log.d("FileViewModel", "folderToShare try result")
            } catch (e: Exception) {
                Log.d("FileViewModel", "folderToShare catch: $e.message")

                _errorMessage.value = e.message
            } finally {
                Log.d("FileViewModel", "folderToShare finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "folderToShare return")

    }
    // 비공개 전환
    fun folderToPrivate(folder: FolderSimpleInfo){
        Log.d("FileViewModel", "folderToPrivate")

        viewModelScope.launch {
            Log.d("FileViewModel", "folderToPrivate launch")

            startLoading()
            _errorMessage.value = null

            try{
                Log.d("FileViewModel", "folderToPrivate try")

                val isSharing = folder.isSharing == "share"

                if(isSharing){
                    Log.d("FileViewModel", "folderToPrivate isSharing true")

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

                Log.d("FileViewModel", "folderToPrivate try result")
            }catch (e: Exception){
                Log.d("FileViewModel", "folderToPrivate catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "folderToPrivate finally")

                stopLoading()
            }
        }
        Log.d("FileViewModel", "folderToPrivate return")
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

                _fastSearchItems.value = linkuRepository.fastSearch(keyword).map{
                    FastSearchItem(
                        id = it.linkuId,
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
        recentRepository.observe(limit = 20)
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

                recentRepository.add(query)
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

                recentRepository.remove(query)

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

                recentRepository.clear()

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
