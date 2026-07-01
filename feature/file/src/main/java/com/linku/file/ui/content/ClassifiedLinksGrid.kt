package com.linku.file.ui.content

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linku.design.modal.ModalWindow
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.linkuColors
import com.linku.file.FileViewModel
import com.linku.file.R
import com.linku.file.ui.item.LinkItemLayout
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

private const val INTER_LAYER_PADDING = 18.51
private const val ITEM_RATIO = 10f / 174f

@Composable
internal fun ClassifiedLinksGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp),
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
){
    val colors = MaterialTheme.linkuColors
    val linkList by fileViewModel.links.collectAsStateWithLifecycle()

    val notCategorizationLinks by fileViewModel.notCategorizationLinks.collectAsStateWithLifecycle()

    var categorizationModalWindowVisible by remember { mutableStateOf(false) }
    var deleteModalWindowVisible by remember { mutableStateOf(false) }

    var selectedLinkId by remember { mutableStateOf<Long?>(null) }

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
                AddLinkItem(
                    modifier = Modifier
                        .fillMaxSize(164f / 174f)
                        .noRippleClickable {
                            Log.d("LinksGrid", "링크 추가하기 클릭")
                            if (notCategorizationLinks.isNotEmpty()) {
                                folderStateViewModel.updateLinkCategorizationBottomSheetVisible(true)
                            } else {
                                categorizationModalWindowVisible = true
                            }
                        }
                )
            }

            items(linkList) { link ->
                LinkItemLayout(
                    modifier = Modifier.fillMaxSize(164f / 174f),
                    link = link,
                    onClick = {
                        fileViewModel.onLinkClick?.invoke(link.linkuId)
                    },
                    onLongClick = {
                        selectedLinkId = link.linkuId
                        deleteModalWindowVisible = true
                    }
                )
            }
        }
    }


    // 분류되지 않는 링크가 없으면 뜨는 모달창
    ModalWindow(
        visible = categorizationModalWindowVisible,
        onDismiss = { categorizationModalWindowVisible = false },
        title = "분류되지 않은 링크가 없습니다.",
        positiveText = "확인"
    ) {
        Text(
            text = "새 링크를 저장한 뒤 분류해보세요!",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal,
            color = colors.gray[600],
            textAlign = TextAlign.Center
        )
    }

    // 링크 삭제 모달창
    ModalWindow(
        visible = deleteModalWindowVisible,
        onOkay = {
            // ✅ 확인에서 안전하게 현재 선택된 id로 삭제
            selectedLinkId?.let { id ->
                fileViewModel.deleteLink(id)
            }
            // 상태 정리
            deleteModalWindowVisible = false
            selectedLinkId = null
        },
        onDismiss = { deleteModalWindowVisible = false },
        title = "해당 링크를 삭제하시겠습니까?",
        positiveText = "삭제하기",
        negativeText = "취소하기"
    ) {
        Text(
            text = "삭제 시 해당 링크가 영구적으로 제거되며\n복구가 불가능합니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight(400),
            color = colors.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AddLinkItem(
    modifier: Modifier
) {
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier.alpha(1f),
            ) {
                LinkItemLayout(
                    link = null
                )
            }

            Image(
                modifier = Modifier.padding(top = 103.dp),
                painter = painterResource(R.drawable.add_folder_icon),
                contentDescription = null
            )

            Text(
                modifier = Modifier.padding(top = 147.dp),
                text = "링크 추가하기",
                fontSize = 15.sp,
                fontWeight = FontWeight(500),
                color = colors.black,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClassifiedLinksGridTest(){
    ClassifiedLinksGrid(
        fileViewModel = viewModel(),
        folderStateViewModel = viewModel(),
    )
}
