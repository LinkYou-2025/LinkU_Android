package com.linku.design.top.bar



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.design.theme.font.Taebaek
import com.linku.design.util.scaler
import com.linku.design.R as Res

/*
    공통 TopBar 컴포넌트
 *
 * @param modifier Modifier
 * @param showSearchBar 검색바 표시 여부 (false면 로고+알림만)
 * @param logoBrush 로고 텍스트 색상/그라데이션 (null이면 테마 기본값)
 * @param searchBarBrush 검색바 배경 색상/그라데이션 (null이면 테마 기본값)
 * @param backgroundColor 전체 배경색 (기본값: 흰색, null이면 배경 없음 = 투명)
 * @param onClickSearch 검색바 클릭 콜백
 * @param onClickAlarm 알림 아이콘 클릭 콜백
 */

private const val TOPBAR_SIMPLE_HEIGHT = 96f   // 로고 + 알림만(큐레이션에서 사용)
private const val TOPBAR_SEARCH_HEIGHT = 139f    // 검색바 포함 //기존 파일은 206f

private val DEFAULT_BACKGROUND = Color.White // 기본 배경 흰색

//아 이것도 리펙해야하나...?
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    showSearchBar: Boolean = true,
    logoBrush: Brush? = null,
    searchBarBrush: Brush? = null,
    searchBarBorderColor: Color? = null,
    backgroundColor: Color? = DEFAULT_BACKGROUND,  // 기본값: 흰색, null이면 투명
    onClickSearch: () -> Unit = {},
    onClickAlarm: () -> Unit = {}
) {
    //디자인 모듈 불러오기
    val colorTheme = LocalColorTheme.current

    // 기본값 설정 - 파일 제외 모두 기본값은 동일합니다.
    val actualLogoBrush = logoBrush ?: colorTheme.maincolor
    val actualSearchBarBrush = searchBarBrush ?: colorTheme.maincolor

    // 로고 + 알림만 있을 때, 탑 바 높이를 77.4.scaler 일반적일 때는 139
    val topBarHeight =
        if (showSearchBar) TOPBAR_SEARCH_HEIGHT.scaler
        else TOPBAR_SIMPLE_HEIGHT.scaler

    // null이면 배경 없음, 아니면 해당 색상 적용
    val backgroundModifier = if (backgroundColor != null) {
        Modifier.background(backgroundColor)
    } else {
        Modifier  // 배경 없음 (투명)
    }

    // 파일 탭과 동일한 규격이나 반응형으로 수정함.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(topBarHeight)
            .then(backgroundModifier)
    ) {

        //링큐 로고 텍스트
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 35.scaler, top = 52.scaler),
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontSize = 24.sp,
                        fontFamily = Taebaek.font,
                        fontWeight = FontWeight(400),
                        brush = actualLogoBrush
                    )
                ) {
                    append("링큐")
                }
            }
        )

        // 알림
        // 다인누나의 요구사항에 따라 제거함.
//        Icon(
//            painter = painterResource(id = Res.drawable.ic_alarm),
//            contentDescription = "알림",
//            tint = colorTheme.gray[300],
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(end = 29.8f.scaler, top = 50.38f.scaler)
//                .size(width = 22.26f.scaler, height = 27.18f.scaler)
//                .noRippleClickable { onClickAlarm() }
//        )

        // 빠른 링크 검색바. (showSearchBar가 true일 때만 표시가 됩니다. 마이페이지 탑바 생성시 참고 부탁드립니다.)
        if (showSearchBar) {
            // 테두리 Modifier 조건부 적용
            val borderModifier = if (searchBarBorderColor != null) {
                Modifier.border(
                    width = 1.scaler,
                    color = searchBarBorderColor,
                    shape = RoundedCornerShape(18.scaler)
                )
            } else {
                Modifier //테두리 없음.
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 91.scaler, start = 16.scaler, end = 16.scaler)
                    .fillMaxWidth()
                    .height(48.scaler)
                    .clip(RoundedCornerShape(18.scaler))
                    .background(brush = actualSearchBarBrush)
                    .then(borderModifier) //테두리 적용 추가.
                    .noRippleClickable { onClickSearch() },
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(13.scaler)
                ) {
                    Icon(
                        painter = painterResource(id = Res.drawable.ic_logo_white),
                        contentDescription = null,
                        tint = colorTheme.white,
                        modifier = Modifier
                            .padding(start = 18.5f.scaler, top = 15.scaler, bottom = 16.scaler)
                            .width(23.97571f.scaler)
                            .height(17f.scaler)
                    )

                    Text(
                        text = "빠른 링크 검색",
                        color = colorTheme.white,
                        fontFamily = Paperlogy.font,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "기본 (검색바 포함)")
@Composable
fun PreviewCurationTopBar() {
    TopBar()
}

@Preview(showBackground = true, name = "로고 + 알림만")
@Composable
fun PreviewCurationTopBarSimple() {
    TopBar(showSearchBar = false)
}

@Preview(showBackground = true, name = "커스텀 컬러 (FileTopBar 스타일 프리뷰)")
@Composable
fun PreviewCurationTopBarCustom() {
    val colorTheme = LocalColorTheme.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(139.scaler)
            .background(brush = colorTheme.maincolor)
    ) {
        TopBar(
            backgroundColor = null,  // 투명

            logoBrush = Brush.linearGradient(
                listOf(Color.White, Color.White)
            ),

            searchBarBrush = Brush.linearGradient(
                listOf(Color(0x26FFFFFF), Color(0x26FFFFFF))
            ),

            searchBarBorderColor = Color.White
        )
    }
}