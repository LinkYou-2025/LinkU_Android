package com.linku.file.ui.item.items

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.ui.item.BookMarkStar
import com.linku.file.ui.item.FolderItemLayout
import com.linku.file.ui.item.PencilIcon

@Composable
fun CategoryItemLayout(
    modifier: Modifier = Modifier,
    colorStyle: CategoryColorStyle,
    folder: FolderSimpleInfo,
    visibleBookmarked: Boolean = true,
    isEditMode: Boolean = false,
    onBookmark: () -> Unit
) {
    val colors = MaterialTheme.linkuColors

    FolderItemLayout(
        backgroundColor = colors.gray[200],
        color1 = colorStyle.color3,
        color2 = colorStyle.color2,
        color3 = colorStyle.color1,
        folderMaskBrush = Brush.verticalGradient(
            colorStops = arrayOf(
                1.0f to colors.gray[100].copy(alpha = 0.7f),
                0.2f to colors.gray[200].copy(alpha = 1.0f),
            )
        ),
        leftIcon = {},
        rightIcon = {
            if (visibleBookmarked) {
                if (isEditMode) {
                    Box(
                        modifier = Modifier
                    ) {
                        PencilIcon(colorStyle.color2)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .noRippleClickable {
                                onBookmark()
                            }
                    ) {
                        BookMarkStar(folder.isBookmarked)
                    }
                }
            }
        },
        textBackgroundColor = colorStyle.color4,
        folderName = folder.folderName,
        modifier = modifier
    )
}
