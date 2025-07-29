package com.example.file.ui.bottom.sheet


import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.file.R
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray300
import com.example.file.ui.theme.Gray400
import com.example.file.ui.theme.Gray600
import com.example.file.ui.theme.Gray800
import com.example.file.ui.theme.MainColor

@Composable
fun TextFieldFileBottomSheet(
    title: String,
    body: String,
    placeholderText: String,
    isEditable: Boolean,
    visible: Boolean,
    onDismiss: () -> Unit,
){
    FileBottomSheet(
        title,
        body,
        "저장",
        visible,
        onDismiss
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(1.dp, MainColor, RoundedCornerShape(18.dp))
                .padding(horizontal = 21.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var text by remember { mutableStateOf("") }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Gray400,
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
                                text = placeholderText, // placeholder
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
        Spacer(modifier = Modifier.height(19.dp))
        if (isEditable) {
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
                Surface(
                    modifier = Modifier
                        .size(25.dp),
                    color = Gray300,
                    shape = CircleShape
                ) { }
                Icon(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    tint = Gray600,
                    painter = painterResource(id = R.drawable.check_img),
                    contentDescription = "아래 화살표"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextFieldFileBottomSheetTest(){
    TextFieldFileBottomSheet(
        "해당 카테고리를 수정하시겠습니까?",
        "새 카테고리명을 입력하고 대표 색상을 지정해주세요!",
        "카테고리명은 최대 10자입니다",
        true,
        true
    ){}
}
