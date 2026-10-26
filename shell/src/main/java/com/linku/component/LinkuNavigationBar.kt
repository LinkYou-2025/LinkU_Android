package com.linku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.navigation.LinkuNavigationItem
import com.linku.design.modifier.gradientTint
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic

// val centerButtonSize = DpSize(19.2.dp, 19.2.dp)
val iconHeight = 24.dp

/*
* 링큐의 바텀 내비게이션 바
* */
@Composable
fun LinkuNavigationBar(
    /*
    * currentLinkuNavigationItem: 현재 선택된 아이템 및 보이는 화면의 이름
    * onNavigate: 아이템 클릭 시 호출되는 콜백
    * onFABClick: 링크 추가 버튼 클릭 시 호출되는 콜백
    * */
    currentLinkuNavigationItem: LinkuNavigationItem?,
    onNavigate: (LinkuNavigationItem) -> Unit,
    onFABClick: () -> Unit,
) {

    // 시스템 내비게이션 바(제스처 바 또는 3버튼 바) 높이만큼의 하단 여백.
    // 앱이 항상 엣지투엣지라 Scaffold의 bottomBar는 시스템 인셋을 자동으로 받지 못하므로, 이 값이 있어야
    // 하단바 뒤로 흰 배경이 이어지고 아이콘이 시스템 바에 가려지지 않음. (동작 여부 확인 완료)
    // 몰입 모드로 시스템 바가 숨겨지면 0이 됨.
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // 내비게이션 바의 전체 레이아웃
    Row(
        modifier = Modifier

            // 화면의 가로 전체를 채움
            .fillMaxWidth()

            // 흰색 배경색
            .background(Color.White)

            // Row의 양 옆에 여백을 줌
            .padding(horizontal = 8.dp)

            // Row의 밑에 bottomPadding만큼 여백을 줌
            .padding(bottom = bottomPadding)

            // 가장 큰 자식 높이에 Row 전체 높이를 맞춰 모든 칸이 동일 높이
            .height(IntrinsicSize.Min)
    ) {

        // 가운데 링크 추가 버튼을 기준으로 앞쪽 탭: 홈, 파일
        listOf(LinkuNavigationItem.HOME, LinkuNavigationItem.FILE).forEach { item ->
            NavItem(
                item = item,
                selected = currentLinkuNavigationItem == item,
                onClick = { onNavigate(item) }
            )
        }

        // 링크 추가 버튼은 탭이 아니라 동작(링크 저장 화면 열기)이므로 탭 목록과 따로 둔다.
        // 다른 탭과 같은 폭(가중치 1f)을 차지하며 버튼을 가운데 정렬함.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            LinkuNavigationBarFAB(onClick = onFABClick)
        }

        // 가운데 링크 추가 버튼을 기준으로 뒤쪽 탭: 큐레이션, 마이페이지
        listOf(LinkuNavigationItem.CURATION, LinkuNavigationItem.MY_PAGE).forEach { item ->
            NavItem(
                item = item,
                selected = currentLinkuNavigationItem == item,
                onClick = { onNavigate(item) }
            )
        }
    }
}

/*
* LinkuNavigationItem의 필드를 통해
* 내비게이션 바에 들어갈 아이템 컴포넌트를
* 생성하여 반환하는 컴포저블 함수
* */
@Composable
private fun RowScope.NavItem(
    /*
    * item: 전달된 LinkuNavigationItem
    * selected: 현재 선택된 아이템인지 여부
    * onClick: 아이템 클릭 시 해당 페이지로 이동하는 콜백
    * */
    item: LinkuNavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
) {

    // CURATION만 약간의 시각 보정
    val topPadding = if (item == LinkuNavigationItem.CURATION) 20.dp else 14.dp //다인언니랑 상의 끝에 이걸로 결정!
    //val topPadding = if (item == LinkuNavigationItem.CURATION) 27.56.dp else 21.dp
    val iconYOffset = if (item == LinkuNavigationItem.CURATION) (-2).dp else 0.dp

    // 내비게이션 바 내 아이템 레이아웃

    Column(
        modifier = Modifier

            // 동일한 간격을 위해 같은 가중치 1f 부여
            .weight(1f)

            // Row의 IntrinsicSize.Min과 맞물려 동일 높이 보장
            .fillMaxHeight()

            // 매개변수로 받은 콜백 함수 등록 (클릭 시 리플/박스 효과 없음)
            .noRippleClickable(onClick = onClick)

            // 위 아래 여백
            .padding(top = topPadding, bottom = 3.dp),

        // 가로 중앙 정렬
        horizontalAlignment = Alignment.CenterHorizontally,

        // 아이콘 이름이 위 아래 딱 붙게 배치
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // 아이콘 가로 길이
        val width = iconHeight * item.size.width / item.size.height * item.magnification

        // 아이콘 세로 길이
        val height = iconHeight * item.magnification

        // 아이콘
        Icon(
            // LinkuNavigationItem의 icon 필드로 아이콘을 설정
            painter = painterResource(id = item.icon),

            contentDescription = "내비게이션 바 아이템 아이콘",

            modifier = Modifier
                // 길이 지정
                .width(width)
                .height(height)

                // 큐레이션 로고 보정
                .offset(y = iconYOffset)

                // 선택된 아이템은 메인 그라데이션 색
                .run{
                    if(selected)
                        gradientTint(brush = Basic.maincolor)
                    else
                        this
                },

            // 선택되지 않은 아이템은 기본 색
            tint = Color.Unspecified,
        )

        // 이름
        Text(
            text = item.title,

            fontSize = 12.sp,   //10dp로 되어있더라고요..??????? 그래서 수정했습니다!

            lineHeight = 14.sp,
            // 폰트 굵기 = 400 (레귤러 사이즈)
            fontWeight = FontWeight.W400,

            // 사용할 폰트 (paperlogy)
            fontFamily = LocalFontTheme.current.font,

            // 선택된 아이템이면 그라데이션, 아니면 회색
            style = if (selected) TextStyle(brush = Basic.maincolor)
                    else TextStyle(color = Color(0xFFCACACA)),

            // 상단 패딩 11dp
            modifier = Modifier.padding(top = 6.dp) //다인언니가 올려달라고 함!
        )
    }
}

@Preview
@Composable
private fun PreviewNavigationBar() {
    ThemeProvider {
        LinkuNavigationBar(
            currentLinkuNavigationItem = LinkuNavigationItem.HOME,
            onNavigate = {},
            onFABClick = {}
        )
    }
}