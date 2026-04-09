package com.linku.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider
import com.linku.mypage.R

@Composable
fun CustomInfoSelectionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) LocalColorTheme.current.blue[100] else LocalColorTheme.current.gray[200],
                shape = RoundedCornerShape(18.dp)
            )
            .background(
                if (isSelected) Color(0xFFF5F8FF)
                else LocalColorTheme.current.white
            )
            .noRippleClickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(LocalColorTheme.current.gray[600])
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = LocalColorTheme.current.black,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(
                if (isSelected) R.drawable.ic_checkbox_checked
                else R.drawable.ic_checkbox_empty_white
            ),
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewCustomInfoSelectionItem() {
    ThemeProvider {
        CustomInfoSelectionItem(
            text = "비즈니스/마케팅",
            isSelected = true,
            onClick = {}
        )
    }
}