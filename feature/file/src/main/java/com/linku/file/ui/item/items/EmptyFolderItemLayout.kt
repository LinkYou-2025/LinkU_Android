package com.linku.file.ui.item.items

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.ui.item.FolderItemLayout

@Composable
fun EmptyFolderItemLayout(
    modifier: Modifier = Modifier,
    folderName: String = ""
) {
    val color = CategoryColorStyle.DEFAULT

    FolderItemLayout(
        backgroundColor = color.color2,
        color1 = color.color3,
        color2 = color.color2,
        color3 = MaterialTheme.linkuColors.white,
        folderMaskBrush = color.verticalGradient(),
        leftIcon = {},
        rightIcon = {},
        textBackgroundColor = color.color4,
        folderName = folderName,
        modifier = modifier
    )
}
