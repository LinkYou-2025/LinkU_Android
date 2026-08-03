package com.linku.file.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.innerRingShadow
import com.linku.design.theme.linkuColors

/**
 * 폴더 카드의 기본 시각 구조를 그리는 공통 레이아웃입니다.
 *
 * 이 컴포저블은 카드 배경, 겹쳐진 폴더 레이어, 도형 기반 하단 [FolderMask], 좌우 아이콘 영역,
 * 폴더명 배지를 한 번에 구성합니다. 실제 카드 크기는 외부 [modifier]와 내부 기준
 * 비율을 함께 사용해 결정되며, 내부 요소는 [BoxWithConstraints]에서 계산한 스케일을
 * 기준으로 함께 축소/확대됩니다.
 *
 * @param modifier 카드의 외부 크기와 배치를 결정하는 [Modifier]입니다.
 * @param backgroundColor 카드 바탕과 [FolderMask] 내부의 불투명 기본 영역을 채우는 색상입니다.
 * @param color1 뒤쪽에 배치되는 첫 번째 폴더 레이어 색상입니다.
 * @param color2 가운데에 배치되는 두 번째 폴더 레이어 색상입니다.
 * @param color3 앞쪽에 배치되는 세 번째 폴더 레이어 색상입니다.
 * @param folderMaskBrush 하단 [FolderMask]의 불투명 기본 레이어 위에 합성할 전면 브러시입니다.
 * @param leftIcon 카드 왼쪽 상단에 배치할 아이콘 슬롯입니다.
 * @param rightIcon 카드 오른쪽 상단에 배치할 아이콘 슬롯입니다.
 * @param textBackgroundColor 폴더명 첫 글자 배지의 배경색입니다.
 * @param folderName 카드 하단에 표시할 폴더명입니다. 빈 문자열이면 폴더명 영역을 숨깁니다.
 */
