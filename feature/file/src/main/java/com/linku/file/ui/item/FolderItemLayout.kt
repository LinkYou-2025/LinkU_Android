package com.linku.file.ui.item

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.innerRingShadow
import com.linku.design.theme.linkuColors
import com.linku.file.R

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
    val colors = MaterialTheme.linkuColors

    val baseW = 165.3.dp
    val baseH = 145.8535.dp
    val aspect = baseW / baseH

    Surface(
        modifier = modifier
            .aspectRatio(aspect, matchHeightConstraintsFirst = false),
        shape = RoundedCornerShape(28.5.dp),
        color = backgroundColor,
        shadowElevation = 3.8.dp
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val scaleW = maxWidth / baseW
            val scaleH = maxHeight / baseH
            val scale = minOf(scaleW, scaleH)

            fun s(dp: Dp) = dp * scale
            fun ssp(sp: TextUnit) = (sp.value * scale).sp

            @Composable
            fun FolderLayerBox(
                color: Color,
                size: Dp,
                height: Dp = size,
                padding: PaddingValues = PaddingValues(0.dp),
                rotation: Float = 0f,
            ) {
                Surface(
                    modifier = Modifier
                        .padding(
                            PaddingValues(
                                start = s(padding.calculateStartPadding(LayoutDirection.Ltr)),
                                top = s(padding.calculateTopPadding()),
                                end = s(padding.calculateEndPadding(LayoutDirection.Ltr)),
                                bottom = s(padding.calculateBottomPadding())
                            )
                        )
                        .rotate(rotation)
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

            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                FolderLayerBox(
                    color = color1,
                    size = 105.45.dp,
                    padding = PaddingValues(bottom = 5.7.dp),
                    rotation = -7.39f
                )
                FolderLayerBox(
                    color = color2,
                    size = 105.45.dp,
                    padding = PaddingValues(bottom = 3.1825.dp),
                    rotation = 4.86f
                )
                FolderLayerBox(
                    color = color3,
                    size = 126.407.dp,
                    height = 107.9605.dp,
                    padding = PaddingValues(top = 7.6.dp),
                    rotation = 0f
                )

                Box(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Image(
                        painter = painterResource(R.drawable.folder_mask),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(folderMaskBrush, blendMode = BlendMode.SrcAtop)
                                }
                            }
                            .shadow(
                                elevation = 9.5.dp,
                                ambientColor = Color.Black.copy(alpha = 0.5f),
                                spotColor = Color.Black.copy(alpha = 0.5f),
                            )
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = s(12.5495.dp), start = s(19.95.dp))
                    ) {
                        leftIcon()
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = s(25.8685.dp), end = s(19.dp))
                    ) {
                        rightIcon()
                    }

                    if (folderName.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = s(17.499.dp), bottom = s(17.499.dp)),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(s(7.7805.dp))
                        ) {
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
                                Text(
                                    text = folderName.first().toString(),
                                    fontSize = ssp(15.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = colors.white,
                                )
                            }

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
