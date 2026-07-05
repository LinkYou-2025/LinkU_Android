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

@Composable
fun MyFolderItemLayout(
    modifier: Modifier = Modifier,
    colorStyle: CategoryColorStyle,
    folder: FolderSimpleInfo,
    isEditMode: Boolean = false,
    onEdit: () -> Unit = {},
    onChangeSharing: () -> Unit = {}
) {
    val colors = MaterialTheme.linkuColors

    FolderItemLayout(
        backgroundColor = colorStyle.color1,
        color1 = colorStyle.color2,
        color2 = colorStyle.color1,
        color3 = colors.white,
        folderMaskBrush = colorStyle.verticalGradient(),
        leftIcon = {
            Box(
                modifier = Modifier.noRippleClickable {
                    if (isEditMode) {
                        onChangeSharing()
                    }
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
            if (isEditMode) {
                Box(
                    modifier = Modifier.noRippleClickable {
                        onEdit()
                    }
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
