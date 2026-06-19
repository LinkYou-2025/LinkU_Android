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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.login.R

@Composable
internal fun SelectionCardItem(
    text: String,
    isSelected: Boolean,
    iconRes: Int,
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
            .width(140.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(colorTheme.white)
            .toggleable(
                value = isSelected,
                role = Role.Checkbox,
                onValueChange = { onClick() }
            )
            .padding(start = 20.dp, top = 25.dp, end = 20.dp, bottom = 25.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .width(26.dp)
                    .height(26.dp)
            )
            Text(
                text = text,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorTheme.black
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(brush = colorTheme.maincolor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_login_check),
                    contentDescription = null, //이미지는 장식용
                    modifier = Modifier
                        .width(12.dp)  // 지난 번, 피그마와 크기 차이로 인해 CheckIndicator를 따랐음(실제 10,7)
                        .height(9.dp)
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
                text = "사이드 프로젝트\n& 창업",
                isSelected = false,
                iconRes = R.drawable.ic_purpose_side_project,
                onClick = {}
            )
            SelectionCardItem(
                text = "인사이트 모으기",
                isSelected = true,
                iconRes = R.drawable.ic_purpose_insights,
                onClick = {}
            )
        }
    }
}