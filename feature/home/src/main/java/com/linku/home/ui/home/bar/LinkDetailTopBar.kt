package com.linku.home.ui.home.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont
import com.linku.home.R

@Composable
fun LinkDetailTopBar(
    linkTitle: String,
    originalLinkTitle: String,
    category: String,
    emotion: String,
    situation: String,
    isEditMode: Boolean,
    isCategoryDropdownOpen: Boolean,
    isEmotionDropdownOpen: Boolean,
    isSituationDropdownOpen: Boolean,
    onBack: () -> Unit,
    onMoreClick: () -> Unit,
    onLinkGoClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onEmotionClick: () -> Unit,
    onSituationClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onTitleClearClick: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    val isTitleEmpty = linkTitle.isBlank()
    val isTitleChanged = linkTitle != originalLinkTitle
    val titleAlpha = if (isEditMode && !isTitleChanged && !isTitleEmpty) 0.3f else 1f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
            .background(colors.blue[200])
    ) {
        Image(
            painter = painterResource(R.drawable.linku_logo_transparent),
            contentDescription = null,
            modifier = Modifier
                .height(110.dp)
                .align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 59.dp, start = 20.dp, end = 24.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_back_white),
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(11.dp)
                        .noRippleClickable { onBack() }
                )

                Text(
                    text = if (isEditMode) "링크 수정하기" else "저장된 링크",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.white,
                    modifier = Modifier.align(Alignment.Center)
                )

                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.CenterEnd)
                        .noRippleClickable(enabled = !isEditMode) {
                            onMoreClick()
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = "더보기",
                        modifier = Modifier
                            .height(18.dp)
                            .align(AbsoluteAlignment.TopRight)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 29.dp, start = 24.dp, end = 24.dp, bottom = 23.dp)  // 편집 모드에서는 top = 20.dp
            ) {
                Row(
                    modifier = Modifier
                        .then(
                            if (isEditMode) {
                                Modifier.padding(bottom = 11.dp)
                            } else {
                                Modifier.padding(bottom = 12.dp)
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isEditMode) {
                        Row(
                            modifier = Modifier
                                .widthIn(min = 1.dp, max = 280.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .border(1.dp, colors.white, RoundedCornerShape(13.dp))
                                .padding(horizontal = 15.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BasicTextField(
                                value = linkTitle,
                                onValueChange = onTitleChange,
                                textStyle = TextStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = MaterialTheme.linkuFont.font,
                                    color = colors.white.copy(alpha = titleAlpha)
                                ),
                                modifier = Modifier.widthIn(min = 1.dp, max = 220.dp),
                                decorationBox = { innerTextField ->
                                    Box {
                                        if (isTitleEmpty) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Image(
                                                    painter = painterResource(R.drawable.ic_warning),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )

                                                Text(
                                                    text = "링크 제목을 입력하세요",
                                                    fontSize = 22.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colors.white,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        innerTextField()
                                    }
                                }
                            )

                            if (!isTitleEmpty) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .noRippleClickable { onTitleClearClick() }
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.ic_delete_blue),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = linkTitle,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.white,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        isEditMode && isCategoryDropdownOpen -> colors.white
                                        isEditMode -> colors.blue[200]
                                        else -> colors.purple[50]
                                    }
                                )  // 추후 카테고리 API 연동 후 실제 색상으로 변경 예정
                                .then(
                                    if(isEditMode) {
                                        Modifier.border(1.dp, colors.blue[100], RoundedCornerShape(10.dp))
                                    } else {
                                        Modifier
                                    }
                                )
                                .noRippleClickable(enabled = isEditMode) {
                                    onCategoryClick()
                                }
                                .padding(horizontal = 10.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = when {
                                    isEditMode && isCategoryDropdownOpen -> colors.blue[300]
                                    isEditMode -> colors.white
                                    else -> colors.black  // API 연동 후 수정 예정
                                }
                            )

                            if(isEditMode) {
                                Image(
                                    painter = painterResource(R.drawable.ic_toggle),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(12.dp)
                                        .rotate(if (isCategoryDropdownOpen) 180f else 0f)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        isEditMode && isEmotionDropdownOpen -> colors.white
                                        isEditMode -> colors.blue[200]
                                        else -> colors.blue[50]
                                    }
                                )
                                .then(
                                    if(isEditMode) {
                                        Modifier.border(1.dp, colors.blue[100], RoundedCornerShape(10.dp))
                                    } else {
                                        Modifier
                                    }
                                )
                                .noRippleClickable(enabled = isEditMode) {
                                    onEmotionClick()
                                }
                                .padding(horizontal = 10.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = emotion,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = when {
                                    isEditMode && isEmotionDropdownOpen -> colors.blue[300]
                                    isEditMode -> colors.white
                                    else -> colors.blue[300]
                                }
                            )

                            if(isEditMode) {
                                Image(
                                    painter = painterResource(R.drawable.ic_toggle),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(12.dp)
                                        .rotate(if (isEmotionDropdownOpen) 180f else 0f)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        isEditMode && isSituationDropdownOpen -> colors.white
                                        isEditMode -> colors.blue[200]
                                        else -> colors.purple[50]
                                    }
                                )
                                .then(
                                    if(isEditMode) {
                                        Modifier.border(1.dp, colors.blue[100], RoundedCornerShape(10.dp))
                                    } else {
                                        Modifier
                                    }
                                )
                                .noRippleClickable(enabled = isEditMode) {
                                    onSituationClick()
                                }
                                .padding(horizontal = 10.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = situation,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = when {
                                    isEditMode && isSituationDropdownOpen -> colors.blue[300]
                                    isEditMode -> colors.white
                                    else -> colors.purple[300]
                                }
                            )

                            if(isEditMode) {
                                Image(
                                    painter = painterResource(R.drawable.ic_toggle),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(12.dp)
                                        .rotate(if (isSituationDropdownOpen) 180f else 0f)
                                )
                            }
                        }
                    }

                    if(!isEditMode) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .noRippleClickable {
                                    onLinkGoClick()
                                },
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_link_go),
                                contentDescription = null,
                                modifier = Modifier.height(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkDetailTopBar() {
    ThemeProvider {
        LinkDetailTopBar(
            linkTitle = "3일만에 오픽 AL 꿀팁",
            originalLinkTitle = "3일만에 오픽 AL 꿀팁",
            category = "어학",
            emotion = "평온",
            situation = "통학 중",
            isEditMode = true,
            isCategoryDropdownOpen = false,
            isEmotionDropdownOpen = false,
            isSituationDropdownOpen = false,
            onBack = { },
            onMoreClick = { },
            onLinkGoClick = { },
            onEmotionClick = { },
            onCategoryClick = { },
            onSituationClick = { },
            onTitleChange = { },
            onTitleClearClick = { }
        )
    }
}