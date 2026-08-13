package com.linku.login.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.login.R

/**
 * 회원가입 목적·관심사 항목의 아이콘과 선택 상태를 카드 형태로 표시합니다.
 *
 * @param text 카드에 표시할 항목 이름
 * @param isSelected 현재 카드의 선택 여부
 * @param iconPainter 항목 종류에 대응하는 아이콘 Painter
 * @param onClick 카드를 눌렀을 때 실행할 콜백
 * @param modifier 외부에서 전달받는 레이아웃 Modifier
 */
@Composable
internal fun SelectionCardItem(
    text: String,
    isSelected: Boolean,
    iconPainter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colorTheme = MaterialTheme.linkuColors

    val cardModifier = if (isSelected) {
        modifier
            .shadow(
                elevation = 15.dp, shape = RoundedCornerShape(22.dp),
                spotColor = colorTheme.shadowColor,
                ambientColor = colorTheme.shadowColor
            )
            .border(width = 1.dp, brush = colorTheme.maincolor, shape = RoundedCornerShape(22.dp))
    } else {
        modifier.border(
            width = 1.dp,
            color = colorTheme.gray[200],
            shape = RoundedCornerShape(22.dp)
        )
    }

    Box(
        modifier = cardModifier
            .clip(RoundedCornerShape(22.dp))
            .background(colorTheme.white)
            .toggleable(
                value = isSelected,
                role = Role.Checkbox,
                onValueChange = { onClick() }
            )
            .padding(start = 18.dp, top = 25.dp, end = 18.dp, bottom = 25.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = iconPainter,
                contentDescription = null,
                modifier = Modifier
                    .width(25.dp)
                    .height(25.dp)
            )
            Text(
                text = text,
                fontSize = 12.5.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(brush = colorTheme.maincolor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_login_check),
                    contentDescription = null, //이미지는 장식용
                    modifier = Modifier
                        .width(10.dp)  // 지난 번, 피그마와 크기 차이로 인해 CheckIndicator를 따랐음(실제 10,7)
                        .height(8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun SelectionCardItemComparePreview() {
    LinkuPreview {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            SelectionCardItem(
                text = "프로젝트\n& 창업",
                isSelected = false,
                iconPainter = painterResource(com.linku.design.R.drawable.ic_purpose_side_project),
                onClick = {},
                modifier = Modifier.size(130.dp)
            )
            SelectionCardItem(
                text = "글쓰기\n&콘텐츠\n노하우",
                isSelected = true,
                iconPainter = painterResource(com.linku.design.R.drawable.ic_purpose_insights),
                onClick = {},
                modifier = Modifier.size(130.dp)
            )
        }
    }
}
