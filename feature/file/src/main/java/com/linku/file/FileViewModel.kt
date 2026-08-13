package com.linku.file

import android.net.Uri
import android.util.Log
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
import com.linku.core.model.ParentFolderSort
import com.linku.core.model.SharedFolderInfo
import com.linku.core.repository.AIArticleRepository
import com.linku.core.repository.CategoryRepository
import com.linku.core.repository.FolderRepository
import com.linku.core.repository.InvitationRepository
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.UserRepository
import com.linku.core.usecase.AcceptSharedFolderInvitationResult
import com.linku.core.usecase.AcceptSharedFolderInvitationUseCase
import com.linku.data.preference.AuthPreference
import com.linku.data.util.toCategoryColorStyleMap
import com.linku.design.theme.color.CategoryColorStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

/**
 * 파일 화면에서 폴더, 링크, 공유 상태와 관련 비동기 작업을 관리하는 ViewModel입니다.
 *
 * @property acceptSharedFolderInvitationUseCase 초대 토큰을 사용해 공유 폴더 초대를 수락하고
 * 최신 공유 폴더 목록을 함께 반환하는 UseCase입니다.
 */
@HiltViewModel
class FileViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val folderRepository: FolderRepository,
    private val invitationRepository: InvitationRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,

    private val linkuRepository: LinkuRepository,

    private val aiArticleRepository: AIArticleRepository,
    private val acceptSharedFolderInvitationUseCase: AcceptSharedFolderInvitationUseCase,
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

    /** 서버가 정렬한 순서를 각 그룹 안에서 유지하면서 북마크 폴더를 먼저 노출합니다. */
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

    private val _parentFolderSort = MutableStateFlow(ParentFolderSort.NAME)

    /** 현재 상위 폴더 목록과 기기에 적용된 정렬 기준입니다. */
    val parentFolderSort: StateFlow<ParentFolderSort> = _parentFolderSort.asStateFlow()

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

    /** 새 공유 바텀시트 조회가 시작될 때 이전 폴더 트리 응답이 상태를 덮지 않도록 추적합니다. */
    private var folderTreeLoadJob: Job? = null

    /** 새 링크 생성 요청이 시작될 때 이전 결과가 현재 바텀시트 상태를 덮지 않도록 추적합니다. */
    private var invitationLinkCreateJob: Job? = null

    /** 진행 중인 상위 폴더 정렬 조회 작업입니다. */
    private var parentFoldersLoadJob: Job? = null

    /** 화면에 적용된 정렬 기준을 순서대로 저장하는 작업입니다. */
    private var parentFolderSortSaveJob: Job? = null

    /** 연속 선택 시 마지막으로 요청된 정렬 기준입니다. */
    private var requestedParentFolderSort = ParentFolderSort.NAME

    /** DataStore에 마지막으로 기록된 정렬 기준입니다. */
    private var persistedParentFolderSort = ParentFolderSort.NAME

    /**
     * 현재 진행 중인 공유 폴더 초대 수락 요청입니다.
     *
     * 새 요청이 시작되거나 공유 폴더 상태가 초기화되면 기존 요청을 취소하기 위해 사용합니다.
     */
    private var receiveSharedFolderInvitationJob: Deferred<AcceptSharedFolderInvitationResult>? = null
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

        viewModelScope.launch {
            isUpdatingLink = true
            try {
                val updated = linkuRepository.updateLink(
                    linkuId = fixedLinkuId,
                    image = null,
                    memo = memo,
                    emotionId = emotionId ?: current.emotionId,
                    situationId = situationId ?: current.situationId,
                    categoryId = categoryId ?: current.categoryId,
                    title = title.ifBlank { current.title },
                )
                _linkDetail.value = updated
                onSucceed(updated)
            } catch (e: CancellationException) {
                throw e
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

    /**
     * 선택한 서버 정렬 쿼리로 상위 폴더를 조회해 화면 목록을 교체합니다.
     *
     * @param sort 조회에 적용할 상위 폴더 정렬 기준입니다.
     * @return 목록 교체에 성공했으면 `true`, 기존 목록을 유지했으면 `false`입니다.
     * @throws CancellationException 더 최신 정렬 요청으로 현재 조회가 취소된 경우 발생합니다.
     */
    private suspend fun loadParentFolders(sort: ParentFolderSort): Boolean {
        Log.d("FileViewModel", "loadParentFolders sort: ${sort.query}")

        startLoading()
        _errorMessage.value = null

        try {
            val folders = folderRepository.getParentFoldersBySort(sort)
            currentCoroutineContext().ensureActive()
            _parentFolders.value = folders
            return true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("FileViewModel", "loadParentFolders catch", e)
            _errorMessage.value = e.message
            return false
        } finally {
            stopLoading()
        }
    }

    /**
     * 파일 화면 진입 시 기기에 저장된 정렬 기준을 먼저 읽고 상위 폴더를 조회합니다.
     *
     * 저장값을 읽기 전에 기본 정렬로 요청하지 않으므로 화면 진입마다 API는 한 번만 호출됩니다.
     */
    fun loadParentFoldersBySavedSort() {
        val pendingSaveJob = parentFolderSortSaveJob
        parentFoldersLoadJob?.cancel()
        parentFoldersLoadJob = viewModelScope.launch {
            try {
                pendingSaveJob?.join()
                val savedSort = folderRepository.parentFolderSort.first()
                persistedParentFolderSort = savedSort
                requestedParentFolderSort = savedSort
                if (loadParentFolders(savedSort)) {
                    _parentFolderSort.value = savedSort
                } else if (requestedParentFolderSort == savedSort) {
                    requestedParentFolderSort = _parentFolderSort.value
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("FileViewModel", "loadParentFoldersBySavedSort catch", e)
                _errorMessage.value = e.message
            }
        }
    }

    /**
     * 화면에 적용된 정렬 기준을 이전 저장 작업 다음에 이어서 기기에 기록합니다.
     *
     * 네트워크 요청과 저장 작업의 생명주기를 분리해 이후 요청이 실패하더라도 이미 적용된
     * 정렬 기준의 저장이 취소되지 않도록 합니다.
     *
     * @param sort 화면 목록에 성공적으로 적용된 정렬 기준입니다.
     */
    private fun persistParentFolderSort(sort: ParentFolderSort) {
        val previousSaveJob = parentFolderSortSaveJob
        parentFolderSortSaveJob = viewModelScope.launch {
            previousSaveJob?.join()

            try {
                folderRepository.setParentFolderSort(sort)
                persistedParentFolderSort = sort
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("FileViewModel", "persistParentFolderSort catch", e)
                if (
                    _parentFolderSort.value == sort &&
                    requestedParentFolderSort == sort
                ) {
                    _errorMessage.value = e.message
                }
            }
        }
    }

    /**
     * 선택한 기준으로 상위 폴더를 조회하고, 성공한 기준을 기기에 저장합니다.
     *
     * API 요청이 실패하면 기존 목록과 적용 중인 정렬 표시는 유지합니다. 새 선택이 들어오면
     * 진행 중인 이전 요청을 취소해 오래된 응답이 화면 상태를 덮어쓰지 않도록 합니다.
     *
     * @param sort 사용자가 선택한 상위 폴더 정렬 기준입니다.
     */
    fun updateParentFolderSort(sort: ParentFolderSort) {
        if (sort == _parentFolderSort.value) {
            if (sort != requestedParentFolderSort) {
                requestedParentFolderSort = sort
                parentFoldersLoadJob?.cancel()
            }
            if (
                sort != persistedParentFolderSort &&
                parentFolderSortSaveJob?.isActive != true
            ) {
                parentFoldersLoadJob?.cancel()
                _errorMessage.value = null
                persistParentFolderSort(sort)
            }
            return
        }

        if (sort == requestedParentFolderSort) return

        requestedParentFolderSort = sort
        parentFoldersLoadJob?.cancel()

        parentFoldersLoadJob = viewModelScope.launch {
            if (loadParentFolders(sort)) {
                _parentFolderSort.value = sort
                persistParentFolderSort(sort)
            } else if (requestedParentFolderSort == sort) {
                requestedParentFolderSort = _parentFolderSort.value
            }
        }
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

    fun resetSharedFolderState() {
        receiveSharedFolderInvitationJob?.cancel()
        receiveSharedFolderInvitationJob = null
        _sharedTopFolders.value = emptyList()
        _sharedBottomFolders.value = emptyList()
        _invitationInfo.value = null
        _links.value = emptyList()
        _notCategorizationLinks.value = emptyList()
        _subFoldersCursor.value = null
        _errorMessage.value = null
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

    /**
     * 공유 바텀시트에 표시할 내 폴더 트리를 갱신합니다.
     *
     * 조회 진행·실패 상태는 이 요청을 시작한 바텀시트가 표시하므로 파일 화면 전체의 로딩·오류
     * 상태는 변경하지 않습니다. 요청 생명주기만 [viewModelScope]이 소유합니다.
     *
     * @param onSuccess 폴더 트리 갱신이 완료되었을 때 결과와 함께 호출되는 콜백입니다.
     * @param onFailure 폴더 트리 조회가 실패했을 때 원인과 함께 호출되는 콜백입니다.
     */
    fun getFolderTree(
        onSuccess: (List<FolderSimpleInfo>) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        Log.d("FileViewModel", "getFolderTree")

        folderTreeLoadJob?.cancel()
        val request = viewModelScope.launch {
            Log.d("FileViewModel", "getFolderTree launch")

            try {
                Log.d("FileViewModel", "getFolderTree try")

                val loadedFolderTree = folderRepository.getMyFolderTree()
                _folderTree.value = loadedFolderTree
                onSuccess(loadedFolderTree)

                Log.d("FileViewModel", "getFolderTree try result: ${folderTree.value}")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.d("FileViewModel", "getFolderTree catch: $e.message")
                onFailure(e)
            }
        }
        folderTreeLoadJob = request
        request.invokeOnCompletion {
            if (folderTreeLoadJob === request) {
                folderTreeLoadJob = null
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
    /**
     * 중분류 폴더의 대표 색상을 수정합니다.
     *
     * @param onFinished 성공 또는 실패로 요청 처리가 끝난 뒤 호출되는 완료 콜백입니다.
     */
    fun updateCategoryColor(
        categoryName: String,
        colorId: Long,
        colorStyle: CategoryColorStyle,
        onFinished: () -> Unit = {},
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

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception){
                Log.d("FileViewModel", "updateCategoryColor catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "updateCategoryColor finally")

                stopLoading()
                onFinished()
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
            }

            Log.d("FileViewModel", "updateBookmark end")
        }
        Log.d("FileViewModel", "updateBookmark return")

        return result
    }

    /**
     * 소분류 폴더 이름을 수정하고 호출자가 결과를 기다릴 수 있는 작업을 반환합니다.
     *
     * [onFinished]는 요청을 소유한 [viewModelScope]에서 작업이 종료될 때 호출되므로,
     * 화면의 대기 코루틴이 먼저 취소되더라도 실제 요청 종료 시점과 어긋나지 않습니다.
     *
     * @param onFinished 요청 작업이 종료될 때 호출되는 완료 콜백입니다.
     */
    fun updateSubfolder(
        folderId: Long,
        folderName: String,
        onFinished: () -> Unit = {},
    ) = viewModelScope.async {
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d("FileViewModel", "updateSubfolderAsync Exception: ${e.message}")
            _errorMessage.value = e.message
            // 필요하면 전파하지 않고 내부 처리로 끝냄
        } finally {
            stopLoading()
            onFinished()
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
    /**
     * 소분류 폴더를 삭제합니다.
     *
     * @param onFinished 성공 또는 실패로 요청 처리가 끝난 뒤 호출되는 완료 콜백입니다.
     */
    fun deleteSubfolder(
        folderId: Long,
        onFinished: () -> Unit = {},
    ) {
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

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.d("FileViewModel", "deleteSubfolder catch: $e.message")

                _errorMessage.value = e.message

            } finally {
                Log.d("FileViewModel", "deleteSubfolder finally")

                stopLoading()
                onFinished()
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
    /**
     * 서버가 반환한 초대 토큰 또는 소문자 `http://`·`https://` 접두사의 초대 링크를
     * 앱에서 공유할 HTTPS 링크로 정규화합니다.
     *
     * 최종 링크는 [BuildConfig.SERVER_HOST]의 `/open` 경로에 URL 인코딩된 `token`
     * 쿼리 파라미터를 포함합니다.
     *
     * @param tokenOrLink 원시 초대 토큰 또는 소문자 `http://`·`https://` 접두사로 시작하고
     * `token` 쿼리 파라미터가 포함된 링크입니다.
     * @return 앱에서 공유할 수 있는 완전한 HTTPS 초대 링크입니다.
     * @throws IllegalArgumentException 입력에서 추출한 초대 토큰이 빈 문자열인 경우 발생합니다.
     */
    private fun buildInvitationLink(tokenOrLink: String): String {
        val invitationToken = extractInvitationToken(tokenOrLink)
        require(invitationToken.isNotBlank()) {
            "Invitation token must not be blank."
        }

        return Uri.Builder()
            .scheme("https")
            .authority(BuildConfig.SERVER_HOST)
            .path("open")
            .appendQueryParameter("token", invitationToken)
            .build()
            .toString()
    }

    /**
     * 원시 초대 토큰과 소문자 `http://`·`https://` 접두사의 기존 초대 링크를 구분해
     * 초대 토큰만 추출합니다.
     *
     * @param tokenOrLink 원시 초대 토큰 또는 소문자 `http://`·`https://` 접두사로 시작하고
     * `token` 쿼리 파라미터가 포함된 링크입니다.
     * @return 링크 입력이면 `token` 쿼리 값, 그 외에는 앞뒤 공백을 제거한 입력값입니다.
     * 링크에 `token` 쿼리 파라미터가 없으면 빈 문자열을 반환합니다.
     */
    private fun extractInvitationToken(tokenOrLink: String): String {
        val trimmed = tokenOrLink.trim()

        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            Uri.parse(trimmed).getQueryParameter("token").orEmpty()
        } else {
            trimmed
        }
    }

    /**
     * 지정한 폴더의 초대 링크 생성 요청을 시작하고 현재 링크 값을 즉시 반환합니다.
     *
     * 이 함수는 [viewModelScope]에서 시작한 비동기 요청의 완료를 기다리지 않습니다. 따라서 반환
     * 시점에 링크 생성이 완료된다고 보장할 수 없으며, 완료 전에는 `null`이 반환될 수 있습니다.
     * 완료된 링크가 필요한 호출자는 [createInvitationLink]의 콜백을 사용해야 합니다.
     *
     * @param folderId 초대 링크를 생성할 폴더 ID입니다.
     * @return 반환 시점까지 생성된 초대 링크이며, 아직 생성되지 않았거나 실패하면 `null`입니다.
     */
    fun makeInvitationLink(folderId: Long): String? {
        Log.d("FileViewModel", "makeInvitationLink")

        var link: String? = null

        viewModelScope.launch {
            Log.d("FileViewModel", "makeInvitationLink launch")

            startLoading()
            _errorMessage.value = null

            try {

                Log.d("FileViewModel", "makeInvitationLink try")

                val token = folderRepository.makeInvitationLink(folderId)
                link = buildInvitationLink(token)

                Log.d("FileViewModel", "makeInvitationLink try result: true")

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

    /**
     * 지정한 폴더의 초대 토큰을 발급받아 공유 가능한 HTTPS 초대 링크를 비동기로 생성합니다.
     *
     * 링크 생성 진행·실패 상태는 이 요청을 시작한 공유 바텀시트가 표시하므로 파일 화면 전체의
     * 로딩·오류 상태는 변경하지 않습니다. 요청 생명주기만 [viewModelScope]이 소유합니다.
     *
     * @param folderId 초대 링크를 생성할 폴더 ID입니다.
     * @param onSuccess 완성된 HTTPS 초대 링크와 함께 호출되는 콜백입니다.
     * @param onFailure 토큰 발급 또는 링크 정규화에 실패했을 때 원인과 함께 호출되는 콜백입니다.
     */
    fun createInvitationLink(
        folderId: Long,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit = {}
    ) {
        Log.d("FileViewModel", "createInvitationLink")

        invitationLinkCreateJob?.cancel()
        val request = viewModelScope.launch {
            Log.d("FileViewModel", "createInvitationLink launch")

            try {
                val link = buildInvitationLink(folderRepository.makeInvitationLink(folderId))
                onSuccess(link)

                Log.d("FileViewModel", "createInvitationLink result: true")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("FileViewModel", "createInvitationLink catch: $e")
                onFailure(e)
            }
        }
        invitationLinkCreateJob = request
        request.invokeOnCompletion {
            if (invitationLinkCreateJob === request) {
                invitationLinkCreateJob = null
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

    /**
     * 로그인한 사용자의 공유 폴더 초대를 수락하고 결과를 파일 화면 상태에 반영합니다.
     *
     * 새 요청이 시작되면 이전 초대 수락 요청을 취소합니다. 수락에 성공하면 최신 공유 폴더 목록을
     * 갱신하고, 실패 결과는 화면에 노출할 오류 메시지로 반영합니다.
     *
     * @param token 수락할 공유 폴더 초대 토큰입니다.
     * @return 초대 수락과 공유 폴더 목록 갱신 결과를 구분한 [AcceptSharedFolderInvitationResult]입니다.
     * @throws CancellationException 호출이 취소되거나 새 요청으로 기존 요청이 취소된 경우 발생합니다.
     */
    suspend fun receiveSharedFolderInvitation(
        token: String,
    ): AcceptSharedFolderInvitationResult {
        Log.d("FileViewModel", "receiveSharedFolderInvitation")

        receiveSharedFolderInvitationJob?.cancel()
        val request = viewModelScope.async(start = CoroutineStart.LAZY) {
            Log.d("FileViewModel", "receiveSharedFolderInvitation launch")

            startLoading()
            _errorMessage.value = null

            try {
                val userId = authPreference.getUserId()

                val result = if (userId == null) {
                    AcceptSharedFolderInvitationResult.AuthenticationRequired(
                        UserIdNullException()
                    )
                } else {
                    acceptSharedFolderInvitationUseCase(token)
                }

                result.updateSharedFolderInvitationState()

                Log.d("FileViewModel", "receiveSharedFolderInvitation well done")
                result
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.d("FileViewModel", "receiveSharedFolderInvitation catch: $e")
                _errorMessage.value = e.message
                AcceptSharedFolderInvitationResult.Failure(e)
            } finally {
                stopLoading()
            }
        }

        receiveSharedFolderInvitationJob = request
        request.invokeOnCompletion {
            if (receiveSharedFolderInvitationJob === request) {
                receiveSharedFolderInvitationJob = null
            }
        }
        request.start()

        return request.await()
    }

    /**
     * 공유 폴더 초대 수락 결과를 파일 화면의 공유 폴더 목록 또는 오류 상태에 반영합니다.
     *
     * @receiver 화면 상태에 반영할 [AcceptSharedFolderInvitationResult]입니다.
     */
    private fun AcceptSharedFolderInvitationResult.updateSharedFolderInvitationState() {
        when (this) {
            is AcceptSharedFolderInvitationResult.Accepted -> {
                _sharedTopFolders.value = sharedFolders
            }

            is AcceptSharedFolderInvitationResult.AcceptedButRefreshFailed -> {
                _errorMessage.value = cause.message
            }

            is AcceptSharedFolderInvitationResult.AuthenticationRequired -> {
                _errorMessage.value = cause.message
            }

            is AcceptSharedFolderInvitationResult.Failure -> {
                _errorMessage.value = cause.message
            }

            is AcceptSharedFolderInvitationResult.InvalidInvitation -> {
                _errorMessage.value = cause?.message
            }

            is AcceptSharedFolderInvitationResult.NetworkFailure -> {
                _errorMessage.value = cause.message
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
    /**
     * 공유 중인 소분류 폴더를 비공개 상태로 전환합니다.
     *
     * @param onFinished 성공 또는 실패로 요청 처리가 끝난 뒤 호출되는 완료 콜백입니다.
     */
    fun folderToPrivate(
        folder: FolderSimpleInfo,
        onFinished: () -> Unit = {},
    ){
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
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception){
                Log.d("FileViewModel", "folderToPrivate catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("FileViewModel", "folderToPrivate finally")

                stopLoading()
                onFinished()
            }
        }
        Log.d("FileViewModel", "folderToPrivate return")
    }

    // ---------- share method ----------

}
