package com.linku.file.ui.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.LinkItemInfo
import com.linku.design.modal.ModalWindow
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.R
import com.linku.file.ui.item.EmptyFolderItemLayout
import com.linku.file.ui.item.LinkItemLayout
import com.linku.file.ui.item.MyFolderItemLayout

/**
 * 폴더/링크 카드 행 사이에 적용되는 기본 세로 간격(dp)입니다.
 */
private const val INTER_LAYER_PADDING = 18.51

/**
 * 미분류 링크 섹션 제목 위쪽에 적용되는 여백(dp)입니다.
 */
private const val SECTION_TITLE_TOP_PADDING = 21.49

/**
 * 미분류 링크 섹션 제목 아래쪽에 적용되는 여백(dp)입니다.
 */
private const val SECTION_TITLE_BOTTOM_PADDING = 1.49

/**
 * 사용 가능한 가로 폭을 기준으로 두 열 사이의 가로 간격을 계산할 때 사용하는 비율입니다.
 */
private const val ITEM_RATIO = 10f / 174f

/**
 * 선택된 최상위 폴더의 하위 폴더와 미분류 링크를 한 그리드 안에 표시합니다.
 *
 * 첫 번째 셀에는 하위 폴더 추가 아이템을 배치하고, 이어서 하위 폴더 목록을 표시합니다.
 * 미분류 링크가 존재하면 전체 폭 섹션 제목을 추가한 뒤 링크 카드를 이어서 렌더링합니다.
 * 하위 폴더는 편집 모드에 따라 수정/공유 상태 변경 액션을 제공하고, 링크는 길게 눌러
 * 삭제 확인 모달을 열 수 있습니다.
 *
 * @param modifier 그리드 전체 컨테이너에 적용할 [Modifier]입니다.
 * @param contentPadding 그리드 내부 콘텐츠에 적용할 여백입니다.
 * @param folders 표시할 하위 폴더 목록입니다.
 * @param notCategorizationLinks 표시할 미분류 링크 목록입니다.
 * @param selectedTopFolderColorStyle 하위 폴더 카드에 적용할 선택된 최상위 폴더 색상 스타일입니다.
 * @param isEditMode 현재 편집 모드 여부입니다.
 * @param onAddFolderClick 폴더 추가 아이템을 눌렀을 때 실행할 동작입니다.
 * @param onFolderClick 일반 모드에서 하위 폴더 카드를 눌렀을 때 실행할 동작입니다.
 * @param onFolderEditClick 편집 모드에서 하위 폴더 편집 아이콘을 눌렀을 때 실행할 동작입니다.
 * @param onChangeSharingClick 편집 모드에서 공유 상태 아이콘을 눌렀을 때 실행할 동작입니다.
 * @param onDeleteFolder 삭제 확인 모달에서 확인을 눌렀을 때 하위 폴더를 전달하는 콜백입니다.
 * @param onLinkClick 미분류 링크 카드를 눌렀을 때 링크 ID를 전달하는 콜백입니다.
 * @param onDeleteNotCategorizationLink 미분류 링크 삭제 확인 모달에서 확인을 눌렀을 때 링크 ID를 전달하는 콜백입니다.
 */