@Composable
fun FolderItemLayout(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    color1: Color,
    color2: Color,
    color3: Color,
    folderMaskBrush: Brush,
    leftIcon: @Composable () -> Unit,
    rightIcon: @Composable () -> Unit,
    textBackgroundColor: Color,
    folderName: String = "",
) {
    /** 폴더명 배지와 텍스트에 사용할 LinkU 테마 색상 팔레트입니다. */
    val colors = MaterialTheme.linkuColors
    val backdropLayer = rememberGraphicsLayer()

    // 디자인 원본 기준 사이즈입니다. 카드 비율과 내부 요소 스케일 계산의 기준값으로 사용합니다.
    val baseW = 165.3.dp
    val baseH = 145.8535.dp
    val aspect = baseW / baseH
    val cardShape = RoundedCornerShape(28.5.dp)

    // 카드 자체의 모양은 유지하되, 폴더 마스크 그림자가 카드 경계 밖에서도 보이도록 자식은 자르지 않습니다.
    Box(
        modifier = modifier
            .aspectRatio(aspect, matchHeightConstraintsFirst = false)
            .shadow(
                elevation = 2.dp,
                shape = cardShape,
                clip = false,
            )
            .background(
                color = backgroundColor,
                shape = cardShape,
            )
    ) {
        /**
         * 실제 배치된 카드 크기를 기준으로 폴더 레이어, 아이콘 위치, 폰트 크기를 함께 스케일링합니다.
         *
         * 그리드 셀 크기가 달라져도 폴더 일러스트의 겹침과 하단 텍스트 영역의 비율을 유지하기 위해
         * 모든 기준 dp/sp 값을 같은 scale 값으로 변환합니다.
         */
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val scaleW = maxWidth / baseW
            val scaleH = maxHeight / baseH
            val scale = minOf(scaleW, scaleH)

            // 디자인 기준 dp, sp를 현재 카드 크기에 맞게 변환하는 헬퍼입니다.
            fun s(dp: Dp) = dp * scale
            fun ssp(sp: TextUnit) = (sp.value * scale).sp
            val maskHeight = s(116.dp)
            val maskTopOffset = maxHeight - maskHeight

            /**
             * 폴더 일러스트를 구성하는 단일 레이어를 현재 카드 크기에 맞춰 그립니다.
             *
             * @param color 레이어의 채움 색상입니다.
             * @param size 레이어의 기준 너비입니다.
             * @param height 레이어의 기준 높이입니다. 지정하지 않으면 [size]와 같은 값으로 사용합니다.
             * @param padding 기준 크기에서 적용할 외부 여백입니다.
             * @param rotation 레이어에 적용할 회전 각도입니다.
             */
            @Composable
            fun FolderLayerBox(
                color: Color,
                size: Dp,
                height: Dp = size,
                padding: PaddingValues = PaddingValues(0.dp),
                rotation: Float = 0f,
            ) {
                /** 회전과 그림자를 가진 폴더 뒷장/중간장/앞장 레이어입니다. */
                Surface(
                    modifier = Modifier
                        // 레이어별 기준 여백을 현재 카드 스케일에 맞게 변환합니다.
                        .padding(
                            PaddingValues(
                                start = s(padding.calculateStartPadding(LayoutDirection.Ltr)),
                                top = s(padding.calculateTopPadding()),
                                end = s(padding.calculateEndPadding(LayoutDirection.Ltr)),
                                bottom = s(padding.calculateBottomPadding())
                            )
                        )
                        // 레이어마다 다른 회전값을 적용해 폴더 종이가 겹친 느낌을 만듭니다.
                        .rotate(rotation)
                        // 레이어의 기준 너비/높이를 현재 카드 크기에 맞춰 스케일링합니다.
                        .width(s(size))
                        .height(s(height))
                        .shadow(
                            elevation = 4.dp,
                            ambientColor = Color.Black.copy(alpha = 0.5f),
                            spotColor = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(s(18.dp))
                        ),
                    shape = RoundedCornerShape(s(18.dp)),
                    color = color,
                ) {}
            }

            /** 폴더 레이어와 하단 마스크를 하나의 카드 좌표계에 겹쳐 배치하는 루트 컨테이너입니다. */
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                // 폴더 종이 레이어를 backdropLayer에 기록한 뒤 같은 콘텐츠를 현재 카드에도 그립니다.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            backdropLayer.renderEffect = null
                            backdropLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawContent()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    // 첫 번째 레이어: 가장 뒤쪽 종이입니다. 원본: 105.45, padding bottom 5.7, rot -7.39
                    FolderLayerBox(
                        color = color1,
                        size = 105.45.dp,
                        padding = PaddingValues(bottom = 5.7.dp),
                        rotation = -7.39f
                    )
                    // 두 번째 레이어: 가운데 종이입니다. 원본: 105.45, padding bottom 3.1825, rot 4.86
                    FolderLayerBox(
                        color = color2,
                        size = 105.45.dp,
                        padding = PaddingValues(bottom = 3.1825.dp),
                        rotation = 4.86f
                    )
                    // 세 번째 레이어: 가장 앞쪽 흰색/컬러 종이입니다. 원본: size 126.407 x 107.9605, padding top 7.6
                    FolderLayerBox(
                        color = color3,
                        size = 126.407.dp,
                        height = 107.9605.dp,
                        padding = PaddingValues(top = 7.6.dp),
                        rotation = 0f
                    )
                }

                /** 하단 폴더 마스크, 아이콘 슬롯, 폴더명 배지를 배치하는 영역입니다. */
                Box(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    // Compose FolderMask는 캡처한 뒤쪽 폴더 레이어와 전달받은 전면 브러시를 카드 하단에 합성합니다.
                    FolderMask(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(maskHeight)
                            .align(Alignment.BottomCenter),
                        brush = folderMaskBrush,
                        baseColor = backgroundColor,
                        shadow = Shadow(
                            radius = 10.dp,
                            spread = 0.dp,
                            offset = DpOffset(
                                x = 0.dp,
                                y = (-4).dp,
                            ),
                            color = Color.Black,
                            alpha = 0.1f,
                        ),
                        backdropLayer = backdropLayer,
                        backdropOffsetY = maskTopOffset,
                        backdropBlurRadius = 4.dp,
                    )

                    // 왼쪽 상단 슬롯입니다. 공유/잠금 아이콘처럼 폴더 상태를 나타내는 아이콘을 배치합니다.
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = s(12.5495.dp), start = s(19.95.dp))
                    ) {
                        leftIcon()
                    }

                    // 오른쪽 상단 슬롯입니다. 북마크/편집 아이콘처럼 사용자가 누르는 액션을 배치합니다.
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = s(25.8685.dp), end = s(19.dp))
                    ) {
                        rightIcon()
                    }

                    // 폴더명이 있을 때만 하단에 첫 글자 배지와 이름 텍스트를 표시합니다.
                    if (folderName.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = s(17.499.dp), bottom = s(17.499.dp)),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(s(7.7805.dp))
                        ) {
                            /** 폴더명 첫 글자를 보여주는 원형 배지입니다. */
                            Box(
                                modifier = Modifier
                                    .size(s(29.1745.dp))
                                    .clip(shape = CircleShape)
                                    .background(color = textBackgroundColor)
                                    .innerRingShadow(
                                        shadowColor = Color.Black.copy(alpha = 0.10f),
                                        edgeThickness = 8.dp
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                /** 폴더명의 첫 글자를 배지 중앙에 표시합니다. */
                                Text(
                                    text = folderName.first().toString(),
                                    fontSize = ssp(15.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = colors.white,
                                )
                            }

                            /** 폴더 전체 이름입니다. 길이가 길면 한 줄에서 말줄임 처리합니다. */
                            Text(
                                modifier = Modifier
                                    .padding(end = s(17.499.dp)),
                                text = folderName,
                                fontSize = ssp(15.sp),
                                fontWeight = FontWeight.Medium,
                                color = colors.black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
