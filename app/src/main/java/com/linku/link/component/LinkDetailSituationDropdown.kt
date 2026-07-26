package com.linku.link.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.Situation
import com.linku.core.model.SituationId
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors

@Composable
fun LinkDetailSituationDropdown(
    situations: List<Situation>,
    selectedSituation: Situation?,
    onSituationClick: (Situation) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(colors.white)
            .heightIn(max = 264.dp)
            .verticalScroll(rememberScrollState())
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 38.dp)
    ) {
        situations.forEach { situation ->
            val isSelected = situation.id == selectedSituation?.id

            Text(
                text = situation.tagName,
                fontSize = 15.sp,
                fontWeight = if (isSelected) {
                    FontWeight.Medium
                } else {
                    FontWeight.Normal
                },
                color = if (isSelected) {
                    colors.blue[200]
                } else {
                    colors.gray[800]
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
            Situation(SituationId.OFFICE_TREND_CHECK, "트렌드 확인"),
            Situation(SituationId.UNIVERSITY_COMMUTE, "통학 중"),
            Situation(SituationId.UNIVERSITY_ASSIGNMENT, "과제 중"),
            Situation(SituationId.UNIVERSITY_SHOPPING, "쇼핑 중"),
            Situation(SituationId.UNIVERSITY_DATE, "데이트 중"),
            Situation(SituationId.UNIVERSITY_PART_TIME_JOB, "알바 전")
        )

        LinkDetailSituationDropdown(
            situations = situations,
            selectedSituation = situations.firstOrNull { it.tagName == "통학 중" },
            onSituationClick = { }
        )
    }
}