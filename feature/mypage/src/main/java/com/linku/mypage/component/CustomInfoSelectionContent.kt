package com.linku.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.mypage.R

@Composable
fun CustomInfoSelectionContent(
    questionContent: @Composable () -> Unit,
    selectionContent: @Composable () -> Unit,
    buttonText: String,
    isButtonEnabled: Boolean,
    onBackClick: () -> Unit,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 59.dp, start = 20.dp, end = 20.dp)
                .height(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .width(11.dp)
                    .align(Alignment.CenterStart)
                    .noRippleClickable { onBackClick() }
            )

            Text(
                text = "맞춤정보 설정",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = LocalFontTheme.current.font,
                color = LocalColorTheme.current.black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(39.25.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            questionContent()
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            selectionContent()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(18.dp))
                .then(
                    if (isButtonEnabled) {
                        Modifier.background(
                            brush = LocalColorTheme.current.maincolor,
                            shape = RoundedCornerShape(18.dp)
                        )
                    } else {
                        Modifier.background(
                            color = LocalColorTheme.current.gray[300],
                            shape = RoundedCornerShape(18.dp)
                        )
                    }
                )
                .noRippleClickable(enabled = isButtonEnabled) {
                    onButtonClick()
                }
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = buttonText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = LocalColorTheme.current.white
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomInfoSelectionContent() {
    ThemeProvider {
        CustomInfoSelectionContent(
            questionContent = {
                Text(
                    text = "어떤 정보를 수정하시겠어요?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalColorTheme.current.black
                )
            },
            selectionContent = {
                Text(
                    text = "선택된 정보가 여기에 표시됩니다.",
                    fontSize = 14.sp,
                    color = LocalColorTheme.current.gray[700]
                )
            },
            buttonText = "저장",
            isButtonEnabled = true,
            onBackClick = {},
            onButtonClick = {}
        )
    }
}