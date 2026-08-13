package com.linku.file.ui.bottom.sheet


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.linkuColors

/**
 * 폴더명 입력과 검증을 공통으로 제공하는 파일 바텀시트입니다.
 *
 * @param modifier 바텀시트에 적용할 수정자입니다.
 * @param title 바텀시트 상단 제목입니다.
 * @param body 입력 안내 문구입니다.
 * @param placeholderText 입력값이 없을 때 표시할 문구입니다.
 * @param visible 바텀시트 표시 여부입니다.
 * @param sheetState 바텀시트의 펼침 상태입니다.
 * @param onTextDeliver 저장할 텍스트를 전달하는 콜백입니다.
 * @param maxTextLength 입력 및 저장을 허용할 최대 문자 수입니다. `null`이면 길이를 제한하지 않습니다.
 * @param onDismiss 바텀시트를 닫을 때 호출되는 콜백입니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TextFieldFileBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    body: String,
    placeholderText: String,
    visible: Boolean,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onTextDeliver: (String) -> Unit,
    maxTextLength: Int? = null,
    onDismiss: () -> Unit,
){
    var text by remember { mutableStateOf("") }
    val isTextLengthValid = maxTextLength == null || text.length <= maxTextLength

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
        isReady = text.isNotEmpty() && isTextLengthValid,
        onOkay = {
            if (isTextLengthValid) {
                onTextDeliver(text)
            }
        },
        onDismiss = onDismiss,
    ) {
        FileBottomSheetTextField(
            value = text,
            onValueChange = { newText ->
                if (maxTextLength == null || newText.length <= maxTextLength) {
                    text = newText
                }
            },
            placeholderText = placeholderText,
            enabled = true,
        )
    }
}

/**
 * 파일 바텀시트에서 사용하는 테두리형 단일 행 텍스트 필드입니다.
 *
 * 입력값과 placeholder가 같은 텍스트 메트릭과 내부 중앙 정렬을 사용하도록 하여
 * 입력 커서와 안내 문구의 세로 위치를 일치시킵니다.
 *
 * @param value 현재 표시할 텍스트입니다.
 * @param onValueChange 입력값 변경 콜백입니다.
 * @param placeholderText 입력값이 없을 때 표시할 문구입니다.
 * @param enabled 사용자 입력 가능 여부입니다.
 * @param modifier 텍스트 필드 행에 적용할 수정자입니다.
 */
@Composable
internal fun FileBottomSheetTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors
    val inputTextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = colors.black,
        fontWeight = FontWeight.Normal,
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, colors.maincolor, RoundedCornerShape(18.dp))
            .padding(horizontal = 21.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            enabled = enabled,
            value = value,
            onValueChange = onValueChange,
            textStyle = inputTextStyle,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = " $placeholderText",
                            style = inputTextStyle.copy(color = colors.gray[400]),
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, heightDp = 2000)
@Composable
private fun TextFieldFileBottomSheetTest(){
    TextFieldFileBottomSheet(
        modifier = Modifier.height(900.dp),
        title = "폴더명을 변경하시겠습니까?",
        body = "변경할 폴더명을 입력해주세요!",
        placeholderText = "폴더명은 최대 10자입니다.",
        visible = true,
        onTextDeliver = {},
        onDismiss = {},
    )
}
