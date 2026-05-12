package com.linku.mypage.component.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(46.dp)
            .height(26.dp)
            .graphicsLayer {
                shadowElevation = 2.dp.toPx()
                shape = RoundedCornerShape(12.dp)
                clip = false
            }
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) LocalColorTheme.current.blue[200] else LocalColorTheme.current.gray[200])
            .noRippleClickable { onCheckedChange(!checked) }
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(21.9.dp)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .graphicsLayer {
                    shadowElevation = 2.dp.toPx()
                    shape = CircleShape
                    clip = false
                }
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomSwitchCheckedPreview() {
    LinkuPreview {
        CustomSwitch(
            checked = true,
            onCheckedChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomSwitchUncheckedPreview() {
    LinkuPreview {
        CustomSwitch(
            checked = false,
            onCheckedChange = {}
        )
    }
}
