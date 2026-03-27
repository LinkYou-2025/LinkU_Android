package com.linku.file.ui.top.bar.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.file.R
import com.linku.file.ui.theme.DefaultFont
import com.linku.file.ui.theme.White

@Composable
fun FileSearchBar() {

    // 검색창 전체 바탕(틀)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),

        // 배경색 (투명)
        color = Color.Transparent,

        // 모서리 둥글게 (18dp)
        shape = RoundedCornerShape(18.dp),

        // 외곽선(테두리) 두께 1dp, 색상(흰색)
        border = BorderStroke(1.dp, White)
    ) {

        // 배경 Surface (반투명 흰색)
        Surface(
            // 전체 영역을 가득 채우도록
            modifier = Modifier.fillMaxSize(),

            // 배경색 (흰색, alpha=0.15 → 매우 연한 반투명)
            color = White.copy(alpha = 0.15f),

            // 모서리 둥글게 (18dp)
            shape = RoundedCornerShape(18.dp)
        ) {}

        // 내부 요소 가로 정렬
        Row(
            // 전체 영역을 가득 채우도록
            modifier = Modifier.fillMaxSize(),

            // 가로 정렬: 요소 간 13dp 간격, 왼쪽부터 배치
            horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.Start),

            // 세로 정렬: 세로 중앙 정렬
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // 왼쪽 링크 로고 아이콘
            Icon(
                // 왼쪽 여백 (18.51dp)
                modifier = Modifier.padding(start = 18.51.dp),

                // 아이콘 색상 (흰색)
                tint = White,

                // 사용할 아이콘 이미지 리소스 (drawable/linku_logo.xml)
                painter = painterResource(id = R.drawable.linku_logo),

                // 이미지 설명 ("링큐 로고")
                contentDescription = "링큐 로고"
            )

            // 검색 안내 텍스트
            Text(
                // 텍스트 내용 ("빠른 링크 검색")
                text = "빠른 링크 검색",

                // 폰트 크기 (16sp)
                fontSize = 16.sp,

                // 한 줄 높이 (20sp)
                lineHeight = 20.sp,

                // 지정 폰트 (paperlogy 등)
                fontFamily = DefaultFont,

                // 폰트 굵기 (Medium)
                fontWeight = FontWeight(500),

                // 글자색 (흰색)
                color = White,
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun FileSearchBarTest() {
    FileSearchBar()
}