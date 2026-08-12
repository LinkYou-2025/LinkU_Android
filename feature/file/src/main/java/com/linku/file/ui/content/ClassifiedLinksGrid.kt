package com.linku.file.ui.content

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.LinkItemInfo
import com.linku.design.modal.ModalWindow
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.file.R
import com.linku.file.ui.item.LinkItemLayout
import com.linku.file.ui.item.items.EmptyLinkItemLayout

/**
 * 링크 카드 행 사이에 적용되는 기본 세로 간격(dp)입니다.
 */
private const val INTER_LAYER_PADDING = 18.51

/**
 * 사용 가능한 가로 폭을 기준으로 두 열 사이의 가로 간격을 계산할 때 사용하는 비율입니다.
 */
private const val ITEM_RATIO = 10f / 174f

/**
 * 선택된 폴더에 속한 링크 목록을 두 열 그리드로 표시합니다.
 *
 * 첫 번째 셀에는 링크 분류를 시작하는 추가 아이템을 배치하고, 이후 셀에는 분류된 링크를
 * [LinkItemLayout]으로 렌더링합니다. 분류되지 않은 링크가 없을 때는 안내 모달을 띄우며,
 * 링크를 길게 누르면 삭제 확인 모달을 표시합니다.
 *
 * @param modifier 그리드 전체 컨테이너에 적용할 [Modifier]입니다.
 * @param contentPadding 그리드 내부 콘텐츠에 적용할 여백입니다.
 * @param links 표시할 분류된 링크 목록입니다.
 * @param hasNotCategorizationLinks 분류 바텀시트에 전달할 미분류 링크가 존재하는지 여부입니다.
 * @param onLinkCategorizationClick 링크 분류 추가 아이템을 눌렀을 때 실행할 동작입니다.
 * @param onLinkClick 링크 카드를 눌렀을 때 링크 ID를 전달하는 콜백입니다.
 * @param onDeleteLink 삭제 확인 모달에서 확인을 눌렀을 때 링크 ID를 전달하는 콜백입니다.
 */
