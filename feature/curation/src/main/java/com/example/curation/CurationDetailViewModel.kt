package com.example.curation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.RecommendedLink
import com.example.core.repository.CurationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CurationLinksUiState(
    val loading: Boolean = false,
    val items: List<RecommendedLink> = emptyList(),
    val error: String? = null
)

data class CurationDetailUiState(
    val loading: Boolean = false,
    val topTags: List<String> = emptyList(),
    val headerMent: String? = null,
    val footerMent: String? = null,
    val liked: Boolean? = null,
    val likeBusy: Boolean = false,
    val error: String? = null
)

//스킵 보정 헬퍼(url 이동)
private fun ensureHttpScheme(raw: String): String =
    if (raw.startsWith("http://") || raw.startsWith("https://")) raw else "https://$raw"



@HiltViewModel
class CurationDetailViewModel @Inject constructor(
    private val repo: CurationRepository
) : ViewModel() {

    // 내부 보관용 ID 상태 추가
    private val _userId = MutableStateFlow(-1L)
    val userId: StateFlow<Long> = _userId

    private val _curationId = MutableStateFlow(-1L)

    private val _links = MutableStateFlow(CurationLinksUiState())
    val links: StateFlow<CurationLinksUiState> = _links

    // 외부에서 userId/curationId를 주입하거나, 내부에서 rememberSaveable 등으로 유지 가능
    fun loadRecommendedLinks(userId: Long, curationId: Long) {
        _links.value = _links.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.getRecommendedLinks(userId, curationId) }
                .onSuccess { list ->
                    _links.value = CurationLinksUiState(
                        loading = false,
                        items = list,
                        error = null)
                }
                .onFailure { e ->
                    _links.value = CurationLinksUiState(
                        loading = false,
                        items = emptyList(),
                        error = e.message)
                }
        }
    }

    /** 화면 진입 시 한 번 호출: userId/curationId 주입 + 좋아요 상태 로드 */
    fun attach(userId: Long, curationId: Long, loadDetail: Boolean = true, loadLinks: Boolean = false) {
        _userId.value = userId
        _curationId.value = curationId
        if (loadDetail) loadCurationDetail(curationId)
        refreshLike() // 현재 좋아요 상태 조회
        if (loadLinks) loadRecommendedLinks(userId, curationId)
    }

    private val _detail = MutableStateFlow(CurationDetailUiState())
    val detail: StateFlow<CurationDetailUiState> = _detail

    fun loadCurationDetail(curationId: Long) {
        _detail.value = _detail.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.getCurationDetail(curationId) }
                .onSuccess { d ->
                    _detail.value = CurationDetailUiState(
                        loading = false,
                        topTags = d.topTags,
                        headerMent = d.headerMent,
                        footerMent = d.footerMent
                    )
                }
                .onFailure { e ->
                    _detail.value = CurationDetailUiState(loading = false, error = e.message)
                }
        }
    }

    /** 현재 좋아요 상태 새로고침 */
    fun refreshLike(userId: Long? = null, curationId: Long? = null) {
        val uid = userId ?: _userId.value
        val cid = curationId ?: _curationId.value
        if (uid <= 0 || cid <= 0) return

        viewModelScope.launch {
            runCatching { repo.isCurationLiked(cid, uid) }
                .onSuccess { liked -> _detail.value = _detail.value.copy(liked = liked) }
                .onFailure {
                    // 실패 시 기본 false (UI에서 빈 하트)
                    _detail.value = _detail.value.copy(liked = false)
                }
        }
    }

    /** 하트 토글 (낙관적 업데이트 + 실패 롤백) */
    fun toggleLike() {
        val uid = _userId.value
        val cid = _curationId.value
        val current = _detail.value.liked ?: false
        if (uid <= 0 || cid <= 0 || _detail.value.likeBusy) return

        // 낙관적 업데이트 + busy on
        _detail.value = _detail.value.copy(liked = !current, likeBusy = true, error = null)

        viewModelScope.launch {
            val result = runCatching {
                if (current) repo.unlikeCuration(cid, uid) else repo.likeCuration(cid, uid)
            }

            _detail.value = result.fold(
                onSuccess = {
                    // 성공: busy off, 상태 유지
                    _detail.value.copy(likeBusy = false)
                },
                onFailure = { e ->
                    // 실패: 롤백 + 메시지
                    val msg = e.message.orEmpty()
                    val userMsg =
                        if (msg.contains("Token", true) && msg.contains("expired", true))
                            "세션이 만료됐어요. 다시 로그인해 주세요."
                        else "좋아요 처리에 실패했어요"
                    _detail.value.copy(liked = current, likeBusy = false, error = userMsg)
                }
            )
        }
    }

    /** 편의: 디테일/링크/좋아요 한 번에 로드 */
    fun loadAll(userId: Long, curationId: Long) {
        attach(userId, curationId, loadDetail = true, loadLinks = true)
    }
}
