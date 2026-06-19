package com.linku.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider

@Composable
fun LinkDetailOptionDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(LocalColorTheme.current.white)
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 38.dp)
            .heightIn(max = 264.dp)
    ) {
        options.forEach { option ->
            Text(
                text = option,
                fontSize = 15.sp,
                fontWeight = if (option == selectedOption) {
                    FontWeight.Medium
                } else {
                    FontWeight.Normal
                },
                color = if (option == selectedOption) {
                    LocalColorTheme.current.blue[200]
                } else {
                    LocalColorTheme.current.gray[800]
                },
                modifier = Modifier
                    .noRippleClickable {
                        onOptionClick(option)
                    }
                    .padding(horizontal = 4.dp, vertical = 9.dp)
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkDetailOptionDropdown() {
    ThemeProvider {
        LinkDetailOptionDropdown(
            options = listOf(
                "트렌드 확인",
                "통학 중",
                "과제 중",
                "쇼핑 중",
                "데이트 중",
                "알바 전"
            ),
            selectedOption = "통학 중",
            onOptionClick = { }
        )
    }
}