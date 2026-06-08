package com.linku.home.ui.alarm.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linku.design.modifier.skeleton
import com.linku.design.theme.LinkuPreview

/**
 * 알람 목록의 로딩 상태를 표시하는 컴포저블입니다.
 *
 * 데이터를 불러오는 동안 사용자에게 시각적 피드백을 제공하기 위해
 * [LazyColumn]을 사용하여 스켈레톤(Skeleton) UI 형태의 플레이스홀더를 렌더링합니다.
 */
@Composable
fun AlarmLoadingContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 12.dp)
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .skeleton(isLoading = true)
            )
        }
    }
}

@Preview
@Composable
private fun AlarmLoadingContentPreview() {
    LinkuPreview {
        AlarmLoadingContent()
    }
}