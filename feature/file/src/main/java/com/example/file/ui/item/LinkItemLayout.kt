// 폴더 단위의 레이아웃

package com.example.file.ui.item

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import com.example.core.model.LinkItemInfo
import com.example.design.modifier.noRippleClickable
import com.example.file.FileViewModel
import com.example.file.R
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray100
import com.example.file.ui.theme.Gray200
import com.example.file.ui.theme.Gray400
import com.example.file.ui.theme.Gray600
import com.example.file.ui.theme.Gray800
import com.example.file.ui.theme.White
import com.example.file.ui.theme.domainLogoPainterOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkItemLayout(
    link: LinkItemInfo? = null,
    onClick: (LinkItemInfo?) -> Unit = {},
) {
    val tags = link?.tags?:emptyList()
    var showDialog by remember { mutableStateOf(false) }

    val domainIcon = link?.let{ domainLogoPainterOrNull(it.url) }

    val isNotAdder = link != null

    val painter = ImageRequest.Builder(LocalContext.current)
        .data(link?.linkuImageUrl) // url이 null일 수도 있음
        .crossfade(true)
        .placeholder(R.drawable.link_categorization_default)
        .error(R.drawable.link_categorization_default)
        .fallback(R.drawable.link_categorization_default) // null이면 이거 표시
        .build()

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
                    .padding(horizontal = 6.dp, vertical = 1.dp),
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

    val modifier = if(link != null) Modifier.noRippleClickable {
        link?.linkuId?.let {
            Log.d("LinkItemLayout", "아이템 클릭: \"savelinkresult/${it}\"")
            onClick(link)
        }
    } else Modifier

    // 전체 카드 바탕 Surface
    Surface(
        // 카드 크기: 가로 181dp, 세로 267dp
        modifier = Modifier
            .width(181.dp)
            //.height(267.dp)
            then(modifier)
            /*.pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        showDialog = true // 꾹 누르면 Dialog 띄우기
                    }
                )
            }*/,
        // 모서리 둥글게(18dp)
        shape = RoundedCornerShape(18.dp),
        // 카드 배경색(White)
        color = White,
        // 그림자(입체감) 효과(20dp)
        shadowElevation = 5.dp//(if(painter == null) 0 else 5).dp,
    ) {
        // 내용 전체를 세로로 배치하는 Column
        Column (
            // 전체 영역 채우고, 내부 패딩 11dp
            modifier = Modifier
                .fillMaxSize()
                .padding(11.dp)
                .alpha(if (isNotAdder) 1f else 0.35f)
        ) {

            // (1) 링크의 메인 이미지
            Box(
                // 둥근 모서리(18dp)로 클립, 사이즈 157dp, 가로 중앙 정렬
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .size(157.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(color = if (isNotAdder) Gray100 else White),
                contentAlignment = Alignment.Center
            ){
                if(isNotAdder){
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        // 사용할 이미지 리소스
                        model = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }else{
                    Icon(
                        modifier = Modifier.width(90.dp),
                        painter = painterResource(com.example.design.R.drawable.logo_whiteback),
                        tint = Gray400,
                        contentDescription = null
                    )
                }
            }

            // (2) 링크 제목
            Text(
                // 위쪽 여백(10dp)
                modifier = Modifier
                    .padding(top = 10.dp),
                text = link?.title?:"제목",
                // 폰트 크기(15sp)
                fontSize = 15.sp,
                // 폰트
                fontFamily = DefaultFont,
                // 폰트 굵기(Medium, 500)
                fontWeight = FontWeight(500),
                // 글자색(Black)
                color = Black,
                maxLines = 1, // 최대 2줄
                overflow = TextOverflow.Ellipsis // 잘리면 ... 표시
            )

            // (3) 링크 분류 태그(여러 개를 Row에 배치)
            LazyRow(
                // 가로 전체 채우기, 위쪽 여백(8dp)
                modifier = Modifier
                    .padding(top = 8.dp),
                // 태그 간 5dp 간격, 왼쪽 정렬
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                // 세로 중앙 정렬
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 전달받은 tags 리스트를 순회하며 LinkItemTag 생성
                items(tags){
                    LinkItemTag(it)
                }
            }

            // (4) 남은 공간 모두 차지하는 Spacer (아래로 밀어내기)
            Spacer(modifier = Modifier.height(8.dp))

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

                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Gray200),
                    contentAlignment = Alignment.Center
                ){
                    // 도메인 아이콘
                    domainIcon?.let {
                        Image(
                            modifier = Modifier
                                .fillMaxSize(),
                            // 아이콘 이미지 리소스
                            painter = it,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // 링크의 도메인 텍스트
                Text(
                    modifier = Modifier,

                    // 텍스트 내용
                    text = link?.url?:"도메인",

                    // 폰트 크기 (12sp)
                    fontSize = 12.sp,

                    // 폰트 (paperlogy 폰트)
                    fontFamily = DefaultFont,

                    // 폰트 굵기 (Bold)
                    fontWeight = FontWeight.Bold,

                    // 글자색 (Gray800)
                    color = Gray800,
                    maxLines = 1, // 최대 1줄
                    overflow = TextOverflow.Ellipsis // 잘리면 ... 표시
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LinkItemTest() {
    Box(
        contentAlignment = Alignment.TopCenter
    ){
        Box(
            modifier = Modifier.alpha(0.35f),
        ){
            LinkItemLayout(
                link = null
            )
        }

        Image(
            modifier = Modifier.padding(top = 103.dp),
            painter = painterResource(R.drawable.add_folder_icon),
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(top = 147.dp),
            text = "링크 추가하기",
            fontSize = 15.sp,
            fontFamily = DefaultFont,
            fontWeight = FontWeight(500),
            color = Black,
            textAlign = TextAlign.Center,
        )
    }
}