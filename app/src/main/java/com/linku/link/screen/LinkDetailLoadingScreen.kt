package com.linku.link.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.R
import com.linku.design.component.SkeletonBox
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors

/** 링크 상세 상태 화면 상단에 표시할 정적 제목 영역의 높이입니다. */
private val LINK_DETAIL_STATUS_HEADER_BOTTOM_PADDING = 23.dp

/**
 * 링크 상세 정보를 처음 불러오는 동안 실제 상세 레이아웃과 같은 위치에 스켈레톤을 표시합니다.
 *
 * 상단의 뒤로가기 액션은 로딩과 무관하게 즉시 사용할 수 있습니다. 제목, 분류 칩, 이미지, URL,
 * AI 결과, 메모 영역은 서버 데이터가 준비되기 전까지 placeholder로 표시됩니다.
 *
 * @param onBack 이전 화면으로 돌아갈 때 호출됩니다.
 */
@Composable
fun LinkDetailLoadingScreen(
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.white),
    ) {
        LinkDetailLoadingHeader(onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 25.dp, start = 20.dp, end = 20.dp, bottom = 50.dp),
        ) {
            // 실제 링크 이미지와 같은 정사각형 영역을 먼저 확보해 로딩 완료 시 레이아웃 이동을 줄입니다.
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = RoundedCornerShape(18.dp),
            )

            Spacer(modifier = Modifier.height(18.dp))

            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(43.dp),
                shape = RoundedCornerShape(18.dp),
            )

            Spacer(modifier = Modifier.height(25.dp))

            LinkDetailSkeletonSectionTitle(labelWidth = 48.dp)

            Spacer(modifier = Modifier.height(13.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                LinkDetailTagSkeleton(width = 70.dp)
                LinkDetailTagSkeleton(width = 82.dp)
                LinkDetailTagSkeleton(width = 64.dp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            LinkDetailSkeletonSectionTitle(labelWidth = 76.dp)

            Spacer(modifier = Modifier.height(13.dp))

            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp),
                shape = RoundedCornerShape(18.dp),
            )

            Spacer(modifier = Modifier.height(22.dp))

            SkeletonBox(
                modifier = Modifier
                    .width(32.dp)
                    .height(14.dp),
                shape = RoundedCornerShape(4.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(18.dp),
            )
        }
    }
}

/**
 * 링크 상세 조회가 실패했을 때 뒤로가기와 재시도 액션을 제공합니다.
 *
 * @param onBack 이전 화면으로 돌아갈 때 호출됩니다.
 * @param onRetry 현재 링크 상세 조회를 다시 요청할 때 호출됩니다.
 */
@Composable
fun LinkDetailLoadErrorScreen(
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    BackHandler(onBack = onBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.white),
    ) {
        // 파란 상세 헤더 없이도 이전 화면으로 돌아갈 수 있도록 흰 배경용 버튼을 독립 배치합니다.
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 44.dp, start = 2.dp)
                .size(48.dp)
                .noRippleClickable(
                    role = Role.Button,
                    onClick = onBack,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = stringResource(R.string.link_detail_back),
                modifier = Modifier
                    .width(12.dp)
                    .height(18.dp),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.link_detail_load_error_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.gray[800],
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.link_detail_load_error_body),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = colors.gray[500],
            )

            Spacer(modifier = Modifier.height(45.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(colors.maincolor)
                    .noRippleClickable(
                        role = Role.Button,
                        onClick = onRetry,
                    )
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .padding(horizontal = 19.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_refresh),
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                    tint = Color.Unspecified,
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = stringResource(R.string.link_detail_load_retry),
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.01).sp,
                    color = colors.white,
                )
            }
        }
    }
}

/**
 * 링크 상세 로딩 중 실제 상단바 높이와 내부 정렬을 유지하는 placeholder 헤더입니다.
 *
 * @param onBack 이전 화면으로 돌아갈 때 호출됩니다.
 */
