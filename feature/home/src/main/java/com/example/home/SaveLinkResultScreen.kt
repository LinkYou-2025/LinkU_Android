package com.example.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.design.R as Res

@Composable
fun SaveLinkResultScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 59.dp, start = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 10.dp, height = 16.25.dp)
            )

            Spacer(modifier = Modifier.width(131.dp))

            Text(
                text = "새로운 링크",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                color = LocalColorTheme.current.black
            )
        }

        Text(
            text = "URL 링크 입력",
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
            color = LocalColorTheme.current.black,
            modifier = Modifier.padding(top = 31.dp, start = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .height(50.dp)
                .border(
                    width = 1.dp,
                    brush = Basic.maincolor,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val text = remember { mutableStateOf("") }

            if (text.value.isEmpty()) {
                Text(
                    text = "링크를 입력하거나 붙여넣어 주세요.",
                    color = LocalColorTheme.current.gray[400],
                    fontSize = 14.sp
                )
            }

            BasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = LocalColorTheme.current.black
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSaveLinkResultScreen() {
    SaveLinkResultScreen()
}