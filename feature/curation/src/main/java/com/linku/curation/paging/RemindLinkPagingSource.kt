package com.linku.curation.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.linku.core.model.CategoryType
import com.linku.core.model.EmotionType
import com.linku.core.model.LinkSimpleInfo
import kotlinx.coroutines.delay

/**
 * "저장만 하고 열어보지 않은 링크" 목록(#44-1)을 흉내 내는 임시 로컬 [PagingSource].
 *
 * CurationApi/CurationRepository(princeHw 작업)가 아직 빈 스텁이라, 실제 네트워크 응답 대신
 * 더미 데이터를 페이지 단위로 생성함. API가 준비되면 이 클래스를 커서/오프셋 기반
 * 네트워크 [PagingSource]로 교체하면 됨.
 *
 * [PagingSource]는 "페이지 단위로 데이터를 어떻게 가져올지"를 Paging3에 알려주는 인터페이스.
 * 제네릭 두 개(<Key, Value>) 중 Key=Int는 "다음/이전 페이지를 요청할 때 쓰는 값",
 * Value=LinkSimpleInfo는 "한 페이지에 담기는 아이템 하나의 타입"을 뜻함.
 */
internal class RemindLinkPagingSource : PagingSource<Int, LinkSimpleInfo>() {

    /**
     * Paging3가 "새 페이지가 필요하다"고 판단할 때마다(최초 로드 포함) 호출하는 함수.
     *
     * params.key: 직전 load() 호출이 돌려준 nextKey/prevKey 값. 최초 호출 땐 null.
     * params.loadSize: 이번에 몇 개를 가져와야 하는지. Pager의 pageSize/initialLoadSize로 정해짐
     *                   (CurationViewModel에서 pageSize=initialLoadSize=10으로 고정해둔 값).
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LinkSimpleInfo> {
        // 최초 로드는 params.key가 null이라, 0페이지(첫 페이지)로 간주
        val page = params.key ?: 0

        return try {
            delay(300) // TODO: API 연동 시 삭제 — 네트워크 왕복처럼 보이게 하려고 넣은 인위적 지연

            // TODO: API 연동 시 이 두 줄을 실제 API 호출 + 응답 매핑으로 교체
            // 지금은 "page * loadSize"로 더미 아이템의 시작 id를 정하고, 그 뒤로 loadSize개를 만들어냄
            // 예) page=0,loadSize=10 -> id 0~9 / page=1,loadSize=10 -> id 10~19
            val startId = page * params.loadSize
            val items =
                (0 until params.loadSize).map { offset -> createDummyItem(id = startId + offset) }

            // TODO: API 연동 시 서버 응답의 "다음 페이지 존재 여부" 필드로 교체
            // 지금은 지금까지 만든 아이템 수가 더미 총량(TOTAL_MOCK_ITEM_COUNT)을 넘으면 nextKey=null로 끝냄
            val nextKey = if (startId + items.size >= TOTAL_MOCK_ITEM_COUNT) null else page + 1

            // LoadResult.Page: 이번에 가져온 데이터(data)와 이전/다음 페이지를 요청할 때 쓸 키를 Paging3에 알려줌.
            // LazyPagingItems는 이 정보를 보고 스크롤이 끝에 닿았을 때 nextKey로 load()를 다시 호출함.
            LoadResult.Page(
                data = items,
                prevKey = if (page == 0) null else page - 1, // 0페이지면 이전 페이지가 없음
                nextKey = nextKey
            )
        } catch (e: Exception) {
            // 지금은 더미 생성이라 실질적으로 예외가 안 나지만, 실제 API 호출로 바뀌면
            // 네트워크 실패 시 여기로 들어와서 LazyPagingItems.loadState가 Error로 바뀜
            LoadResult.Error(e)
        }
    }

    /**
     * 화면 회전/프로세스 재생성 등으로 Paging3가 데이터를 다시 불러와야 할 때,
     * "어느 페이지부터 다시 불러올지" 계산하는 함수. Paging3 공식 문서 예시와 동일한 구현이라
     * API 연동 이후에도 그대로 둬도 됨(더미 데이터 전용 코드가 아님).
     */
    override fun getRefreshKey(state: PagingState<Int, LinkSimpleInfo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // anchorPosition(사용자가 마지막으로 보고 있던 아이템 위치)이 속한 페이지를 찾고
            val anchorPage = state.closestPageToPosition(anchorPosition)
            // 그 페이지의 prevKey+1 (또는 nextKey-1)이 곧 "그 페이지 자신의 키"가 됨
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    // TODO: API 연동 시 이 함수 전체 삭제 — DTO -> LinkSimpleInfo 매핑은 Repository/Mapper 레이어로 이동
    private fun createDummyItem(id: Int): LinkSimpleInfo {
        val sample = DUMMY_SAMPLES[id % DUMMY_SAMPLES.size] // 샘플 5개를 순서대로 돌려가며 재사용(id % 5)
        return LinkSimpleInfo(
            linkuId = id.toLong(),
            categoryId = sample.categoryId,
            memo = null,
            emotionId = sample.emotionId,
            title = sample.title,
            domain = sample.domainName,
            domainImageUrl = null,
            linkuImageUrl = null,
            aiArticleExists = false, // TODO: 백엔드에서 실제 값 받기 전까지는 기본 false로 고정
        )
    }

    // TODO: API 연동 시 삭제 — createDummyItem이 LinkSimpleInfo를 만들 때 쓰는 더미 재료 묶음
    private data class DummySample(
        val title: String,
        val categoryId: Long,
        val emotionId: Long,
        val domainName: String,
    )

    // TODO: API 연동 시 이 companion object 전체(더미 상수 + 더미 목록) 삭제
    private companion object {
        const val TOTAL_MOCK_ITEM_COUNT = 50 // 더미 데이터 총 개수. 이 개수를 넘기면 nextKey=null이 되어 스크롤이 멈춤

        // 카드 5종류를 순환시켜서 화면에 다양한 태그/도메인이 섞여 보이게 함
        val DUMMY_SAMPLES = listOf(
            DummySample(
                "오픽 AL 따는 꿀팁 얻고 보러오세요",
                CategoryType.STUDY_METHOD.id,
                EmotionType.CALM.value,
                "BLOG"
            ),
            DummySample(
                "오픽 공부할 때 문법도 공부해야할까?",
                CategoryType.LANGUAGE.id,
                EmotionType.EXCITE.value,
                "GitHub"
            ),
            DummySample("필수 영문법", CategoryType.LANGUAGE.id, EmotionType.CALM.value, "네이버쇼핑"),
            DummySample(
                "영어회화 배우는 모임",
                CategoryType.SELF_IMPROVEMENT.id,
                EmotionType.JOY.value,
                "네이버쇼핑"
            ),
            DummySample(
                "토익스피킹 VS 오픽 뭐가 더 좋을까?",
                CategoryType.STUDY_METHOD.id,
                EmotionType.EXCITE.value,
                "네이버쇼핑"
            ),
        )
    }
}
