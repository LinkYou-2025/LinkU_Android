package com.linku.curation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.linku.core.model.LinkSimpleInfo
import com.linku.curation.paging.RemindLinkPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val REMIND_LINK_PAGE_SIZE = 10

@HiltViewModel
class CurationViewModel @Inject constructor(

) : ViewModel() {

    /**
     * "저장만 하고 열어보지 않은 링크" 목록(#44-1).
     *
     * TODO: CurationApi/CurationRepository(princeHw 작업)가 준비되면
     * [RemindLinkPagingSource]를 실제 네트워크 PagingSource로 교체.
     *
     * Paging3는 기본적으로 첫 로드 시 pageSize의 3배를 한 번에 불러오는데, => 현우 공주님께서 알려주심
     * 10개 단위로 화면을 구성하기 위해 [PagingConfig.initialLoadSize]를
     * pageSize와 동일하게 강제 고정함.
     */
    val remindLinks: Flow<PagingData<LinkSimpleInfo>> = Pager(
        config = PagingConfig(
            pageSize = REMIND_LINK_PAGE_SIZE,
            initialLoadSize = REMIND_LINK_PAGE_SIZE, // 여기 고정 안하면 30개 나옴.
            enablePlaceholders = false // 아직 불러오지 않은 자리 null로 채우지 않게 설정함.
        ),
        pagingSourceFactory = { RemindLinkPagingSource() } // 바로 넘기지 않고 람다로 감싸서 필요할 때마다 만드는 방법을 알려줌
    ).flow.cachedIn(viewModelScope) // flow는 콜드 스트림이라 구독할 때마다 데이터를 만드는데 이를 cachedIn(viewModelScope)를 통해 방지함. 뷰모델이 살아 있는 동안 유지함.
}




