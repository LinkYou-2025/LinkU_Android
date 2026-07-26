package com.linku.link.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors

enum class LinkDetailAction(
    @param:DrawableRes val iconRes: Int,
    val label: String
) {
    EDIT(R.drawable.ic_link_edit, "링크 수정하기"),
    DELETE(R.drawable.ic_link_delete, "링크 삭제하기"),
    SHARE(R.drawable.ic_link_share, "링크 공유하기"),
    GO(R.drawable.ic_link_go_gray, "링크 보러가기")
}

@Composable
fun LinkDetailCustomDropdown(
    onAction: (LinkDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = modifier
            .width(240.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(colors.white)
            .padding(horizontal = 24.dp, vertical = 13.dp)
    ) {
        LinkDetailAction.entries.forEach { action ->
            LinkDetailDropdownItem(
                action = action,
                onClick = {
                    onAction(action)
                }
            )
        }
    }
}

@Composable
private fun LinkDetailDropdownItem(
    action: LinkDetailAction,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(action.iconRes),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = action.label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = colors.black
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkDetailCustomDropdown() {
    ThemeProvider {
        LinkDetailCustomDropdown(
            onAction = { },
            modifier = Modifier
        )
    }
}