@Composable
internal fun ClassifiedLinksGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp),
    links: List<LinkItemInfo>,
    hasNotCategorizationLinks: Boolean,
    onLinkCategorizationClick: () -> Unit,
    onLinkClick: (Long) -> Unit,
    onDeleteLink: (Long) -> Unit,
){
    /** 모달과 안내 문구에 사용할 LinkU 테마 색상 팔레트입니다. */
    val colors = MaterialTheme.linkuColors

    /** 분류할 수 있는 미분류 링크가 없을 때 표시할 안내 모달 상태입니다. */
    var categorizationModalWindowVisible by remember { mutableStateOf(false) }

    /** 링크를 길게 눌렀을 때 표시할 삭제 확인 모달 상태입니다. */
    var deleteModalWindowVisible by remember { mutableStateOf(false) }

    /** 삭제 확인 모달에서 사용할 현재 선택된 링크 ID입니다. */
    var selectedLinkId by remember { mutableStateOf<Long?>(null) }

    /** contentPadding의 start/end 값을 현재 레이아웃 방향에 맞게 계산하기 위한 방향 정보입니다. */
    val layoutDirection = LocalLayoutDirection.current

    /**
     * 그리드가 실제로 사용할 수 있는 가로 폭을 기준으로 카드 사이 간격을 계산합니다.
     *
     * 카테고리/폴더 그리드와 동일한 방식으로 2열 카드 간격을 비율 기반으로 맞춰,
     * 화면 폭이 달라져도 카드와 열 사이 여백의 균형을 유지합니다.
     */
    BoxWithConstraints(
        modifier = modifier
    ) {
        /** 현재 레이아웃 방향(LTR/RTL)을 고려한 좌우 패딩의 합입니다. */
        val horizontalPadding =
            contentPadding.calculateStartPadding(layoutDirection) +
                    contentPadding.calculateEndPadding(layoutDirection)

        /** 전체 최대 너비에서 좌우 패딩을 제외한 실제 콘텐츠 영역의 너비입니다. */
        val availableWidth = maxWidth - horizontalPadding

        /** 콘텐츠 가용 너비에 비례해 계산한 두 열 사이의 가로 간격입니다. */
        val horizontalSpacing = availableWidth * ITEM_RATIO

        /** 링크 추가 카드와 링크 카드 목록을 같은 2열 그리드 안에 배치합니다. */
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(INTER_LAYER_PADDING.dp),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
        ) {
            /** 첫 번째 셀은 항상 링크를 분류/추가하는 진입점으로 사용합니다. */
            item {
                AddLinkItem(
                    modifier = Modifier
                        // 실제 링크 카드와 동일한 셀 점유율을 사용해 그리드 정렬을 맞춥니다.
                        .fillMaxSize()
                        .noRippleClickable {
                            Log.d("LinksGrid", "링크 추가하기 클릭")
                            // 미분류 링크가 있으면 분류 바텀시트를 열고, 없으면 안내 모달을 띄웁니다.
                            if (hasNotCategorizationLinks) {
                                onLinkCategorizationClick()
                            } else {
                                categorizationModalWindowVisible = true
                            }
                        }
                )
            }

            /** 분류된 링크 목록을 링크 카드 레이아웃으로 렌더링합니다. */
            items(links, key = {it.linkuId}) { link ->
                LinkItemLayout(
                    modifier = Modifier.fillMaxSize(),
                    link = link,
                    onClick = {
                        // 상세 화면 이동은 상위에서 전달받은 링크 클릭 콜백으로 위임합니다.
                        onLinkClick(link.linkuId)
                    },
                    onLongClick = {
                        // 길게 누른 링크 ID를 저장한 뒤 삭제 확인 모달을 표시합니다.
                        selectedLinkId = link.linkuId
                        deleteModalWindowVisible = true
                    }
                )
            }
        }
    }


    // 분류되지 않은 링크가 없을 때 링크 추가 동선을 안내하는 모달창입니다.
    ModalWindow(
        visible = categorizationModalWindowVisible,
        onDismiss = { categorizationModalWindowVisible = false },
        title = "분류되지 않은 링크가 없습니다.",
        positiveText = "확인"
    ) {
        Text(
            text = "새 링크를 저장한 뒤 분류해보세요!",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal,
            color = colors.gray[600],
            textAlign = TextAlign.Center
        )
    }

    // 링크 카드 long click 이후 실제 삭제를 한 번 더 확인하는 모달창입니다.
    ModalWindow(
        visible = deleteModalWindowVisible,
        onOkay = {
            // 확인 시점에 선택된 링크 ID가 남아 있을 때만 삭제 콜백을 실행합니다.
            selectedLinkId?.let { id ->
                onDeleteLink(id)
            }
            // 모달을 닫으면서 선택 상태도 함께 비워 다음 삭제 동작과 섞이지 않게 합니다.
            deleteModalWindowVisible = false
            selectedLinkId = null
        },
        onDismiss = { deleteModalWindowVisible = false },
        title = "해당 링크를 삭제하시겠습니까?",
        positiveText = "삭제하기",
        negativeText = "취소하기"
    ) {
        Text(
            text = "삭제 시 해당 링크가 영구적으로 제거되며\n복구가 불가능합니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight(400),
            color = colors.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 링크 목록의 첫 번째 셀에 표시되는 링크 추가 아이템입니다.
 *
 * 빈 링크 카드 위에 추가 아이콘과 라벨을 겹쳐 표시하며, 실제 클릭 동작은 상위 그리드에서
 * 전달한 [modifier]의 클릭 modifier를 통해 처리합니다.
 *
 * @param modifier 카드 크기와 클릭 영역을 결정하는 [Modifier]입니다.
 */
@Composable
private fun AddLinkItem(
    modifier: Modifier
) {
    /** 링크 추가 아이템의 라벨 색상을 가져오기 위한 테마 색상입니다. */
    val colors = MaterialTheme.linkuColors

    /** 빈 링크 카드 위에 아이콘과 텍스트를 겹쳐 올리는 오버레이 컨테이너입니다. */
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        /** 실제 링크 카드와 같은 크기의 빈 링크 카드를 배경으로 사용합니다. */
        EmptyLinkItemLayout()

        /** 링크 추가를 나타내는 아이콘을 카드 상단 기준 위치에 배치합니다. */
        Image(
            modifier = Modifier.padding(top = 103.dp),
            painter = painterResource(R.drawable.add_folder_icon),
            contentDescription = null
        )

        /** 추가 아이콘 아래에 표시되는 링크 추가 라벨입니다. */
        Text(
            modifier = Modifier.padding(top = 147.dp),
            text = "링크 추가하기",
            fontSize = 15.sp,
            fontWeight = FontWeight(500),
            color = colors.black,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ClassifiedLinksGridPreview() {
    val sampleLinks = listOf(
        LinkItemInfo(
            linkuId = 1L,
            parentFolderId = 1L,
            title = "네이버",
            url = "https://www.naver.com",
            tags = listOf("포털", "검색"),
            linkuImageUrl = null,
            createdAt = null
        ),
        LinkItemInfo(
            linkuId = 2L,
            parentFolderId = 1L,
            title = "구글",
            url = "https://www.google.com",
            tags = listOf("검색", "글로벌"),
            linkuImageUrl = null,
            createdAt = null
        ),
        LinkItemInfo(
            linkuId = 3L,
            parentFolderId = 1L,
            title = "유튜브",
            url = "https://www.youtube.com",
            tags = listOf("영상", "엔터테인먼트"),
            linkuImageUrl = null,
            createdAt = null
        )
    )

    LinkuPreview {
        ClassifiedLinksGrid(
            links = sampleLinks,
            hasNotCategorizationLinks = true,
            onLinkCategorizationClick = {},
            onLinkClick = {},
            onDeleteLink = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ClassifiedLinksGridEmptyPreview() {
    LinkuPreview {
        ClassifiedLinksGrid(
            links = emptyList(),
            hasNotCategorizationLinks = false,
            onLinkCategorizationClick = {},
            onLinkClick = {},
            onDeleteLink = {}
        )
    }
}

