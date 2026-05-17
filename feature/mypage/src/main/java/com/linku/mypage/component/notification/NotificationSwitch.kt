package com.linku.mypage.component.notification

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme

@Composable
fun NotificationSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onSwitchOn: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = LocalColorTheme.current.black,
            fontFamily = LocalFontTheme.current.font,
            modifier = Modifier.weight(1f)
        )

        CustomSwitch(
            checked = checked,
            onCheckedChange = {
                onCheckedChange(it)
                if (it) onSwitchOn?.invoke()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationSwitchCheckedPreview() {
    NotificationSwitch(
        title = "알림 수신 설정",
        checked = true,
        onCheckedChange = {}
    )
}


@Preview(showBackground = true)
@Composable
private fun NotificationSwitchUncheckedPreview() {
    LinkuPreview {
        NotificationSwitch(
            title = "알림 수신 설정",
            checked = false,
            onCheckedChange = {}
        )
    }
}
