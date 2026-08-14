package com.linku.file.ui.bottom.sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.FileViewModel
import com.linku.file.R
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

/**
 * 선택한 카테고리의 대표 색상을 변경하는 바텀시트 진입점입니다.
 *
 * 카테고리 이름은 안내용으로만 표시하며, 선택한 색상 index를 서버용 ID와
 * 화면용 [CategoryColorStyle]로 변환하는 기능 로직은 이 진입점에 유지합니다.
 *
 * @param onUpdateFinished 색상 수정 요청이 성공 또는 실패로 끝났을 때 호출되는 콜백입니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoryEditBottomSheet(
    folderStateViewModel: FolderStateViewModel,
    fileViewModel: FileViewModel,
    onUpdateFinished: () -> Unit,
){
    val colors = MaterialTheme.linkuColors
    var colorId by remember { mutableIntStateOf(-1) }
    var expanded by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(colors.gray[300]) }

    FileBottomSheet(
        title = "해당 카테고리를 수정하시겠습니까?",
        body = "새 카테고리명을 입력하고 대표 색상을 지정해주세요!",
        buttonText = "저장",
        visible = folderStateViewModel.topFolderEditBottomSheetVisible,
        isReady = colorId != -1,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        onOkay = {
            fileViewModel.updateCategoryColor(
                categoryName = folderStateViewModel.readyToUpdateTopFolder!!.folderName,
                colorId = (colorId + 1).toLong(),
                colorStyle = CategoryColorStyle.categoryStyleList[colorId],
                onFinished = onUpdateFinished,
            )
        },
        onDismiss = {
            colorId = -1
            selectedColor = colors.gray[300]
            expanded = false
            folderStateViewModel.updateTopFolderEditBottomSheetVisible(false)
        },
    ) {
        FileBottomSheetTextField(
            value = "",
            onValueChange = {},
            placeholderText = "카테고리명은 현재 변경 불가능합니다.",
            enabled = false,
        )

        Spacer(modifier = Modifier.height(19.dp))

        CategoryColorSelector(
            selectedColor = selectedColor,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            onColorSelected = { selectedId, color ->
                selectedColor = color
                colorId = selectedId
            },
        )
    }
}

@Composable
private fun CategoryColorSelector(
    selectedColor: Color,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onColorSelected: (Int, Color) -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "색상 변경",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal,
            color = colors.gray[800],
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(25.dp)
                .clip(CircleShape)
                .background(selectedColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.fillMaxWidth(0.45f),
                painter = painterResource(R.drawable.ic_top_folders_menu),
                tint = colors.white,
                contentDescription = null,
            )
        }

        val rotation by animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            label = "화살표 회전 애니메이션",
        )

        val arrowModifier = Modifier
            .padding(start = 10.dp)
            .graphicsLayer {
                // 상태 전환에도 modifier 구조를 유지하되, 그라데이션을 그릴 때만 합성을 격리합니다.
                compositingStrategy = if (expanded) {
                    CompositingStrategy.Offscreen
                } else {
                    CompositingStrategy.Auto
                }
            }
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    if (expanded) {
                        drawRect(colors.maincolor, blendMode = BlendMode.SrcIn)
                    }
                }
            }

        Icon(
            modifier = arrowModifier
                .rotate(rotation)
                .noRippleClickable { onExpandedChange(!expanded) },
            tint = colors.gray[600],
            painter = painterResource(id = R.drawable.check_img),
            contentDescription = "아래 화살표",
        )
    }

    AnimatedVisibility(
        modifier = Modifier.padding(horizontal = 26.5.dp),
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        VerticalGrid(
            modifier = Modifier.padding(top = 14.dp),
            columns = SimpleGridCells.Fixed(8),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(7.5.dp),
        ) {
            for ((index, colorStyle) in CategoryColorStyle.categoryStyleList.withIndex()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .clip(CircleShape)
                            .background(colorStyle.color4)
                            .align(Alignment.Center)
                            .noRippleClickable {
                                onColorSelected(index, colorStyle.color4)
                            },
                        contentAlignment = Alignment.Center,
                    ) {}
                }
            }
        }
    }
}
