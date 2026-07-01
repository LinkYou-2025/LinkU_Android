package com.linku.file.ui.content

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.alpha
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
import com.linku.design.theme.linkuColors
import com.linku.file.R
import com.linku.file.ui.item.LinkItemLayout

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
    val colors = MaterialTheme.linkuColors

    var categorizationModalWindowVisible by remember { mutableStateOf(false) }
    var deleteModalWindowVisible by remember { mutableStateOf(false) }

    var selectedLinkId by remember { mutableStateOf<Long?>(null) }

    val layoutDirection = LocalLayoutDirection.current

    BoxWithConstraints(
        modifier = modifier
    ) {
        val horizontalPadding =
            contentPadding.calculateStartPadding(layoutDirection) +
                    contentPadding.calculateEndPadding(layoutDirection)

        val availableWidth = maxWidth - horizontalPadding

        val horizontalSpacing = availableWidth * ITEM_RATIO

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(INTER_LAYER_PADDING.dp),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
        ) {
            item {
                AddLinkItem(
                    modifier = Modifier
                        .fillMaxSize(164f / 174f)
                        .noRippleClickable {
                            Log.d("LinksGrid", "링크 추가하기 클릭")
                            if (hasNotCategorizationLinks) {
                                onLinkCategorizationClick()
                            } else {
                                categorizationModalWindowVisible = true
                            }
                        }
                )
            }

            items(links) { link ->
                LinkItemLayout(
                    modifier = Modifier.fillMaxSize(164f / 174f),
                    link = link,
                    onClick = {
                        onLinkClick(link.linkuId)
                    },
                    onLongClick = {
                        selectedLinkId = link.linkuId
                        deleteModalWindowVisible = true
                    }
                )
            }
        }
    }


    // 분류되지 않는 링크가 없으면 뜨는 모달창
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

    // 링크 삭제 모달창
    ModalWindow(
        visible = deleteModalWindowVisible,
        onOkay = {
            // ✅ 확인에서 안전하게 현재 선택된 id로 삭제
            selectedLinkId?.let { id ->
                onDeleteLink(id)
            }
            // 상태 정리
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
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier.alpha(1f),
            ) {
                LinkItemLayout(
                    link = null
                )
            }

            Image(
                modifier = Modifier.padding(top = 103.dp),
                painter = painterResource(R.drawable.add_folder_icon),
                contentDescription = null
            )

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
}

/**
 * [ClassifiedLinksGrid]의 기본 상태를 확인하기 위한 Compose Preview입니다.
 */
@Preview(showBackground = true)
@Composable
private fun ClassifiedLinksGridTest(){
    ClassifiedLinksGrid(
        links = emptyList(),
        hasNotCategorizationLinks = false,
        onLinkCategorizationClick = {},
        onLinkClick = {},
        onDeleteLink = {},
    )
}
