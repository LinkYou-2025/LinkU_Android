// 폴더 단위의 레이아웃

package com.linku.file.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.modifier.innerRingShadow
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
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

    // 디자인 원본 기준 사이즈 (너가 쓰던 고정 dp)
    val baseW = 165.3.dp
    val baseH = 145.8535.dp
    val aspect = baseW / baseH // ≈ 1.1327

    // 외부에서 너비만 정하고 싶으면: fillMaxWidth().aspectRatio(aspect)
    // 높이를 정했다면: 원하는 height + .fillMaxWidth() 제거 등 자유롭게
    Surface(
        modifier = modifier
            .aspectRatio(aspect, matchHeightConstraintsFirst = false),
        shape = RoundedCornerShape(28.5.dp),
        color = backgroundColor,
        shadowElevation = 3.8.dp
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {

            // 에러 경고 제거용 변수
            val tmp = this

            // 현재 실제 너비/높이에 맞춰 스케일 계산
            val scaleW = maxWidth / baseW
            val scaleH = maxHeight / baseH
            val scale = minOf(scaleW, scaleH)

            // dp, sp를 스케일하는 헬퍼
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
                            // elevation은 스케일하지 않음: 시각적 깊이 유지
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
                // 원본: 105.45, padding bottom 5.7, rot -7.39
                FolderLayerBox(
                    color = color1,
                    size = 105.45.dp,
                    padding = PaddingValues(bottom = 5.7.dp),
                    rotation = -7.39f
                )
                // 원본: 105.45, padding bottom 3.1825, rot 4.86
                FolderLayerBox(
                    color = color2,
                    size = 105.45.dp,
                    padding = PaddingValues(bottom = 3.1825.dp),
                    rotation = 4.86f
                )
                // 원본: size 126.407 x 107.9605, padding top 7.6
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
                    // 마스크 이미지는 가로를 꽉 채우고 브러시를 입힘
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
                                elevation = 9.5.dp, // elevation은 고정
                                ambientColor = Color.Black.copy(alpha = 0.5f),
                                spotColor = Color.Black.copy(alpha = 0.5f),
                            )
                    )

                    // 아이콘 위치/패딩 스케일
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

@Composable
fun EmptyFolderItemLayout(
    modifier: Modifier = Modifier,
    folderName: String = ""
){
    val colors = MaterialTheme.linkuColors

    FolderItemLayout(
        backgroundColor = colors.gray[200],
        color1 = colors.gray[300],
        color2 = colors.gray[200],
        color3 = colors.white,
        folderMaskBrush = Brush.verticalGradient(
            colorStops = arrayOf(
                1.0f to colors.gray[100].copy(alpha = 0.7f),
                0.2f to colors.gray[200].copy(alpha = 1.0f),
            )
        ),
        leftIcon = {},
        rightIcon = {},
        textBackgroundColor = colors.gray[500],
        folderName = folderName,
        modifier = modifier
    )
}

@Composable
fun CategoryItemLayout(
    modifier: Modifier = Modifier,
    colorStyle: CategoryColorStyle,
    folder: FolderSimpleInfo,
    visibleBookmarked: Boolean = true,
    isEditMode: Boolean = false,
    onBookmark: () -> Unit
){
    val colors = MaterialTheme.linkuColors

    FolderItemLayout(
        backgroundColor = colors.gray[200],
        color1 = colorStyle.color3,
        color2 = colorStyle.color2,
        color3 = colorStyle.color1,
        folderMaskBrush = Brush.verticalGradient(
            colorStops = arrayOf(
                1.0f to colors.gray[100].copy(alpha = 0.7f),
                0.2f to colors.gray[200].copy(alpha = 1.0f),
            )
        ),
        leftIcon = {},
        rightIcon = {
            if(visibleBookmarked){
                if (isEditMode) {
                    Box(
                        modifier = Modifier
                    ) {
                        PencilIcon(colorStyle.color2)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .noRippleClickable {
                                onBookmark()
                            }
                    ) {
                        BookMarkStar(folder.isBookmarked)
                    }
                }
            }
        },
        textBackgroundColor = colorStyle.color4,
        folderName = folder.folderName,
        modifier = modifier
    )
}

@Composable
fun MyFolderItemLayout(
    modifier: Modifier = Modifier,
    colorStyle: CategoryColorStyle,
    folder: FolderSimpleInfo,
    isEditMode: Boolean = false,
    onEdit: ()-> Unit = {},
    onChangeSharing: () -> Unit = {}
){
    val colors = MaterialTheme.linkuColors

    FolderItemLayout(
        backgroundColor = colorStyle.color1,
        color1 = colorStyle.color2,
        color2 = colorStyle.color1,
        color3 = colors.white,
        folderMaskBrush = colorStyle.verticalGradient(),
        leftIcon = {
            Box(
                modifier = Modifier.noRippleClickable{
                    if (isEditMode) {
                        onChangeSharing()
                    }
                }
            ){
                folder.isSharing?.let{
                    when(it){
                        "share" -> ShareFolderIcon(colorStyle.color2)
                        "personal" -> LockFolderIcon(colorStyle.color2)
                        else -> {}
                    }
                }
            }
        },
        rightIcon = {
            if (isEditMode) {
                Box(
                    modifier = Modifier.noRippleClickable{
                        onEdit()
                    }
                ) {
                    PencilIcon(colorStyle.color2)
                }
            }
        },
        textBackgroundColor = colorStyle.color4,
        folderName = folder.folderName,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun FolderItemTest() {
    Column{
        EmptyFolderItemLayout()
        CategoryItemLayout(
            colorStyle = CategoryColorStyle.categoryStyleList[0],
            folder = FolderSimpleInfo(
                folderId = 0,
                folderName = "기본skskskskskskskksksks",
                parentFolderId = 0,
                isBookmarked = false,
                isSharing = null
            )
        ){}
        MyFolderItemLayout(
            folder = FolderSimpleInfo(
                folderId = 0,
                folderName = "기본",
                parentFolderId = 0,
                isBookmarked = false,
                isSharing = "share"
            ),
            colorStyle = CategoryColorStyle.categoryStyleList[0],
        )
    }
}

