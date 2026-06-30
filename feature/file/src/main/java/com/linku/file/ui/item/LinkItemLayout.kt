// 폴더 단위의 레이아웃

package com.linku.file.ui.item

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import com.linku.core.model.LinkItemInfo
import com.linku.design.theme.linkuColors
import com.linku.file.R
import com.linku.file.ui.theme.domainLogoPainterOrNull

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalTextApi::class
)
@Composable
fun LinkItemLayout(
    modifier: Modifier = Modifier,
    link: LinkItemInfo? = null,
    onClick: (LinkItemInfo?) -> Unit = {},
    onLongClick: (Long) -> Unit = {},
) {
    val colors = MaterialTheme.linkuColors

    val tags = link?.tags ?: emptyList()

    val domainIcon = link?.let { domainLogoPainterOrNull(it.url) }

    val isNotAdder = link != null

    val painter = ImageRequest.Builder(LocalContext.current)
        .data(link?.linkuImageUrl) // url이 null일 수도 있음
        .crossfade(true)
        .placeholder(R.drawable.link_categorization_default)
        .error(R.drawable.link_categorization_default)
        .fallback(R.drawable.link_categorization_default) // null이면 이거 표시
        .build()

    val baseWidth = 181.dp
    val baseHeight = 267.dp
    val aspect = baseWidth / baseHeight

    val clickableModifier = if (link != null) Modifier.combinedClickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = {
                link.linkuId.let {
                    Log.d("LinkItemLayout", "아이템 클릭: \"savelinkresult/${it}\"")
                    onClick(link)
                }
            },
            onLongClick = {
                onLongClick(link.linkuId)
            }
        )
    else Modifier

    // 전체 카드 바탕 Surface
    Surface(
        modifier = modifier
            .aspectRatio(aspect, matchHeightConstraintsFirst = false)
            .then(clickableModifier)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        color = colors.white,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val scaleW = maxWidth / baseWidth
            val scaleH = maxHeight / baseHeight
            val scale = minOf(scaleW, scaleH)

            fun s(dp: Dp) = dp * scale
            fun ssp(textUnit: TextUnit) = (textUnit.value * scale).sp

            @Composable
            fun LinkItemTag(tag: String) {
                Box(
                    modifier = Modifier
                        .background(
                            shape = RoundedCornerShape(size = s(6.dp)),
                            color = colors.gray[100]
                        )
                        .padding(horizontal = s(6.dp), vertical = s(1.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = s(1.dp), vertical = s(2.dp)),
                        text = tag,
                        fontSize = ssp(12.sp),
                        fontWeight = FontWeight.Normal,
                        color = colors.gray[600],
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(s(11.dp))
                    .alpha(if (isNotAdder) 1f else 0.35f)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(s(18.dp)))
                        .size(s(157.dp))
                        .align(Alignment.CenterHorizontally)
                        .background(color = if (isNotAdder) colors.gray[100] else colors.white),
                    contentAlignment = Alignment.Center
                ) {
                    if (isNotAdder) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            modifier = Modifier.fillMaxWidth(90f / 157f),
                            painter = painterResource(com.linku.design.R.drawable.logo_whiteback),
                            tint = colors.gray[400],
                            contentDescription = null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(s(10.dp)))

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = link?.title ?: "제목",
                    fontSize = ssp(15.sp),
                    fontWeight = FontWeight(500),
                    color = colors.black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(s(1.dp)))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(s(30.dp)),
                    horizontalArrangement = Arrangement.spacedBy(s(5.dp), Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    items(tags) {
                        LinkItemTag(it)
                    }
                }

                Spacer(modifier = Modifier.height(s(5.dp)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(s(7.dp), Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(s(26.dp))
                            .clip(CircleShape)
                            .background(colors.gray[200]),
                        contentAlignment = Alignment.Center
                    ) {
                        domainIcon?.let {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = it,
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Text(
                        modifier = Modifier.weight(1f),
                        text = link?.url ?: "도메인",
                        fontSize = ssp(12.sp),
                        fontWeight = FontWeight.Bold,
                        color = colors.gray[800],
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LinkItemTest() {
    val colors = MaterialTheme.linkuColors

    Box(
        contentAlignment = Alignment.TopCenter
    ){
        Box(
            //modifier = Modifier.alpha(0.35f),
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
            fontWeight = FontWeight(500),
            color = colors.black,
            textAlign = TextAlign.Center,
        )
    }

    LinkItemLayout()
}
