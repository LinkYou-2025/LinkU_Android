package com.linku.file.ui.bottom.sheet


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.linku.file.R
import com.linku.design.modifier.noRippleClickable
import com.linku.file.ui.theme.Black
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.file.ui.theme.DefaultFont
import com.linku.file.ui.theme.Gray300
import com.linku.file.ui.theme.Gray400
import com.linku.file.ui.theme.Gray600
import com.linku.file.ui.theme.Gray800
import com.linku.file.ui.theme.MainColor
import com.linku.file.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldFileBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    body: String,
    placeholderText: String,
    isEditable: Boolean = false,
    visible: Boolean,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onTextDeliver: (String) -> Unit = {},
    onColorIdDeliver: (Int) -> Unit = {},
    onDismiss: () -> Unit,
){
    var colorId by remember { mutableIntStateOf(-1) }
    var expanded by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(Gray300) }
    var text by remember { mutableStateOf("") }

    LaunchedEffect(visible) {
        if (visible) {
            text = "" // 바텀 시트 열릴 때 초기화
        }
    }

    FileBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        title = title,
        body = body,
        buttonText = "저장",
        visible = visible,
        isReady = if(isEditable) colorId != -1 else text.isNotEmpty(),
        onOkay = {
            onTextDeliver(text)
            if(isEditable) {
                onColorIdDeliver(colorId)
            }
        },
        onDismiss = {
            selectedColor = Gray300
            expanded = false
            onDismiss()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(1.dp, MainColor, RoundedCornerShape(18.dp))
                .padding(horizontal = 21.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                enabled = !isEditable,
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Black,
                    fontFamily = DefaultFont,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 입력값이 비어 있으면 placeholder 보여줌
                        if (text.isEmpty()) {
                            Text(
                                text = " $placeholderText", // placeholder
                                color = Gray400,
                                fontSize = 14.sp,
                                fontFamily = DefaultFont,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                        innerTextField() // 실제 입력란
                    }
                }
            )

        }

        if (isEditable) {
            Spacer(modifier = Modifier.height(19.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "색상 변경",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontFamily = DefaultFont,
                    fontWeight = FontWeight.Normal,
                    color = Gray800,
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape)
                        .background(selectedColor),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        modifier = Modifier.fillMaxWidth(0.45f),
                        painter = painterResource(R.drawable.ic_top_folders_menu),
                        tint = White,
                        contentDescription = null
                    )
                }

                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f,
                    label = "화살표 회전 애니메이션"
                )

                val modifier = if(expanded) Modifier
                    .padding(start = 10.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(MainColor, blendMode = BlendMode.SrcAtop)
                        }
                    } else Modifier.padding(start = 10.dp)
                Icon(
                    modifier = modifier
                        .rotate(rotation)
                        .noRippleClickable { expanded = !expanded },
                    tint = Gray600,
                    painter = painterResource(id = R.drawable.check_img),
                    contentDescription = "아래 화살표"
                )
            }
            AnimatedVisibility(
                modifier = Modifier
                    .padding(horizontal = 26.5.dp),
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                VerticalGrid(
                    modifier = Modifier
                        .padding(top = 14.dp),
                    columns = SimpleGridCells.Fixed(8),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(7.5.dp)
                ) {
                    for ((i, colorStyle) in CategoryColorStyle.categoryStyleList.withIndex()) {

                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(CircleShape)
                                    .background(colorStyle.color4)
                                    .align(Alignment.Center)
                                    .noRippleClickable {
                                        selectedColor = colorStyle.color4
                                        colorId = i
                                   },
                                contentAlignment = Alignment.Center
                            ) {}
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, heightDp = 2000)
@Composable
private fun TextFieldFileBottomSheetTest(){
    TextFieldFileBottomSheet(
        modifier = Modifier.height(900.dp),
        "해당 카테고리를 수정하시겠습니까?",
        "새 카테고리명을 입력하고 대표 색상을 지정해주세요!",
        "카테고리명은 최대 10자입니다",
        true,
        true,
    ){}
}
