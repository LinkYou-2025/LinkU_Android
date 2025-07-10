package com.example.file.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.file.R

@Composable
fun EditButton() {
    Row(
        modifier = Modifier
            .border(
                width = 0.5.dp,
                color = Color(0x1A000000),
                shape = RoundedCornerShape(size = 2.dp)
            )
            .width(46.dp)
            .height(28.dp)
        .background(color = Color(0x0D000000), shape = RoundedCornerShape(size = 2.dp)),
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .width(38.dp)
                .height(24.dp),
            text = "EDIT",
            style = TextStyle(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                //fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditButtonTest() {
    EditButton()
}