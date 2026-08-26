package com.linku.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors

/**
 * 링크 카드에서 선택한 링크를 삭제할 수 있는 메뉴를 표시합니다.
 *
 * @param onDeleteClick 삭제 메뉴를 눌렀을 때 실행할 콜백입니다.
 * @param modifier 메뉴의 크기와 배치를 조정하는 수정자입니다.
 */
@Composable
fun DeleteLinkItemModal(
    onDeleteClick: () -> Unit = { },
    modifier: Modifier
) {
    val colors = MaterialTheme.linkuColors

    val shape = remember { RoundedCornerShape(14.dp) }

    Column(
        modifier = modifier
            .width(120.dp)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 15.dp,
                    spread = 0.dp,
                    offset = DpOffset(x = 0.dp, y = 3.dp),
                    color = colors.deleteLinkItemModalShadowColor,
                    alpha = 0.3f
                )
            )
            .clip(shape)
            .background(colors.white)
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .noRippleClickable { onDeleteClick() }
    ) {
        Text(
            text = stringResource(R.string.link_card_delete_action),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = colors.gray[800],
            modifier = Modifier.width(90.dp)
        )
    }
}

/** 피그마 명세에 맞춘 링크 삭제 메뉴를 미리 확인합니다. */
@Preview(showBackground = false)
@Composable
fun PreviewDeleteLinkItemModal() {
    ThemeProvider {
        DeleteLinkItemModal(
            onDeleteClick = { },
            modifier = Modifier
        )
    }
}
