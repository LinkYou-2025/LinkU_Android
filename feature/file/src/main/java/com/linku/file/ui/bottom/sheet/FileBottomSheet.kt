package com.linku.file.ui.bottom.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.linkuColors
import com.linku.file.R

private val FileBottomSheetShape = RoundedCornerShape(
    topStart = 30.dp,
    topEnd = 30.dp,
)
private val FileBottomSheetControlShape = RoundedCornerShape(18.dp)

private val FileBottomSheetButtonShadow = Shadow(
    radius = 10.dp,
    spread = 0.dp,
    offset = DpOffset(x = 0.dp, y = 4.dp),
    color = Color(0xFF7C7C7C),
    alpha = 0.25f,
)

/**
 * 파일 기능의 바텀시트가 공유하는 제목, 본문, 확인 버튼과 ModalBottomSheet 셸입니다.
 *
 * 확인 버튼은 [isReady]일 때만 동작하며 [onOkay]를 먼저 호출한 뒤 [onDismiss]를
 * 호출합니다. 기능별 상태 변경과 비동기 작업은 이 셸이 아니라 호출자가 담당합니다.
 *
 * @param modifier ModalBottomSheet에 적용할 수정자입니다.
 * @param sheetState 바텀시트의 펼침 상태입니다.
 * @param title 바텀시트 제목입니다.
 * @param body 제목 아래에 표시할 안내 문구입니다.
 * @param buttonText 확인 버튼 문구입니다.
 * @param visible 바텀시트 표시 여부입니다.
 * @param isReady 확인 버튼 활성화 여부입니다.
 * @param onOkay 활성화된 확인 버튼을 눌렀을 때 먼저 호출되는 콜백입니다.
 * @param onDismiss 바텀시트를 닫을 때 호출되는 콜백입니다.
 * @param content 기능별 본문 UI입니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FileBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    title: String,
    body: String,
    buttonText: String,
    visible: Boolean,
    isReady: Boolean = true,
    onOkay: () -> Unit = {},
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.linkuColors
    
    if(visible) {
        ModalBottomSheet(
            // ModalBottomSheet는 전달된 modifier 뒤에 드래그 위치 보정을 추가합니다.
            // 여기서 외곽 그림자를 그리면 닫힘 중 Dialog 원점에 남으므로 적용하지 않습니다.
            modifier = modifier,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 15.dp, bottom = 16.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(colors.gray[300]),
                )
            },

            // 딤 효과 수치
            scrimColor = colors.black.copy(alpha = 0.5f),
            sheetState = sheetState,
            onDismissRequest = onDismiss,
            shape = FileBottomSheetShape,
            tonalElevation = 0.dp,
            containerColor = colors.white,
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 7.dp)
                    .padding(bottom = 20.dp)
                    .padding(horizontal = 20.dp),
            ) {

                Text(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    text = title,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight(500),
                    color = colors.black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    text = body,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.gray[600],
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(19.dp))

                content()

                Spacer(modifier = Modifier.height(40.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .then(
                            if (isReady) {
                                Modifier.dropShadow(
                                    shape = FileBottomSheetControlShape,
                                    shadow = FileBottomSheetButtonShadow,
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clip(shape = FileBottomSheetControlShape)
                        .then(
                            if (isReady) {
                                Modifier.background(colors.maincolor)
                            } else {
                                Modifier.background(colors.gray[300])
                            }
                        )
                        .noRippleClickable(
                            enabled = isReady,
                            role = Role.Button,
                        ) {
                            onOkay()
                            onDismiss()
                        }
                ){
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = buttonText,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(700),
                        color = colors.white,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, heightDp = 2000)
@Composable
private fun FileEditBottomSheetTest(){
    val colors = MaterialTheme.linkuColors
    FileBottomSheet(
        modifier = Modifier.height(900.dp),
        title = "해당 카테고리를 수정하시겠습니까?",
        body = "새 카테고리명을 입력하고 대표 색상을 지정해주세요!",
        buttonText = "저장",
        visible = true,
        isReady = true,
        onDismiss = {}
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(1.dp, colors.maincolor, RoundedCornerShape(18.dp))
                .padding(horizontal = 21.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var text by remember { mutableStateOf("") }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = colors.gray[400],
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
                                text = "카테고리명은 최대 10자입니다", // placeholder
                                color = colors.gray[400],
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                        innerTextField() // 실제 입력란
                    }
                }
            )

        }
        Spacer(modifier = Modifier.height(19.dp))
        if(true){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "색상",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.gray[800],
                )
                Text(
                    modifier = Modifier
                        .padding(start = 1.dp),
                    text = "(색상은 한 번 지정하면 변경 불가합니다)",
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.gray[400],
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    modifier = Modifier
                        .size(25.dp),
                    color = colors.gray[300],
                    shape = CircleShape
                ) { }
                Icon(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    tint = colors.gray[600],
                    painter = painterResource(id = R.drawable.check_img),
                    contentDescription = "아래 화살표"
                )
            }
        }
    }
}
