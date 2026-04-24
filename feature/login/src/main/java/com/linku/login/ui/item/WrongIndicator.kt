package com.linku.login.ui.item


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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.linku.login.R
import androidx.compose.ui.tooling.preview.Preview
import com.linku.design.theme.LocalColorTheme
import androidx.compose.material3.MaterialTheme
import com.linku.design.theme.linkuColors

@Composable
internal fun WrongIndicator(
    modifier: Modifier = Modifier
) {
    val colorTheme = MaterialTheme.linkuColors

    Box(
        modifier = modifier
            .size(18.dp)
            .background(
                color = colorTheme.negative,
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