package com.linku.file.ui.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.design.modal.ModalWindow
import com.linku.design.theme.linkuColors
import com.linku.file.FileViewModel
import com.linku.file.ui.item.items.EmptyFolderItemLayout
import com.linku.file.viewmodel.edit.state.EditStateViewModel
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

private const val INTER_LAYER_PADDING = 18.51
private const val ITEM_RATIO = 10f / 174f

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SharedBottomFolderGrid(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    editStateViewModel: EditStateViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp)
) {
    val colors = MaterialTheme.linkuColors
    val folderList by fileViewModel.sharedBottomFolders.collectAsStateWithLifecycle()
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
            items(folderList, key = { it.folderId }) { folder ->
                var deleteModalVisible by remember { mutableStateOf(false) }
                val interactionSource = remember { MutableInteractionSource() }

                EmptyFolderItemLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(
                            indication = null,
                            interactionSource = interactionSource,
                            onClick = {
                                fileViewModel.getLinks(folder.folderId)
                                folderStateViewModel.updateSelectedBottomSharedFolder(folder)
                                folderStateViewModel.updateFolderState(FolderState.LINKS)
                            },
                            onLongClick = {
                                deleteModalVisible = true
                            }
                        ),
                    folderName = folder.folderName
                )

                ModalWindow(
                    visible = deleteModalVisible,
                    onOkay = {
                        fileViewModel.deleteSharedFolder(folder.folderId)
                        deleteModalVisible = false
                    },
                    onDismiss = { deleteModalVisible = false },
                    positiveText = "\uC0AD\uC81C\uD558\uAE30",
                    title = "\uACF5\uC720\uBC1B\uC740 \uD3F4\uB354\uB97C \uC0AD\uC81C\uD558\uC2DC\uACA0\uC2B5\uB2C8\uAE4C?"
                ) {
                    Text(
                        text = "\uC0AD\uC81C \uC2DC \uACF5\uC720\uBC1B\uC740 \uD3F4\uB354 \uBAA9\uB85D\uC5D0\uC11C \uC81C\uAC70\uB429\uB2C8\uB2E4.",
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight(400),
                        color = colors.gray[600],
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
