/**
 * 링크 카드 UI를 구성하는 레이아웃 컴포저블을 정의합니다.
 */

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
import com.linku.design.theme.linkuFont
import com.linku.file.R
import com.linku.file.ui.theme.domainLogoPainterOrNull
import com.linku.file.ui.theme.extractDomainHost

/**
 * 링크 정보를 카드 형태로 표시합니다.
 *
 * [link]가 존재하면 썸네일, 제목, 태그 목록, 도메인 정보를 표시하고 클릭/길게 누르기
 * 동작을 활성화합니다. [link]가 `null`이면 링크 추가 아이템에서 재사용할 수 있는
 * 비활성 placeholder 카드로 렌더링됩니다. 카드 내부 요소는 기준 크기를 바탕으로
 * [BoxWithConstraints]에서 계산한 스케일에 맞춰 함께 축소/확대됩니다.
 *
 * @param modifier 카드의 외부 크기와 배치를 결정하는 [Modifier]입니다.
 * @param link 표시할 링크 정보입니다. `null`이면 placeholder 상태로 표시합니다.
 * @param onClick 링크 카드 클릭 시 호출되는 콜백입니다.
 * @param onLongClick 링크 카드 길게 누르기 시 링크 ID를 전달하는 콜백입니다.
 */
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
    /** 카드 배경, 텍스트, placeholder 색상을 가져오기 위한 LinkU 테마 색상 팔레트입니다. */
    val colors = MaterialTheme.linkuColors

    /** 태그 텍스트에 적용할 LinkU 테마 폰트입니다. */
    val font = MaterialTheme.linkuFont.font

    /** 링크가 없을 때는 태그 영역을 비워 placeholder 카드로 사용할 수 있게 합니다. */
    val tags = link?.tags ?: emptyList()

    /** 링크 URL에서 하단에 표시할 도메인을 추출합니다. */
    val domain = link?.url?.let(::extractDomainHost)

    /** 추출한 도메인에 대응하는 로컬 도메인 로고 painter를 찾습니다. */
    val domainIcon = domain?.let { domainLogoPainterOrNull(it) }

    /** 실제 링크 카드와 링크 추가용 placeholder 카드를 구분하는 플래그입니다. */
    val isNotAdder = link != null

    /** 링크 썸네일을 Coil로 로드하고, 실패/빈 값일 때 기본 이미지를 표시하도록 설정합니다. */
    val painter = ImageRequest.Builder(LocalContext.current)
        .data(link?.linkuImageUrl) // url이 null일 수도 있음
        .crossfade(true)
        .placeholder(R.drawable.link_categorization_default)
        .error(R.drawable.link_categorization_default)
        .fallback(R.drawable.link_categorization_default) // null이면 이거 표시
        .build()

    /** 디자인 원본 기준 카드 너비입니다. 내부 요소 스케일 계산의 기준값으로 사용합니다. */
    val baseWidth = 181.dp

    /** 디자인 원본 기준 카드 높이입니다. 내부 요소 스케일 계산의 기준값으로 사용합니다. */
    val baseHeight = 267.dp

    /** 외부에서 너비만 지정해도 카드 비율이 유지되도록 사용하는 원본 비율입니다. */
    val aspect = baseWidth / baseHeight

    /** 실제 링크가 있을 때만 click/long click을 활성화하고, placeholder 상태에서는 비활성화합니다. */
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

    // 전체 카드 바탕 Surface입니다. 외부 modifier가 크기를 정하고 aspectRatio가 카드 비율을 고정합니다.
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
        /**
         * 실제 배치된 카드 크기를 기준으로 내부 이미지, 간격, 폰트 크기를 함께 스케일링합니다.
         *
         * 그리드 셀 크기가 달라져도 링크 카드 내부 구성 비율이 유지되도록 모든 dp/sp 값에
         * 동일한 scale 값을 적용합니다.
         */
        BoxWithConstraints(Modifier.fillMaxSize()) {
            /** 현재 카드 너비가 디자인 기준 너비에 비해 얼마나 커지거나 작아졌는지 나타냅니다. */
            val scaleW = maxWidth / baseWidth

            /** 현재 카드 높이가 디자인 기준 높이에 비해 얼마나 커지거나 작아졌는지 나타냅니다. */
            val scaleH = maxHeight / baseHeight

            /** 너비/높이 중 더 작은 스케일을 사용해 내부 요소가 카드 밖으로 넘치지 않게 합니다. */
            val scale = minOf(scaleW, scaleH)

            /** 디자인 기준 dp 값을 현재 카드 크기에 맞는 dp 값으로 변환합니다. */
            fun s(dp: Dp) = dp * scale

            /** 디자인 기준 sp 값을 현재 카드 크기에 맞는 sp 값으로 변환합니다. */
            fun ssp(textUnit: TextUnit) = (textUnit.value * scale).sp

            /**
             * 링크 카드에 표시되는 단일 태그 칩입니다.
             *
             * @param tag 칩 내부에 표시할 태그명입니다.
             */
            @Composable
            fun LinkItemTag(tag: String) {
                /** 태그 텍스트를 감싸는 회색 배경 칩입니다. */
                Box(
                    modifier = Modifier
                        .background(
                            shape = RoundedCornerShape(size = s(6.dp)),
                            color = colors.gray[100]
                        )
                        .padding(horizontal = s(6.dp), vertical = s(1.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    /** 태그명은 한 줄 높이 안에서 폰트 패딩 없이 중앙 정렬합니다. */
                    Text(
                        modifier = Modifier
                            .padding(horizontal = s(1.dp), vertical = s(2.dp)),
                        text = tag,
                        fontSize = ssp(12.sp),
                        fontFamily = font,
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

            /** 링크 썸네일, 제목, 태그, 도메인 행을 위에서 아래로 배치하는 카드 본문입니다. */
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // 카드 내부 여백도 카드 크기에 맞춰 스케일링합니다.
                    .padding(s(11.dp))
                    // placeholder 상태에서는 배경 카드가 흐리게 보이도록 투명도를 낮춥니다.
                    .alpha(if (isNotAdder) 1f else 0.35f)
            ) {
                /** 카드 상단의 정사각형 썸네일 영역입니다. */
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(s(18.dp)))
                        .size(s(157.dp))
                        .align(Alignment.CenterHorizontally)
                        .background(color = if (isNotAdder) colors.gray[100] else colors.white),
                    contentAlignment = Alignment.Center
                ) {
                    if (isNotAdder) {
                        /** 실제 링크 상태에서는 네트워크 썸네일을 꽉 채워 표시합니다. */
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        /** 링크 추가 placeholder 상태에서는 기본 로고 아이콘을 표시합니다. */
                        Icon(
                            modifier = Modifier.fillMaxWidth(90f / 157f),
                            painter = painterResource(com.linku.design.R.drawable.logo_whiteback),
                            tint = colors.gray[400],
                            contentDescription = null
                        )
                    }
                }

                /** 빈 링크 카드에서는 제목, 태그, 도메인 영역을 구성하지 않습니다. */
                if (!isNotAdder) return@Column

                /** 썸네일과 제목 사이의 디자인 기준 간격입니다. */
                Spacer(modifier = Modifier.height(s(10.dp)))

                /** 링크 제목입니다. 긴 제목은 카드 너비 안에서 말줄임 처리합니다. */
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = link.title,
                    fontSize = ssp(15.sp),
                    lineHeight = ssp(15.sp),
                    fontWeight = FontWeight(500),
                    color = colors.black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                /** 제목과 태그 리스트 사이의 미세 간격입니다. */
                Spacer(modifier = Modifier.height(s(1.dp)))

                /** 링크 태그 목록입니다. 태그가 많을 수 있어 가로 스크롤 Row로 배치합니다. */
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

                /** 태그 리스트와 도메인 정보 행 사이의 간격입니다. */
                Spacer(modifier = Modifier.height(s(5.dp)))

                /** 도메인 아이콘과 URL 텍스트를 한 줄에 배치하는 하단 정보 행입니다. */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(s(7.dp), Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    /** 도메인 로고가 들어가는 원형 아이콘 영역입니다. */
                    Box(
                        modifier = Modifier
                            .size(s(26.dp))
                            .clip(CircleShape)
                            .background(colors.gray[200]),
                        contentAlignment = Alignment.Center
                    ) {
                        domainIcon?.let {
                            /** 로컬 매핑에 도메인 로고가 있을 때만 아이콘 이미지를 표시합니다. */
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = it,
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    /** 추출한 도메인 또는 placeholder 텍스트입니다. 긴 도메인은 말줄임 처리합니다. */
                    Text(
                        modifier = Modifier.weight(1f),
                        text = domain ?: "도메인",
                        fontSize = ssp(12.sp),
                        lineHeight = ssp(12.sp),
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

/**
 * [LinkItemLayout]의 링크 추가 placeholder 상태를 확인하기 위한 Compose Preview입니다.
 */
@Preview(showBackground = true)
@Composable
private fun LinkItemTest() {
    val colors = MaterialTheme.linkuColors

    /** 링크 추가 카드에서 사용하는 placeholder 오버레이 상태를 미리보기로 확인합니다. */
    Box(
        contentAlignment = Alignment.TopCenter
    ){
        Box(
            //modifier = Modifier.alpha(0.35f),
        ){
            /** 실제 링크가 없는 상태의 기본 카드 배경입니다. */
            LinkItemLayout(
                link = null
            )
        }

        /** 링크 추가 placeholder 위에 올라가는 추가 아이콘입니다. */
        Image(
            modifier = Modifier.padding(top = 103.dp),
            painter = painterResource(R.drawable.add_folder_icon),
            contentDescription = null
        )

        /** 링크 추가 placeholder 위에 올라가는 안내 라벨입니다. */
        Text(
            modifier = Modifier.padding(top = 147.dp),
            text = "링크 추가하기",
            fontSize = 15.sp,
            fontWeight = FontWeight(500),
            color = colors.black,
            textAlign = TextAlign.Center,
        )
    }

    /** 기본 파라미터 상태의 링크 카드도 함께 렌더링해 placeholder 표현을 확인합니다. */
    LinkItemLayout()
}
