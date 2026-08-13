package com.linku.file.ui.top.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont
import com.linku.file.R

/**
 * 공유폴더 상세 화면의 그라데이션 상단 영역과 케밥 메뉴를 렌더링합니다.
 *
 * 메뉴 표시 상태와 모든 동작은 호출자가 소유합니다. 메뉴 항목을 선택하면 먼저
 * [onMenuExpandedChange]로 메뉴를 닫은 뒤 공유 또는 나가기 이벤트를 전달하여 다른 overlay와
 * 동시에 남지 않도록 합니다.
 *
 * @param folderName 현재 상세 폴더 이름
 * @param title 상세 화면의 중앙 제목
 * @param scopeLabel 현재 공유 범위를 설명하는 칩 문구
 * @param menuExpanded 케밥 메뉴가 열려 있는지 여부
 * @param backContentDescription 뒤로가기 접근성 문구
 * @param moreContentDescription 케밥 메뉴 접근성 문구
 * @param shareLabel 공유 메뉴 문구와 접근성 레이블
 * @param leaveLabel 나가기 메뉴 문구와 접근성 레이블
 */
@Composable
internal fun SharedFolderDetailTopBar(
    folderName: String,
    title: String,
    scopeLabel: String,
    menuExpanded: Boolean,
    backContentDescription: String,
    moreContentDescription: String,
    shareLabel: String,
    leaveLabel: String,
    onMenuExpandedChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors
    val font = MaterialTheme.linkuFont.font

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(206.dp)
            .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
            .background(colors.maincolor),
    ) {
        Icon(
            painter = painterResource(R.drawable.linku_logo),
            contentDescription = null,
            tint = colors.white,
            modifier = Modifier
                .alpha(0.2f)
                .align(Alignment.TopEnd)
                .padding(top = 80.dp)
                .offset(x = 22.5.dp)
                .size(width = 149.496.dp, height = 106.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 54.dp, start = 20.dp, end = 20.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterStart)
                    .noRippleClickable(
                        onClickLabel = backContentDescription,
                        role = Role.Button,
                        onClick = onBack,
                    ),
                contentAlignment = Alignment.CenterStart,
            ) {
                Image(
                    painter = painterResource(com.linku.design.R.drawable.ic_back_white),
                    contentDescription = null,
                    modifier = Modifier.size(width = 12.dp, height = 18.dp),
                )
            }

            Text(
                text = title,
                color = colors.white,
                fontFamily = font,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 52.dp),
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterEnd)
                    .noRippleClickable(
                        onClickLabel = moreContentDescription,
                        role = Role.Button,
                    ) {
                        onMenuExpandedChange(!menuExpanded)
                    },
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    painter = painterResource(com.linku.design.R.drawable.ic_more),
                    contentDescription = null,
                    tint = colors.white,
                    modifier = Modifier.size(width = 3.dp, height = 17.dp),
                )

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { onMenuExpandedChange(false) },
                    offset = DpOffset(x = 0.dp, y = 10.dp),
                    shape = RoundedCornerShape(22.dp),
                    containerColor = colors.white,
                    modifier = Modifier
                        .width(196.dp)
                        .padding(vertical = 20.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                        SharedFolderDetailMenuRow(
                            label = shareLabel,
                            iconRes = R.drawable.ic_file_floating_menu_share,
                            iconModifier = Modifier
                                .size(19.dp)
                                .rotate(-90f),
                            onClick = {
                                onMenuExpandedChange(false)
                                onShare()
                            },
                        )
                        SharedFolderDetailMenuRow(
                            label = leaveLabel,
                            iconRes = R.drawable.ic_file_floating_menu_leave,
                            iconModifier = Modifier.size(19.dp),
                            onClick = {
                                onMenuExpandedChange(false)
                                onLeave()
                            },
                        )
                    }
                }
            }
        }

        Text(
            text = folderName,
            color = colors.white,
            fontFamily = font,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 105.dp, end = 20.dp),
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 24.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.white)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = scopeLabel,
                color = colors.gray[800],
                fontFamily = font,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SharedFolderDetailMenuRow(
    label: String,
    iconRes: Int,
    iconModifier: Modifier,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp)
            .noRippleClickable(
                onClickLabel = label,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(width = 19.dp, height = 21.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = colors.gray[800],
                modifier = iconModifier,
            )
        }
        Text(
            text = label,
            color = colors.gray[800],
            fontFamily = MaterialTheme.linkuFont.font,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun SharedFolderDetailTopBarPreview() {
    LinkuPreview {
        SharedFolderDetailTopBar(
            folderName = "함께 보는 프로젝트 자료",
            title = "공유 폴더",
            scopeLabel = "지민님의 폴더",
            menuExpanded = true,
            backContentDescription = "뒤로가기",
            moreContentDescription = "공유폴더 메뉴",
            shareLabel = "폴더 공유하기",
            leaveLabel = "폴더 나가기",
            onMenuExpandedChange = {},
            onBack = {},
            onShare = {},
            onLeave = {},
        )
    }
}
