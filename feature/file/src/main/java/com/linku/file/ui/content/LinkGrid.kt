package com.linku.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.linku.core.model.LinkItemInfo

internal inline fun LazyGridScope.LinkGrid(
    modifier: Modifier = Modifier,
    linkList: List<LinkItemInfo>,
    itemIndexOffset: Int = 0,
    crossinline linkLayout: @Composable (LinkItemInfo) -> Unit
) = itemsIndexed(linkList) { index, link ->
        val itemIndex = index + itemIndexOffset

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (itemIndex % 2 == 0) Arrangement.Start else Arrangement.End
        ) {
            linkLayout(link)
        }
    }