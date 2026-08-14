package com.linku.file.ui.item.items

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.ui.item.FolderItemLayout
import com.linku.file.ui.item.LockFolderIcon
import com.linku.file.ui.item.PencilIcon
import com.linku.file.ui.item.ShareFolderIcon

/**
 * 내 폴더 카드를 편집 또는 삭제 대상 선택 상태에 맞게 표시합니다.
 *
 * 편집 모드에서는 공유 상태와 연필 아이콘이 각각 독립 액션을 수행합니다. 삭제 모드에서는
 * 편집용 연필 아이콘을 표시하지 않으며, 아이콘 슬롯이 카드 전체의 길게 누르기 입력을
 * 가로채지 않도록 합니다.
 *
 * @param isEditMode 폴더 수정 및 공유 상태 변경 액션의 활성화 여부입니다.
 * @param isDeleteMode 폴더 삭제 대상 선택 모드 여부입니다.
 * @param onEdit 편집 모드에서 연필 아이콘을 눌렀을 때 실행할 동작입니다.
 * @param onChangeSharing 편집 모드에서 공유 상태 아이콘을 눌렀을 때 실행할 동작입니다.
 */
@Composable
fun MyFolderItemLayout(
    modifier: Modifier = Modifier,
    colorStyle: CategoryColorStyle,
    folder: FolderSimpleInfo,
    isEditMode: Boolean = false,
    isDeleteMode: Boolean = false,
    onEdit: () -> Unit = {},
    onChangeSharing: () -> Unit = {}
) {
    val colors = MaterialTheme.linkuColors
    // 잘못된 외부 상태로 두 모드가 겹쳐도 삭제 선택이 수정·공유 동작보다 우선합니다.
    val editActionsEnabled = isEditMode && !isDeleteMode

    FolderItemLayout(
        backgroundColor = colorStyle.color1,
        color1 = colorStyle.color2,
        color2 = colorStyle.color1,
        color3 = colors.white,
        folderMaskBrush = colorStyle.verticalGradient(),
        leftIcon = {
            Box(
                modifier = when {
                    editActionsEnabled -> Modifier.noRippleClickable(onClick = onChangeSharing)
                    // 삭제 모드에서는 카드 전체가 길게 누르기를 받아야 하므로 자식이 소비하지 않습니다.
                    isDeleteMode -> Modifier
                    // 일반 상태에서는 기존처럼 아이콘 탭을 소비하되 별도 동작은 하지 않습니다.
                    else -> Modifier.noRippleClickable(onClick = {})
                }
            ) {
                folder.isSharing?.let {
                    when (it) {
                        "share" -> ShareFolderIcon(colorStyle.color2)
                        "personal" -> LockFolderIcon(colorStyle.color2)
                        else -> {}
                    }
                }
            }
        },
        rightIcon = {
            if (editActionsEnabled) {
                Box(
                    modifier = Modifier.noRippleClickable(onClick = onEdit)
                ) {
                    PencilIcon(colorStyle.color2)
                }
            }
        },
        textBackgroundColor = colorStyle.color4,
        folderName = folder.folderName,
        modifier = modifier
    )
}
