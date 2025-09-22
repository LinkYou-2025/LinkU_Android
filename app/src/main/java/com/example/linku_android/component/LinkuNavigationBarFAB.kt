package com.example.linku_android.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.linku_android.R

@Composable
fun LinkuNavigationBarFAB(
    onClicK: () -> Unit = {},
){

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    IconButton(
        // TODO: 링크 추가 버튼 넣기, 클릭 시 그라데이션으로 색 바뀌어야 하나?
        onClick = { onClicK() },
        interactionSource = interactionSource,
        modifier = Modifier
            .width(57.6.dp)
            .height(48.dp)
            .background(
                LocalColorTheme.current.gray[100],
                shape = RoundedCornerShape(14.dp)
            ),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_plus),
            contentDescription = "FAB 더하기 이미지",
            modifier = Modifier
                .fillMaxSize(20f/57f)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        if (isPressed) {
                            drawRect(
                                brush = Basic.maincolor,
                                blendMode = BlendMode.SrcIn
                            )
                        }
                    }
                },
            tint = LocalColorTheme.current.gray[400],
        )
    }

}