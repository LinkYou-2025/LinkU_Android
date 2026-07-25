package com.linku.file.ui.item.items

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.ui.item.FolderItemLayout

@Composable
fun EmptyFolderItemLayout(
    modifier: Modifier = Modifier,
    folderName: String = ""
) {
    val colors = MaterialTheme.linkuColors
    val color = CategoryColorStyle.DEFAULT

    FolderItemLayout(
        backgroundColor = color.color2,
        color1 = color.color3,
        color2 = color.color2,
        color3 = colors.white,
        folderMaskBrush = Brush.verticalGradient(
            colorStops = arrayOf(
                1.0f to colors.gray[100].copy(alpha = 0.7f),
                0.2f to colors.gray[200].copy(alpha = 1.0f),
            )
        ),
        leftIcon = {},
        rightIcon = {},
        textBackgroundColor = color.color4,
        folderName = folderName,
        modifier = modifier
    )
}
