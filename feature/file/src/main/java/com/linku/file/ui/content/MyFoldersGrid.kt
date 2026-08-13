package com.linku.file.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.LinkItemInfo
import com.linku.design.modal.ModalWindow
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.R
import com.linku.file.ui.item.LinkItemLayout
import com.linku.file.ui.item.items.EmptyFolderItemLayout
import com.linku.file.ui.item.items.MyFolderItemLayout

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
 * 하위 폴더는 편집 모드에 따라 수정/공유 상태 변경 액션을 제공하고, 삭제 모드에서는
 * 폴더 카드를 길게 눌러 기존 삭제 확인 모달을 엽니다. 링크도 길게 눌러 삭제할 수 있습니다.
 *
 * @param modifier 그리드 전체 컨테이너에 적용할 [Modifier]입니다.
 * @param contentPadding 그리드 내부 콘텐츠에 적용할 여백입니다.
 * @param folders 표시할 하위 폴더 목록입니다.
 * @param notCategorizationLinks 표시할 미분류 링크 목록입니다.
 * @param selectedTopFolderColorStyle 하위 폴더 카드에 적용할 선택된 최상위 폴더 색상 스타일입니다.
 * @param isEditMode 현재 편집 모드 여부입니다.
 * @param isDeleteMode 현재 폴더 삭제 대상 선택 모드 여부입니다.
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
    isDeleteMode: Boolean,
    onAddFolderClick: () -> Unit,
    onFolderClick: (FolderSimpleInfo) -> Unit,
    onFolderEditClick: (FolderSimpleInfo) -> Unit,
    onChangeSharingClick: (FolderSimpleInfo) -> Unit,
    onDeleteFolder: (FolderSimpleInfo) -> Unit,
    onLinkClick: (Long) -> Unit,
    onDeleteNotCategorizationLink: (Long) -> Unit
) {
    /** 섹션 제목과 삭제 모달 문구에 사용할 LinkU 테마 색상 팔레트입니다. */
    val colors = MaterialTheme.linkuColors

    /** 미분류 링크 카드 long click 이후 표시되는 삭제 확인 모달 상태입니다. */
    var deleteModalWindowVisible by remember { mutableStateOf(false) }

    /** 삭제 확인 모달에서 사용할 현재 선택된 미분류 링크 ID입니다. */
    var selectedLinkId by remember { mutableStateOf<Long?>(null) }

    /** contentPadding의 start/end 값을 현재 레이아웃 방향에 맞게 계산하기 위한 방향 정보입니다. */
    val layoutDirection = LocalLayoutDirection.current

    /**
     * 폴더 카드와 미분류 링크 카드를 하나의 2열 그리드에 함께 배치합니다.
     *
     * 하위 폴더 섹션과 미분류 링크 섹션은 같은 LazyVerticalGrid 안에서 순서만 나뉘며,
     * 카드 간격은 현재 화면의 가용 폭에 비례해서 계산합니다.
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

        /** 폴더 추가 카드, 하위 폴더 목록, 미분류 링크 섹션을 순서대로 배치합니다. */
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(INTER_LAYER_PADDING.dp),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
        ) {
            /** 첫 번째 셀은 항상 하위 폴더 추가 진입점으로 사용합니다. */
            item {
                AddBottomFolderItem(
                    modifier = Modifier
                        // 실제 폴더 카드와 동일한 셀 점유율을 사용해 그리드 정렬을 맞춥니다.
                        .fillMaxSize()
                        .noRippleClickable(
                            enabled = !isEditMode && !isDeleteMode,
                            role = Role.Button,
                            onClick = onAddFolderClick
                        )
                )
            }

            /** 선택된 최상위 폴더에 속한 하위 폴더 목록을 렌더링합니다. */
            items(folders, key = {it.folderId}) { folder ->
                MyFolderItem(
                    modifier = Modifier.fillMaxSize(),
                    folder = folder,
                    colorStyle = selectedTopFolderColorStyle,
                    isEditMode = isEditMode,
                    isDeleteMode = isDeleteMode,
                    deleteClickLabel = stringResource(
                        R.string.file_delete_folder_select_action,
                        folder.folderName
                    ),
                    onClick = {
                        // 일반 모드에서 하위 폴더를 눌렀을 때의 화면 전환/데이터 로딩은 상위로 위임합니다.
                        onFolderClick(folder)
                    },
                    onEdit = {
                        // 편집 아이콘 클릭 시 수정 대상 지정과 바텀시트 노출을 상위로 위임합니다.
                        onFolderEditClick(folder)
                    },
                    onChangeSharing = {
                        // 공유 상태 아이콘 클릭 시 공유 상태 변경 동작을 상위로 위임합니다.
                        onChangeSharingClick(folder)
                    },
                    onDelete = {
                        // 폴더 삭제 확인 후 실행할 실제 삭제 동작을 상위로 위임합니다.
                        onDeleteFolder(folder)
                    }
                )
            }

            /** 미분류 링크가 있을 때만 폴더 목록 아래에 별도 섹션으로 노출합니다. */
            if (notCategorizationLinks.isNotEmpty()) {
                /** 섹션 제목은 두 열을 모두 차지해야 하므로 maxLineSpan을 사용합니다. */
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

                /** 미분류 링크 목록을 폴더 카드 다음 셀부터 이어서 배치합니다. */
                items(notCategorizationLinks, key = { it.userLinkuId }) { link ->
                    LinkItemLayout(
                        modifier = Modifier.fillMaxSize(),
                        link = link,
                        onClick = {
                            // 미분류 링크도 일반 링크와 동일하게 상세 화면 이동 콜백으로 위임합니다.
                            onLinkClick(link.userLinkuId)
                        },
                        onLongClick = {
                            // 길게 누른 링크 ID를 저장한 뒤 삭제 확인 모달을 표시합니다.
                            selectedLinkId = link.userLinkuId
                            deleteModalWindowVisible = true
                        }
                    )
                }
            }
        }
    }

    // 미분류 링크 카드 long click 이후 실제 삭제를 한 번 더 확인하는 모달창입니다.
    ModalWindow(
        visible = deleteModalWindowVisible,
        onOkay = {
            // 확인 시점에 선택된 링크 ID가 남아 있을 때만 삭제 콜백을 실행합니다.
            selectedLinkId?.let { id ->
                onDeleteNotCategorizationLink(id)
            }
            // 모달을 닫으면서 선택 상태도 함께 비워 다음 삭제 동작과 섞이지 않게 합니다.
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
    /** 폴더 추가 아이템의 라벨 색상을 가져오기 위한 테마 색상입니다. */
    val colors = MaterialTheme.linkuColors

    /** 빈 폴더 카드 위에 아이콘과 텍스트를 겹쳐 올리는 오버레이 컨테이너입니다. */
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        /** 실제 폴더 카드와 같은 형태의 placeholder를 배경으로 사용합니다. */
        EmptyFolderItemLayout()

        /** 폴더 추가를 나타내는 아이콘을 카드 중앙 기준 위치에 배치합니다. */
        Image(
            modifier = Modifier.padding(top = 71.dp),
            painter = painterResource(R.drawable.add_folder_icon),
            contentDescription = null
        )

        /** 추가 아이콘과 함께 표시되는 폴더 추가 라벨입니다. */
        Text(
            text = "폴더 추가하기",
            fontSize = 15.sp,
            fontWeight = FontWeight(500),
            color = colors.black,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 하위 폴더 카드의 클릭, 길게 누르기, 삭제 확인 모달을 함께 처리하는 래퍼입니다.
 *
 * 일반 모드에서는 클릭 시 하위 폴더의 링크 화면으로 이동하고, 편집 모드에서는 카드 내부의
 * 편집/공유 아이콘 액션만 활성화합니다. 삭제 모드에서는 일반 클릭을 소비하되 아무 동작도
 * 하지 않고, 길게 누른 경우에만 삭제 확인 모달을 표시합니다.
 *
 * @param folder 표시할 하위 폴더 정보입니다.
 * @param colorStyle 선택된 최상위 폴더에서 파생된 폴더 카드 색상 스타일입니다.
 * @param isEditMode 편집 모드 활성화 여부입니다.
 * @param isDeleteMode 삭제 대상 선택 모드 활성화 여부입니다.
 * @param deleteClickLabel 접근성 서비스에 제공할 카드 길게 누르기 동작 설명입니다.
 * @param onClick 일반 모드에서 폴더 카드를 눌렀을 때 실행할 동작입니다.
 * @param onEdit 편집 모드에서 편집 아이콘을 눌렀을 때 실행할 동작입니다.
 * @param onChangeSharing 편집 모드에서 공유 상태 아이콘을 눌렀을 때 실행할 동작입니다.
 * @param onDelete 삭제 확인 모달에서 확인을 눌렀을 때 실행할 동작입니다.
 */
@Composable
private fun MyFolderItem(
    modifier: Modifier,
    folder: FolderSimpleInfo,
    colorStyle: CategoryColorStyle,
    isEditMode: Boolean,
    isDeleteMode: Boolean,
    deleteClickLabel: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onChangeSharing: () -> Unit,
    onDelete: () -> Unit
) {
    /** 폴더 삭제 확인 모달 문구에 사용할 LinkU 테마 색상 팔레트입니다. */
    val colors = MaterialTheme.linkuColors

    /** 하위 폴더를 길게 눌렀을 때 표시되는 삭제 확인 모달 상태입니다. */
    var deleteModalWindowVisible by remember(isDeleteMode) { mutableStateOf(false) }

    val interactionModifier = when {
        isDeleteMode -> {
            Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            // 삭제 모드의 일반 클릭은 의도적으로 소비만 합니다.
                        },
                        onLongPress = { deleteModalWindowVisible = true },
                    )
                }
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    onLongClick(label = deleteClickLabel) {
                        deleteModalWindowVisible = true
                        true
                    }
                }
        }

        // 수정 모드에서는 카드 루트가 입력을 처리하지 않고 내부 수정 아이콘만 활성화합니다.
        isEditMode -> Modifier

        // 일반 상태에서는 폴더 진입만 허용하며 삭제 long-click semantics를 제공하지 않습니다.
        else -> Modifier.noRippleClickable(
            role = Role.Button,
            onClick = onClick,
        )
    }

    /** 실제 폴더 카드 UI는 공통 카드 레이아웃 컴포저블에 위임합니다. */
    MyFolderItemLayout(
        // 기존 카드 측정 순서를 유지하면서 카드 전체에 상태별 입력 정책을 적용합니다.
        modifier = interactionModifier,
        colorStyle = colorStyle,
        folder = folder,
        isEditMode = isEditMode,
        isDeleteMode = isDeleteMode,
        onEdit = onEdit,
        onChangeSharing = onChangeSharing
    )


    // 하위 폴더 long click 이후 실제 삭제를 한 번 더 확인하는 모달창입니다.
    ModalWindow(
        visible = isDeleteMode && deleteModalWindowVisible,
        onOkay = {
            // 모드가 유지된 상태에서 확인한 경우에만 상위 삭제 동작을 실행합니다.
            if (isDeleteMode) onDelete()
            deleteModalWindowVisible = false
        },
        onDismiss = { deleteModalWindowVisible = false },
        positiveText = stringResource(R.string.file_delete_folder_dialog_confirm),
        negativeText = stringResource(R.string.file_delete_folder_dialog_cancel),
        title = stringResource(R.string.file_delete_folder_dialog_title)
    ) {
        Text(
            text = stringResource(R.string.file_delete_folder_dialog_message),
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight(400),
            color = colors.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun MyFoldersGridPreview() {
    val folders = listOf(
        FolderSimpleInfo(1, "Folder 1", 0, false),
        FolderSimpleInfo(2, "Folder 2", 0, true),
        FolderSimpleInfo(3, "Folder 3", 0, false)
    )
    val links = listOf(
        LinkItemInfo(1, 0, "Link 1", "https://example.com/1", emptyList(), null, null),
        LinkItemInfo(2, 0, "Link 2", "https://example.com/2", emptyList(), null, null),
        LinkItemInfo(3, 0, "Link 3", "https://example.com/3", emptyList(), null, null)
    )

    LinkuPreview {
        MyFoldersGrid(
            folders = folders,
            notCategorizationLinks = links,
            selectedTopFolderColorStyle = CategoryColorStyle.DEFAULT,
            isEditMode = false,
            isDeleteMode = false,
            onAddFolderClick = {},
            onFolderClick = {},
            onFolderEditClick = {},
            onChangeSharingClick = {},
            onDeleteFolder = {},
            onLinkClick = {},
            onDeleteNotCategorizationLink = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun MyFoldersGridEditModePreview() {
    val folders = listOf(
        FolderSimpleInfo(1, "Folder 1", 0, false),
        FolderSimpleInfo(2, "Folder 2", 0, true),
        FolderSimpleInfo(3, "Folder 3", 0, false)
    )
    val links = listOf(
        LinkItemInfo(1, 0, "Link 1", "https://example.com/1", emptyList(), null, null),
        LinkItemInfo(2, 0, "Link 2", "https://example.com/2", emptyList(), null, null),
        LinkItemInfo(3, 0, "Link 3", "https://example.com/3", emptyList(), null, null)
    )

    LinkuPreview {
        MyFoldersGrid(
            folders = folders,
            notCategorizationLinks = links,
            selectedTopFolderColorStyle = CategoryColorStyle.DEFAULT,
            isEditMode = true,
            isDeleteMode = false,
            onAddFolderClick = {},
            onFolderClick = {},
            onFolderEditClick = {},
            onChangeSharingClick = {},
            onDeleteFolder = {},
            onLinkClick = {},
            onDeleteNotCategorizationLink = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun MyFoldersGridDeleteModePreview() {
    val folders = listOf(
        FolderSimpleInfo(1, "Folder 1", 0, false),
        FolderSimpleInfo(2, "Folder 2", 0, true),
        FolderSimpleInfo(3, "Folder 3", 0, false)
    )

    LinkuPreview {
        MyFoldersGrid(
            folders = folders,
            notCategorizationLinks = emptyList(),
            selectedTopFolderColorStyle = CategoryColorStyle.DEFAULT,
            isEditMode = false,
            isDeleteMode = true,
            onAddFolderClick = {},
            onFolderClick = {},
            onFolderEditClick = {},
            onChangeSharingClick = {},
            onDeleteFolder = {},
            onLinkClick = {},
            onDeleteNotCategorizationLink = {}
        )
    }
}
