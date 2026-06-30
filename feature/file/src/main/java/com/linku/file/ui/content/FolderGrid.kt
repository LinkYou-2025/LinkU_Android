package com.linku.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.linku.core.model.FolderSimpleInfo

internal inline fun LazyGridScope.FolderGrid(
    modifier: Modifier = Modifier,
    folderList: List<FolderSimpleInfo>,
    itemIndexOffset: Int = 0,
    crossinline folderLayout: @Composable (folder: FolderSimpleInfo) -> Unit
) = itemsIndexed(folderList) { index, folder ->
        val itemIndex = index + itemIndexOffset

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (itemIndex % 2 == 0) Arrangement.Start else Arrangement.End
        ) {
            folderLayout(folder)
        }
    }

