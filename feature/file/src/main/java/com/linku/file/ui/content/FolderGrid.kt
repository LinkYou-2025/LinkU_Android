package com.linku.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.theme.color.CategoryColorStyle

private const val MIDDLE_PADDING = 18.51

@Composable
internal fun FolderGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp),
    folderList: List<FolderSimpleInfo>,
    categoryColorMap: Map<String, CategoryColorStyle>,
    folderLayout: @Composable (folder: FolderSimpleInfo, colorStyle: CategoryColorStyle) -> Unit
){
    LazyVerticalGrid(
        modifier = modifier,
        contentPadding = contentPadding,
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(MIDDLE_PADDING.dp)
    ) {
        itemsIndexed(folderList){ index, folder ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = if(index % 2 == 0) Arrangement.Start else Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                folderLayout(
                    folder,
                    categoryColorMap[folder.folderName] ?: CategoryColorStyle.DEFAULT
                )
            }
        }
    }
}