@Composable
private fun LinkDetailLoadingHeader(
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val headerSkeletonColor = colors.white.copy(alpha = 0.32f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
            .background(colors.blue[200]),
    ) {
        Image(
            painter = painterResource(R.drawable.linku_logo_transparent),
            contentDescription = null,
            modifier = Modifier
                .height(110.dp)
                .align(Alignment.TopEnd),
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            LinkDetailNavigationRow(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 29.dp,
                        start = 24.dp,
                        end = 24.dp,
                        bottom = LINK_DETAIL_STATUS_HEADER_BOTTOM_PADDING,
                    ),
            ) {
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(29.dp),
                    shape = RoundedCornerShape(7.dp),
                    color = headerSkeletonColor,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LinkDetailHeaderChipSkeleton(width = 62.dp, color = headerSkeletonColor)
                        LinkDetailHeaderChipSkeleton(width = 58.dp, color = headerSkeletonColor)
                        LinkDetailHeaderChipSkeleton(width = 66.dp, color = headerSkeletonColor)
                    }

                    SkeletonBox(
                        modifier = Modifier.size(22.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = headerSkeletonColor,
                    )
                }
            }
        }
    }
}

/**
 * 링크 상세 상태 화면에서 공통으로 사용하는 뒤로가기 및 화면 제목 행입니다.
 *
 * @param onBack 이전 화면으로 돌아갈 때 호출됩니다.
 * @param modifier 제목 행에 추가로 적용할 modifier입니다.
 */
@Composable
private fun LinkDetailNavigationRow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 59.dp, start = 20.dp, end = 24.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_back_white),
            contentDescription = stringResource(R.string.link_detail_back),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(11.dp)
                .noRippleClickable(onClick = onBack),
        )

        Text(
            text = stringResource(R.string.link_detail_screen_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colors.white,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

/**
 * AI 영역 제목의 아이콘과 텍스트 자리를 함께 표시합니다.
 *
 * @param labelWidth 제목 텍스트 placeholder 너비입니다.
 */
@Composable
private fun LinkDetailSkeletonSectionTitle(
    labelWidth: Dp,
) {
    Row(
        modifier = Modifier.padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SkeletonBox(
            modifier = Modifier.size(15.dp),
            shape = RoundedCornerShape(5.dp),
        )
        SkeletonBox(
            modifier = Modifier
                .width(labelWidth)
                .height(14.dp),
            shape = RoundedCornerShape(4.dp),
        )
    }
}

/**
 * AI 태그 한 개와 같은 크기의 둥근 placeholder를 표시합니다.
 *
 * @param width 태그 placeholder 너비입니다.
 */
@Composable
private fun LinkDetailTagSkeleton(
    width: Dp,
) {
    SkeletonBox(
        modifier = Modifier
            .width(width)
            .height(38.dp),
        shape = RoundedCornerShape(20.dp),
    )
}

/**
 * 링크 상세 헤더의 분류 칩 한 개와 같은 크기의 placeholder를 표시합니다.
 *
 * @param width 칩 placeholder 너비입니다.
 * @param color 파란 헤더 위에 표시할 placeholder 색상입니다.
 */
@Composable
private fun LinkDetailHeaderChipSkeleton(
    width: Dp,
    color: Color,
) {
    SkeletonBox(
        modifier = Modifier
            .width(width)
            .height(27.dp),
        shape = RoundedCornerShape(10.dp),
        color = color,
    )
}

/** 링크 상세 로딩 화면의 디자인 확인용 미리보기입니다. */
@Preview(showBackground = true)
@Composable
private fun PreviewLinkDetailLoadingScreen() {
    ThemeProvider {
        LinkDetailLoadingScreen(onBack = {})
    }
}

/** 링크 상세 조회 실패 화면의 디자인 확인용 미리보기입니다. */
@Preview(showBackground = true)
@Composable
private fun PreviewLinkDetailLoadErrorScreen() {
    ThemeProvider {
        LinkDetailLoadErrorScreen(
            onBack = {},
            onRetry = {},
        )
    }
}
