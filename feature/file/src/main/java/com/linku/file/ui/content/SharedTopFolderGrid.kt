package com.linku.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.linkuColors
import com.linku.file.FileViewModel
import com.linku.file.ui.item.items.EmptyFolderItemLayout
import com.linku.file.viewmodel.edit.state.EditStateViewModel
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

private const val INTER_LAYER_PADDING = 18.51
private const val ITEM_RATIO = 10f / 174f
private const val SECTION_TITLE_TOP_PADDING = 21.49
private const val SECTION_TITLE_BOTTOM_PADDING = 1.49

@Suppress("UNUSED_PARAMETER")
@Composable
fun SharedTopFolderGrid(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    editStateViewModel: EditStateViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp)
) {
    val colors = MaterialTheme.linkuColors
    val folderList by fileViewModel.sharedTopFolders.collectAsStateWithLifecycle()
    val layoutDirection = LocalLayoutDirection.current

    BoxWithConstraints(
        modifier = modifier
    ) {
        val horizontalPadding =
            contentPadding.calculateStartPadding(layoutDirection) +
                    contentPadding.calculateEndPadding(layoutDirection)
        val availableWidth = maxWidth - horizontalPadding
        val horizontalSpacing = availableWidth * ITEM_RATIO

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(INTER_LAYER_PADDING.dp),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
        ) {
            item {
                EmptyFolderItemLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable {
                            folderStateViewModel.updateIsSharedFolders(false)
                            folderStateViewModel.updateSelectedSharedFolder(null)
                            folderStateViewModel.updateFolderState(FolderState.TOP)
                        },
                    folderName = "\uB098\uC758 \uD3F4\uB354"
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.padding(
                        top = SECTION_TITLE_TOP_PADDING.dp,
                        bottom = SECTION_TITLE_BOTTOM_PADDING.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\uACF5\uC720\uBC1B\uC740 \uD3F4\uB354",
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight(700),
                        color = colors.black
                    )
                    Text(
                        text = folderList.size.toString(),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(500),
                        color = colors.gray[600]
                    )
                }
            }

            items(folderList, key = { it.userId }) { folder ->
                EmptyFolderItemLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable {
                            folderStateViewModel.updateSelectedSharedFolder(folder)
                            fileViewModel.getSharedBottomFolders(folder)
                            folderStateViewModel.updateFolderState(FolderState.BOTTOM)
                        },
                    folderName = "${folder.nickname}\uC758 \uD3F4\uB354"
                )
            }
        }
    }
}
