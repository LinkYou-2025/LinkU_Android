package com.linku.login.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.linku.login.R

@Composable
fun BackIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.ic_back),
        contentDescription = "Back",
        modifier = modifier
            .width(10.dp)
            .height(18.dp)
            .clickable { onClick() },
        contentScale = ContentScale.Fit
    )
}