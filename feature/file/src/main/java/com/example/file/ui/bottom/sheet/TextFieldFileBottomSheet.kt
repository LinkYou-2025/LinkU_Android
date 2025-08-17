package com.example.file.ui.bottom.sheet


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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.file.R
import com.example.design.modifier.noRippleClickable
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.CategoryColorStyle
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray300
import com.example.file.ui.theme.Gray400
import com.example.file.ui.theme.Gray600
import com.example.file.ui.theme.Gray800
import com.example.file.ui.theme.MainColor
import com.example.file.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldFileBottomSheet(
    title: String,
    body: String,
    placeholderText: String,
    isEditable: Boolean = false,
    visible: Boolean,
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
                    text = "색상",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontFamily = DefaultFont,
                    fontWeight = FontWeight.Normal,
                    color = Gray800,
                )
                Text(
                    modifier = Modifier
                        .padding(start = 1.dp),
                    text = "(색상은 한 번 지정하면 변경 불가합니다)",
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontFamily = DefaultFont,
                    fontWeight = FontWeight.Normal,
                    color = Gray400,
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
                        modifier = Modifier.width(15.dp),
                        imageVector = Icons.Default.Check,
                        tint = White,
                        contentDescription = null
                    )
                }

                val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "")

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
                    .padding(top = 14.dp)
                    .padding(horizontal = 26.5.dp),
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                VerticalGrid(
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

@Preview(showBackground = true, heightDp = 2000)
@Composable
private fun TextFieldFileBottomSheetTest(){
    TextFieldFileBottomSheet(
        "해당 카테고리를 수정하시겠습니까?",
        "새 카테고리명을 입력하고 대표 색상을 지정해주세요!",
        "카테고리명은 최대 10자입니다",
        true,
        true,
    ){}
}
