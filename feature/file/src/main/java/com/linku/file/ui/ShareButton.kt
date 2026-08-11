package com.linku.file.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.linkuColors
import com.linku.file.R

@Composable
internal fun ShareButton(
    modifier: Modifier = Modifier.Companion
) {
    val colors = MaterialTheme.linkuColors
    // 내부 배치 레이아웃
    Row(
        modifier = Modifier
            // 전체 영역을 가득 채우도록
            .size(width = 146.dp, height = 50.dp)

            // 버튼 배경에 그라데이션 브러시 적용 (MainColor)
            .background(
                shape = RoundedCornerShape(size = 22.dp),
                brush = colors.maincolor
            )

            .then(modifier),

        // 가로 정렬 방법 (요소 간 8dp 간격, 가로 중앙 정렬)
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),

        // 세로 정렬 방법 (세로 중앙 정렬)
        verticalAlignment = Alignment.CenterVertically,
    ) {

        // "폴더 공유하기" 텍스트
        Text(
            // 텍스트 내용
            text = "폴더 공유하기",

            // 폰트 크기 (16sp)
            fontSize = 16.sp,

            // 한 줄 높이 (20sp)
            lineHeight = 20.sp,

            // 굵기 (Bold)
            fontWeight = FontWeight.Bold,

            // 글자색 (흰색)
            color = colors.white,

            // 텍스트 정렬 (가운데)
            textAlign = TextAlign.Center,
        )

        // 공유 아이콘
        Icon(
            modifier = Modifier
                // 가로 17.29426dp, 세로 14dp로 이미지 크기 조정
                .size(width = 17.29426.dp, height = 14.dp),

            // 아이콘 색상 (흰색)
            tint = colors.white,

            // 사용할 아이콘 이미지 리소스
            painter = painterResource(id = R.drawable.share_icon_img),

            // 이미지 설명
            contentDescription = "공유 버튼 아이콘"
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun ShareButtonTest() {
    ShareButton()
}