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
    val headerMent: String? = null,//TODO : api 확인하기.
    val footerMent: String? = null,
    val liked: Boolean? = null,
    val likeBusy: Boolean = false,
    val error: String? = null,
    val month: String? = null
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
                    android.util.Log.d("CurationDetailVM", "추천링크 성공: user=$userId, curation=$curationId, count=${list.size}")
                    _links.value = CurationLinksUiState(
                        loading = false,
                        items = list,
                        error = null)
                }
                .onFailure { e ->
                    android.util.Log.e("CurationDetailVM", "추천링크 실패: user=$userId, curation=$curationId, error=${e.message}", e)
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
        if (loadLinks) loadRecommendedLinks(userId, curationId)
    }

    private val _detail = MutableStateFlow(CurationDetailUiState())
    val detail: StateFlow<CurationDetailUiState> = _detail

    fun loadCurationDetail(curationId: Long) {
        _detail.value = _detail.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.getCurationDetail(curationId) }
                .onSuccess { d ->
                    android.util.Log.d("CurationDetailVM", "디테일 성공: curation=$curationId, topTags=${d.topTags}, header=${d.headerMent}")
                    _detail.value = CurationDetailUiState(
                        loading = false,
                        month = d.month,
                        topTags = d.topTags,
                        headerMent = d.headerMent,
                        footerMent = d.footerMent
                    )
                }
                .onFailure { e ->
                    android.util.Log.e("CurationDetailVM", "디테일 실패: curation=$curationId, error=${e.message}", e)
                    _detail.value = CurationDetailUiState(loading = false, error = e.message)
                }
        }
    }




    /** 편의: 디테일/링크/좋아요 한 번에 로드 */
    fun loadAll(userId: Long, curationId: Long) {
        attach(userId, curationId, loadDetail = true, loadLinks = true)
    }
}
