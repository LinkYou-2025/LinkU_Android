package com.example.login.ui.item


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.design.Negative
import com.example.login.R
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WrongIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(18.dp)
            .background(
                color = Color.Negative,
                shape = RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_login_wrong),
            contentDescription = "wrong",
            modifier = Modifier
                .width(9.dp)
                .height(9.dp)
        )
    }
}

@Preview(
    name = "WrongIndicator",
    showBackground = true
)
@Composable
private fun WrongIndicatorPreview() {
    WrongIndicator()
}