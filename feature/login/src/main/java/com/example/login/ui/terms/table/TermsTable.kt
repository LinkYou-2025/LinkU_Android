package com.example.login.ui.terms.table

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.font.Paperlogy

@Composable
fun TermsTable(
    headers: Pair<String, String>,
    rows: List<Pair<String, String>>
) {
    val colorTheme = LocalColorTheme.current
    val borderColor = colorTheme.gray[400]
    val borderWidth = 0.5.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = borderWidth, color = borderColor)
    ) {
        // 1. 헤더 행
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableCell(text = headers.first, weight = 0.3f, isHeader = true)
            VerticalDivider(color = borderColor, thickness = borderWidth)
            TableCell(text = headers.second, weight = 0.7f, isHeader = true)
        }

        // 2. 데이터 행들
        rows.forEach { rowData ->
            HorizontalDivider(color = borderColor, thickness = borderWidth)
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell(text = rowData.first, weight = 0.3f)
                VerticalDivider(color = borderColor, thickness = borderWidth)
                TableCell(text = rowData.second, weight = 0.7f)
            }
        }
    }
}


@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    isHeader: Boolean = false
) {
    val colorTheme = LocalColorTheme.current
    val verticalPadding = if (isHeader) 10.dp else 22.dp

    Box(
        modifier = Modifier
            .weight(weight)
            .fillMaxHeight()
            .padding(vertical = verticalPadding, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart // 모든 텍스트 세로 중앙, 가로 왼쪽
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontFamily = Paperlogy.font,
            fontWeight = if (isHeader) FontWeight.Medium else FontWeight.Normal,
            color = colorTheme.black,
            textAlign = TextAlign.Start,
            lineHeight = 18.sp
        )
    }
}

// 요소 4개짜리
@Composable
fun TermsTable4Col(
    headers: List<String>,
    rows: List<List<String>>,
    weights: List<Float> = listOf(0.25f, 0.25f, 0.25f, 0.25f)  // 컬럼별 비율 조정 가능
) {
    val colorTheme = LocalColorTheme.current
    val borderColor = colorTheme.gray[400]
    val borderWidth = 0.5.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = borderWidth, color = borderColor)
    ) {
        // 헤더 행
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            headers.forEachIndexed { index, header ->
                TableCell(text = header, weight = weights.getOrElse(index) { 1f / headers.size }, isHeader = true)
                if (index < headers.lastIndex) {
                    VerticalDivider(color = borderColor, thickness = borderWidth)
                }
            }
        }

        // 데이터 행
        rows.forEach { rowData ->
            HorizontalDivider(color = borderColor, thickness = borderWidth)
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowData.forEachIndexed { index, cell ->
                    TableCell(text = cell, weight = weights[index])
                    if (index < rowData.lastIndex) {
                        VerticalDivider(color = borderColor, thickness = borderWidth)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TermsTablePreview() {
    // Preview 어노테이션 대신 Modifier로 여백을 줍니다.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp), // 여기에 패딩을 넣으면 프리뷰에서 여백이 보입니다.
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {

        // 보유 기간 표 - 개인정보 처리방침 표
        TermsTable(
            headers = "항목" to "보유 기간",
            rows = listOf(
                "회원 정보" to "회원 탈퇴 시까지",
                "고객 문의 기록" to "처리 완료 후 1년",
                "접속 로그" to "3개월 (통신비밀보호법)"
            )
        )
        // 개인정보 처리방침 표2
        TermsTable(
            headers = "수탁업체" to "위탁 업무",
            rows = listOf(
                "Kakao Corp." to "소셜 로그인 인증",
                "Google LLC" to "소셜 로그인 인증",
                "NAVER Corp." to "소셜 로그인 인증",
                "Amazon Web Services(AWS)" to "서버 운영 및 데이터 저장"
            )
        )
        // 개인 정보의 국외 이전
        TermsTable4Col(
            headers = listOf("이전받는 자", "이전 국가", "이전 항목", "이용 목적"),
            rows = listOf(
                listOf(
                    "Amazon Web Services, Inc.",
                    "미국",
                    "서비스 이용 과정에서 생성되는 데이터",
                    "클라우드 서버 운영 및 데이터 저장"
                )
            )
        )
    }
}