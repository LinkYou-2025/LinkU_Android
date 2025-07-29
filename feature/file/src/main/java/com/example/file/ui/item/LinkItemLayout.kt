// 폴더 단위의 레이아웃

package com.example.file.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.file.R
import com.example.file.ui.FileModalWindow
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray100
import com.example.file.ui.theme.Gray600
import com.example.file.ui.theme.Gray800
import com.example.file.ui.theme.White

@Composable
fun LinkItemLayout(tags: List<String>) {
    var showDialog by remember { mutableStateOf(false) }

    // 링크 분류 태그(Chip) 컴포저블
    @Composable
    fun LinkItemTag(tag: String){
        // 태그의 배경(Box)
        Box (
            // 태그 배경: 크기 wrap, 둥근 모서리(6dp), Gray100 배경색
            modifier = Modifier
                .wrapContentSize()
                .background(
                    shape = RoundedCornerShape(size = 6.dp),
                    color = Gray100
                ),
        ) {
            // 태그 텍스트
            Text(
                // 태그 텍스트를 Box 중앙에 정렬, 내부 여백(가로 6dp, 세로 3dp)
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                text = tag,
                // 폰트 크기(10sp)
                fontSize = 10.sp,
                // 폰트
                fontFamily = DefaultFont,
                // 폰트 굵기(Normal)
                fontWeight = FontWeight.Normal,
                // 글자색(Gray600)
                color = Gray600,
            )
        }
    }

    // 전체 카드 바탕 Surface
    Surface(
        // 카드 크기: 가로 181dp, 세로 267dp
        modifier = Modifier
            .width(181.dp)
            .height(267.dp)
            .pointerInput(Unit){
                detectTapGestures(
                    onLongPress = {
                        showDialog = true // 꾹 누르면 Dialog 띄우기
                    }
                )
            },
        // 모서리 둥글게(18dp)
        shape = RoundedCornerShape(18.dp),
        // 카드 배경색(White)
        color = White,
        // 그림자(입체감) 효과(20dp)
        shadowElevation = 20.dp,
    ) {
        // 내용 전체를 세로로 배치하는 Column
        Column (
            // 전체 영역 채우고, 내부 패딩 11dp
            modifier = Modifier
                .fillMaxSize()
                .padding(11.dp)
        ) {

            // (1) 링크의 메인 이미지
            Image(
                // 둥근 모서리(18dp)로 클립, 사이즈 157dp, 가로 중앙 정렬
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .size(157.dp)
                    .align(Alignment.CenterHorizontally),
                // 사용할 이미지 리소스
                painter = painterResource(id = R.drawable.test_img),  // 테스트 이미지
                contentDescription = null
            )

            // (2) 링크 제목
            Text(
                // 위쪽 여백(10dp)
                modifier = Modifier
                    .padding(top = 10.dp),
                text = "제목",
                // 폰트 크기(15sp)
                fontSize = 15.sp,
                // 폰트
                fontFamily = DefaultFont,
                // 폰트 굵기(Medium, 500)
                fontWeight = FontWeight(500),
                // 글자색(Black)
                color = Black
            )

            // (3) 링크 분류 태그(여러 개를 Row에 배치)
            Row(
                // 가로 전체 채우기, 위쪽 여백(8dp)
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                // 태그 간 5dp 간격, 왼쪽 정렬
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                // 세로 중앙 정렬
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 전달받은 tags 리스트를 순회하며 LinkItemTag 생성
                tags.forEach {
                    LinkItemTag(it)
                }
            }

            // (4) 남은 공간 모두 차지하는 Spacer (아래로 밀어내기)
            Spacer(modifier = Modifier.weight(1f))

            // (5) 링크 설명 프레임 (도메인, 아이콘 등)
            Row(
                // 가로 전체 채우기, wrapContentHeight로 높이 최소화
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                // 요소 간 7dp 간격, 왼쪽 정렬
                horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.Start),
                // 세로 중앙 정렬
                verticalAlignment = Alignment.CenterVertically,
            ) {

                // 도메인 아이콘 (ex. 트위터)
                Image(
                    // 원형으로 클립, 사이즈 26dp
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(26.dp),
                    // 아이콘 이미지 리소스
                    painter = painterResource(id = R.drawable.twiter_logo_img),  // 트위터 로고(테스트)
                    contentDescription = null
                )

                // 링크의 도메인 텍스트
                Text(
                    modifier = Modifier,

                    // 텍스트 내용
                    text = "도메인",

                    // 폰트 크기 (12sp)
                    fontSize = 12.sp,

                    // 폰트 (paperlogy 폰트)
                    fontFamily = DefaultFont,

                    // 폰트 굵기 (Bold)
                    fontWeight = FontWeight.Bold,

                    // 글자색 (Gray800)
                    color = Gray800
                )
            }
        }
    }

    FileModalWindow(
        showDialog,
        {},
        "삭제하기",
        "",
        "해당 폴더를 비공개 하시겠습니까?"
    ){
        Text(
            text = "해당 폴더는 타인과 공유중인 폴더입니다.\n비공개 폴더로 전환하시겠습니까?",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontFamily = DefaultFont,
            fontWeight = FontWeight(400),
            color = Gray600,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LinkItemTest() {
    LinkItemLayout(listOf("태그1", "태그2", "태그3"))
}