@Composable
internal fun MyFoldersGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp),
    folders: List<FolderSimpleInfo>,
    notCategorizationLinks: List<LinkItemInfo>,
    selectedTopFolderColorStyle: CategoryColorStyle,
    isEditMode: Boolean,
    onAddFolderClick: () -> Unit,
    onFolderClick: (FolderSimpleInfo) -> Unit,
    onFolderEditClick: (FolderSimpleInfo) -> Unit,
    onChangeSharingClick: (FolderSimpleInfo) -> Unit,
    onDeleteFolder: (FolderSimpleInfo) -> Unit,
    onLinkClick: (Long) -> Unit,
    onDeleteNotCategorizationLink: (Long) -> Unit
) {
    val colors = MaterialTheme.linkuColors

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
                AddBottomFolderItem(
                    modifier = Modifier
                        .fillMaxSize(164f / 174f)
                        .noRippleClickable {
                            if (!isEditMode) {
                                onAddFolderClick()
                            }
                        }
                )
            }

            items(folders) { folder ->
                MyFolderItem(
                    folder = folder,
                    colorStyle = selectedTopFolderColorStyle,
                    isEditMode = isEditMode,
                    onClick = {
                        onFolderClick(folder)
                    },
                    onEdit = {
                        onFolderEditClick(folder)
                    },
                    onChangeSharing = {
                        onChangeSharingClick(folder)
                    },
                    onDelete = {
                        onDeleteFolder(folder)
                    }
                )
            }

            if (notCategorizationLinks.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "분류되지 않은 링크",
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight(700),
                        color = colors.black,
                        modifier = Modifier.padding(
                            top = SECTION_TITLE_TOP_PADDING.dp,
                            bottom = SECTION_TITLE_BOTTOM_PADDING.dp
                        )
                    )
                }

                items(notCategorizationLinks) { link ->
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
    }

    ModalWindow(
        visible = deleteModalWindowVisible,
        onOkay = {
            selectedLinkId?.let { id ->
                onDeleteNotCategorizationLink(id)
            }
            deleteModalWindowVisible = false
            selectedLinkId = null
        },
        onDismiss = { deleteModalWindowVisible = false },
        title = "해당 링크를 삭제하시겠습니까?",
        positiveText = "삭제",
        negativeText = "취소"
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
 * 하위 폴더 목록의 첫 번째 셀에 표시되는 폴더 추가 아이템입니다.
 *
 * 빈 폴더 카드 위에 추가 아이콘과 라벨을 겹쳐 표시하며, 실제 클릭 동작은 상위 그리드에서
 * 전달한 [modifier]의 클릭 modifier를 통해 처리합니다.
 *
 * @param modifier 카드 크기와 클릭 영역을 결정하는 [Modifier]입니다.
 */
@Composable
private fun AddBottomFolderItem(
    modifier: Modifier
) {
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            EmptyFolderItemLayout()

            Image(
                modifier = Modifier.padding(top = 71.dp),
                painter = painterResource(R.drawable.add_folder_icon),
                contentDescription = null
            )

            Text(
                text = "폴더 추가하기",
                fontSize = 15.sp,
                fontWeight = FontWeight(500),
                color = colors.black,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * 하위 폴더 카드의 클릭, 길게 누르기, 삭제 확인 모달을 함께 처리하는 래퍼입니다.
 *
 * 일반 모드에서는 클릭 시 하위 폴더의 링크 화면으로 이동하고, 편집 모드에서는 카드 내부의
 * 편집/공유 아이콘 액션만 활성화합니다. 길게 누르면 삭제 확인 모달을 표시합니다.
 *
 * @param folder 표시할 하위 폴더 정보입니다.
 * @param colorStyle 선택된 최상위 폴더에서 파생된 폴더 카드 색상 스타일입니다.
 * @param isEditMode 편집 모드 활성화 여부입니다.
 * @param onClick 일반 모드에서 폴더 카드를 눌렀을 때 실행할 동작입니다.
 * @param onEdit 편집 모드에서 편집 아이콘을 눌렀을 때 실행할 동작입니다.
 * @param onChangeSharing 편집 모드에서 공유 상태 아이콘을 눌렀을 때 실행할 동작입니다.
 * @param onDelete 삭제 확인 모달에서 확인을 눌렀을 때 실행할 동작입니다.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MyFolderItem(
    folder: FolderSimpleInfo,
    colorStyle: CategoryColorStyle,
    isEditMode: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onChangeSharing: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = MaterialTheme.linkuColors
    val interactionSource = remember { MutableInteractionSource() }
    var deleteModalWindowVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.combinedClickable(
            indication = null,
            interactionSource = interactionSource,
            onClick = {
                if (!isEditMode) {
                    onClick()
                }
            },
            onLongClick = {
                deleteModalWindowVisible = true
            }
        ),
        contentAlignment = Alignment.Center
    ) {
        MyFolderItemLayout(
            modifier = Modifier.fillMaxSize(164f / 174f),
            colorStyle = colorStyle,
            folder = folder,
            isEditMode = isEditMode,
            onEdit = onEdit,
            onChangeSharing = onChangeSharing
        )
    }

    ModalWindow(
        visible = deleteModalWindowVisible,
        onOkay = {
            onDelete()
            deleteModalWindowVisible = false
        },
        onDismiss = { deleteModalWindowVisible = false },
        positiveText = "삭제하기",
        negativeText = "취소하기",
        title = "해당 폴더를 삭제하시겠습니까?"
    ) {
        Text(
            text = "삭제 시 폴더 내 모든 링크가 영구적으로\n제거되며 복구가 불가능합니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight(400),
            color = colors.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * [MyFoldersGrid]의 기본 상태를 확인하기 위한 Compose Preview입니다.
 */
@Preview(showBackground = true)
@Composable
private fun MyFoldersGridTest() {
    MyFoldersGrid(
        folders = emptyList(),
        notCategorizationLinks = emptyList(),
        selectedTopFolderColorStyle = CategoryColorStyle.DEFAULT,
        isEditMode = false,
        onAddFolderClick = {},
        onFolderClick = {},
        onFolderEditClick = {},
        onChangeSharingClick = {},
        onDeleteFolder = {},
        onLinkClick = {},
        onDeleteNotCategorizationLink = {},
    )
}
