package com.linku.file.ui.item.items

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.linku.design.theme.linkuColors
import com.linku.file.ui.item.FolderItemLayout

@Composable
fun EmptyFolderItemLayout(
    modifier: Modifier = Modifier,
    folderName: String = ""
) {
    val colors = MaterialTheme.linkuColors

    FolderItemLayout(
        backgroundColor = colors.gray[200],
        color1 = colors.gray[300],
        color2 = colors.gray[200],
        color3 = colors.white,
        folderMaskBrush = Brush.verticalGradient(
            colorStops = arrayOf(
                1.0f to colors.gray[100].copy(alpha = 0.7f),
                0.2f to colors.gray[200].copy(alpha = 1.0f),
            )
        ),
        leftIcon = {},
        rightIcon = {},
        textBackgroundColor = colors.gray[500],
        folderName = folderName,
        modifier = modifier
    )
}
