package com.linku.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.Situation
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider

@Composable
fun LinkDetailSituationDropdown(
    situations: List<Situation>,
    selectedSituation: Situation?,
    onSituationClick: (Situation) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(LocalColorTheme.current.white)
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 38.dp)
            .heightIn(max = 264.dp)
    ) {
        situations.forEach { situation ->
            Text(
                text = situation.tagName,
                fontSize = 15.sp,
                fontWeight = if (situation.id == selectedSituation?.id) {
                    FontWeight.Medium
                } else {
                    FontWeight.Normal
                },
                color = if (situation.id == selectedSituation?.id) {
                    LocalColorTheme.current.blue[200]
                } else {
                    LocalColorTheme.current.gray[800]
                },
                modifier = Modifier
                    .noRippleClickable {
                        onSituationClick(situation)
                    }
                    .padding(horizontal = 4.dp, vertical = 9.dp)
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkDetailSituationDropdown() {
    ThemeProvider {
        val situations = listOf(
            Situation(18L, "트렌드 확인"),
            Situation(10L, "통학 중"),
            Situation(9L, "과제 중"),
            Situation(11L, "쇼핑 중"),
            Situation(14L, "데이트 중"),
            Situation(12L, "알바 전")
        )

        LinkDetailSituationDropdown(
            situations = situations,
            selectedSituation = situations.firstOrNull { it.tagName == "통학 중" },
            onSituationClick = { }
        )
    }
}