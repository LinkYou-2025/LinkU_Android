// 폴더 단위의 레이아웃

package com.example.file.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.model.FolderSimpleInfo
import com.example.file.R
import com.example.design.modifier.noRippleClickable
import com.example.file.ui.content.BookMarkStar
import com.example.file.ui.content.LockFolderIcon
import com.example.file.ui.content.PencilIcon
import com.example.file.ui.content.ShareFolderIcon
import com.example.file.viewmodel.edit.state.EditStateViewModel
import com.example.file.ui.theme.Black
import com.example.design.theme.color.CategoryColorStyle
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray100
import com.example.file.ui.theme.Gray200
import com.example.file.ui.theme.Gray300
import com.example.file.ui.theme.Gray500
import com.example.file.ui.theme.White

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
    // 디자인 원본 기준 사이즈 (너가 쓰던 고정 dp)
    val baseW = 165.3.dp
    val baseH = 145.8535.dp
    val aspect = baseW / baseH // ≈ 1.1327

    // 외부에서 너비만 정하고 싶으면: fillMaxWidth().aspectRatio(aspect)
    // 높이를 정했다면: 원하는 height + .fillMaxWidth() 제거 등 자유롭게
    Surface(
        modifier = modifier
            .then(Modifier)
            .aspectRatio(aspect, matchHeightConstraintsFirst = false),
        shape = RoundedCornerShape(28.5.dp),
        color = backgroundColor,
        shadowElevation = 3.8.dp
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
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
                                    .clip(CircleShape)
                                    .background(color = textBackgroundColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = folderName.first().toString(),
                                    fontSize = ssp(15.sp),
                                    fontFamily = DefaultFont,
                                    fontWeight = FontWeight.Bold,
                                    color = White,
                                )
                            }

                            Text(
                                modifier = Modifier
                                    .padding(end = s(17.499.dp)),
                                text = folderName,
                                fontSize = ssp(15.sp),
                                fontFamily = DefaultFont,
                                fontWeight = FontWeight.Medium,
                                color = Black,
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

//@Composable
//fun FolderItemLayout(
//    modifier: Modifier = Modifier,
//    backgroundColor: Color,
//    color1: Color,
//    color2: Color,
//    color3: Color,
//    folderMaskBrush: Brush,
//    leftIcon: @Composable () -> Unit,
//    rightIcon: @Composable () -> Unit,
//    textBackgroundColor: Color,
//    folderName: String = "",
//) {
//
//    @Composable
//    fun FolderLayerBox(
//        color: Color,
//        size: Dp,
//        height: Dp = size,
//        padding: PaddingValues = PaddingValues(0.dp),
//        rotation: Float = 0f,
//    ) {
//        Surface(
//            modifier = Modifier
//                .padding(padding)
//                .rotate(rotation)
//                .width(size)
//                .height(height)
//                .shadow(
//                    elevation = 4.dp,
//                    ambientColor = Color.Black.copy(alpha = 0.5f), // 그림자 강도 조절!
//                    spotColor = Color.Black.copy(alpha = 0.5f),
//                    shape = RoundedCornerShape(18.dp)
//                ),
//            shape = RoundedCornerShape(18.dp),
//            color = color,
//        ) {}
//    }
//
//    Surface(
//        modifier = Modifier
//            .width(165.3.dp)
//            .height(145.8535.dp),
//        shape = RoundedCornerShape(28.5.dp),
//        color = backgroundColor,
//        shadowElevation = 3.8.dp
//    ) {
//        Box(
//            Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            FolderLayerBox(
//                color = color1,
//                size = 105.45.dp,
//                padding = PaddingValues(bottom = 5.7.dp),
//                rotation = -7.39f
//            )
//            FolderLayerBox(
//                color = color2,
//                size = 105.45.dp,
//                padding = PaddingValues(bottom = 3.1825.dp),
//                rotation = 4.86f
//            )
//            FolderLayerBox(
//                color = color3,
//                size = 126.407.dp,
//                height = 107.9605.dp,
//                padding = PaddingValues(top = 7.6.dp),
//                rotation = 0f
//            )
//
//            Box(
//                Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.BottomCenter)
//            ) {
//                Image(
//                    painter = painterResource(R.drawable.folder_mask),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .align(Alignment.BottomCenter)
//                        .graphicsLayer(alpha = 0.99f)
//                        .drawWithCache {
//                            onDrawWithContent {
//                                drawContent()
//                                drawRect(folderMaskBrush, blendMode = BlendMode.SrcAtop)
//                            }
//                        }
//                        .shadow(
//                            elevation = 9.5.dp,
//                            ambientColor = Color.Black.copy(alpha = 0.5f),
//                            spotColor = Color.Black.copy(alpha = 0.5f),
//                        )
//                )
//
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.TopStart)
//                        .padding(top = 12.5495.dp, start = 19.95.dp)
//                ) {
//                    leftIcon()
//                }
//
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.TopEnd)
//                        .padding(top = 25.8685.dp, end = 19.dp)
//                ) {
//                    rightIcon()
//                }
//
//                if(folderName.isNotEmpty()){
//                    Row(
//                        modifier = Modifier
//                            .padding(end = 5.dp)
//                            .align(Alignment.BottomStart)
//                            .offset(x = 17.499.dp, y = (-17.499).dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(7.7805.dp)
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .size(29.1745.dp)
//                                .clip(CircleShape)
//                                .background(color = textBackgroundColor),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = folderName.first().toString(),
//                                fontSize = 15.sp,
//                                fontFamily = DefaultFont,
//                                fontWeight = FontWeight.Bold,
//                                color = White,
//                            )
//                        }
//
//                        Text(
//                            text = folderName,
//                            fontSize = 15.sp,
//                            fontFamily = DefaultFont,
//                            fontWeight = FontWeight.Medium,
//                            color = Black,
//                            maxLines = 1, // 최대 1줄
//                            overflow = TextOverflow.Ellipsis // 잘리면 ... 표시
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

//@Composable
//fun FolderItemLayout(
//    backgroundColor: Color,
//    color1: Color,
//    color2: Color,
//    color3: Color,
//    folderMaskBrush: Brush,
//    leftIcon: @Composable () -> Unit,
//    rightIcon: @Composable () -> Unit,
//    textBackgroundColor: Color,
//    folderName: String = "",
//    modifier: Modifier = Modifier,           // ⬅️ weight를 바깥에서 주입할 수 있게 열어둠
//) {
//    // 설계 기준(원본) 크기: 165.3 x 145.8535 (dp)
//    val designW = 165.3.dp
//    val designH = 145.8535.dp
//    val aspect = 165.3f / 145.8535f // ≈ 1.13333
//
//    // 내부 박스/카드 조각
//    @Composable
//    fun FolderLayerBox(
//        color: Color,
//        size: Dp,
//        height: Dp = size,
//        padding: PaddingValues = PaddingValues(0.dp),
//        rotation: Float = 0f,
//        corner: Dp,
//        shadow: Dp,
//    ) {
//        Surface(
//            modifier = Modifier
//                .padding(padding)
//                .rotate(rotation)
//                .width(size)
//                .height(height)
//                .shadow(
//                    elevation = shadow,
//                    ambientColor = Color.Black.copy(alpha = 0.5f),
//                    spotColor = Color.Black.copy(alpha = 0.5f),
//                    shape = RoundedCornerShape(corner)
//                ),
//            shape = RoundedCornerShape(corner),
//            color = color,
//        ) {}
//    }
//
//    // 외곽 Surface는 고정 dp를 없애고, 비율만 고정 (가로 기준)
//    Surface(
//        modifier = modifier
//            .fillMaxWidth()       // 바깥(Row/Column)에서 weight로 폭을 결정하게 함
//            .aspectRatio(aspect), // 세로는 비율로 자동 결정
//        // 코너/섀도는 내부에서 스케일된 값으로 다시 지정 (아래 BoxWithConstraints 안에서)
//        color = Color.Transparent,
//    ) {
//        // 부모가 준 실제 width로 스케일 팩터 계산
//        BoxWithConstraints(Modifier.fillMaxSize()) {
//            val s = (maxWidth / designW) // 스케일(폭 기준). Dp/Dp -> Float
//
//            // 자주 쓰는 헬퍼
//            fun Dp.scaled() = this * s
//
//            // 라운드/섀도 값도 스케일 (룩 유지)
//            val cornerOuter = 28.5.dp.scaled()
//            val cornerInner = 18.dp.scaled()
//            val shadowCard  = 3.8.dp//.scaled()
//            val shadowLayer = 4.dp.scaled()
//            val imgShadow   = 9.5.dp.scaled()
//
//            // 겉 Surface에 최종 모양/섀도 적용
//            Surface(
//                modifier = Modifier.fillMaxSize(),
//                shape = RoundedCornerShape(cornerOuter),
//                color = backgroundColor,
//                shadowElevation = shadowCard
//            ) {
//                Box(
//                    Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    // 1층
//                    FolderLayerBox(
//                        color = color1,
//                        size = 105.45.dp.scaled(),
//                        padding = PaddingValues(bottom = 5.7.dp.scaled()),
//                        rotation = -7.39f,
//                        corner = cornerInner,
//                        shadow = shadowLayer
//                    )
//                    // 2층
//                    FolderLayerBox(
//                        color = color2,
//                        size = 105.45.dp.scaled(),
//                        padding = PaddingValues(bottom = 3.1825.dp.scaled()),
//                        rotation = 4.86f,
//                        corner = cornerInner,
//                        shadow = shadowLayer
//                    )
//                    // 3층
//                    FolderLayerBox(
//                        color = color3,
//                        size = 126.407.dp.scaled(),
//                        height = 107.9605.dp.scaled(),
//                        padding = PaddingValues(top = 7.6.dp.scaled()),
//                        rotation = 0f,
//                        corner = cornerInner,
//                        shadow = shadowLayer
//                    )
//
//                    Box(
//                        Modifier
//                            .fillMaxWidth()
//                            .align(Alignment.BottomCenter)
//                    ) {
//                        Image(
//                            painter = painterResource(R.drawable.folder_mask),
//                            contentDescription = null,
//                            contentScale = ContentScale.FillWidth,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .align(Alignment.BottomCenter)
//                                .graphicsLayer(alpha = 0.99f)
//                                .drawWithCache {
//                                    onDrawWithContent {
//                                        drawContent()
//                                        drawRect(folderMaskBrush, blendMode = BlendMode.SrcAtop)
//                                    }
//                                }
//                                .shadow(
//                                    elevation = imgShadow,
//                                    ambientColor = Color.Black.copy(alpha = 0.5f),
//                                    spotColor = Color.Black.copy(alpha = 0.5f),
//                                )
//                        )
//
//                        Box(
//                            modifier = Modifier
//                                .align(Alignment.TopStart)
//                                .padding(top = 12.5495.dp.scaled(), start = 19.95.dp.scaled())
//                        ) {
//                            leftIcon()
//                        }
//
//                        Box(
//                            modifier = Modifier
//                                .align(Alignment.TopEnd)
//                                .padding(top = 25.8685.dp.scaled(), end = 19.dp.scaled())
//                        ) {
//                            rightIcon()
//                        }
//
//                        if (folderName.isNotEmpty()) {
//                            Row(
//                                modifier = Modifier
//                                    .align(Alignment.BottomStart)
//                                    .offset(
//                                        x = 17.499.dp.scaled(),
//                                        y = (-17.499).dp.scaled()
//                                    ),
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(7.7805.dp.scaled())
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        .size(29.1745.dp.scaled())
//                                        .clip(CircleShape)
//                                        .background(color = textBackgroundColor),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Text(
//                                        text = folderName.first().toString(),
//                                        fontSize = 15.sp, // 폰트는 보통 px 고정 권장, 필요하면 스케일링도 가능
//                                        fontFamily = DefaultFont,
//                                        fontWeight = FontWeight.Bold,
//                                        color = White,
//                                    )
//                                }
//
//                                Text(
//                                    text = folderName,
//                                    fontSize = 15.sp,
//                                    fontFamily = DefaultFont,
//                                    fontWeight = FontWeight.Medium,
//                                    color = Black,
//                                    maxLines = 1,
//                                    overflow = TextOverflow.Ellipsis
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

val topFolderMaskBrush = Brush.verticalGradient(
    colorStops = arrayOf(
        1.0f to Gray100.copy(alpha = 0.7f),
        0.2f to Gray200.copy(alpha = 1.0f),
    )
)

@Composable
fun EmptyFolderItemLayout(
    modifier: Modifier = Modifier,
    folderName: String = ""
){
    FolderItemLayout(
        backgroundColor = Gray200,
        color1 = Gray300,
        color2 = Gray200,
        color3 = White,
        folderMaskBrush = topFolderMaskBrush,
        leftIcon = {},
        rightIcon = {},
        textBackgroundColor = Gray500,
        folderName = folderName,
        modifier = modifier
    )
}

@Composable
fun TopFolderItemLayout(
    modifier: Modifier = Modifier,
    colorStyle: CategoryColorStyle,
    folderName: String = "",
    isBookmarked: Boolean = false,
    editStateViewModel: EditStateViewModel,
    onBookmark: () -> Unit
){
    FolderItemLayout(
        backgroundColor = Gray200,
        color1 = colorStyle.color3,
        color2 = colorStyle.color2,
        color3 = colorStyle.color1,
        folderMaskBrush = topFolderMaskBrush,
        leftIcon = {},
        rightIcon = {
            if(editStateViewModel.isEditMode){
                Box(
                    modifier = Modifier
                ) {
                    PencilIcon(colorStyle.color2)
                }
            }else {
                Box(
                    modifier = Modifier
                        .noRippleClickable {
                            onBookmark()
                        }
                ) {
                    BookMarkStar(isBookmarked)
                }
            }
        },
        textBackgroundColor = colorStyle.color4,
        folderName = folderName,
        modifier = modifier
    )
}

@Composable
fun BottomFolderItemLayout(
    modifier: Modifier = Modifier,
    colorStyle: CategoryColorStyle,
    folder: FolderSimpleInfo,
    editStateViewModel: EditStateViewModel,
    onEdit: ()-> Unit = {},
    onChangeSharing: () -> Unit = {}
){
    FolderItemLayout(
        backgroundColor = colorStyle.color1,
        color1 = colorStyle.color2,
        color2 = colorStyle.color1,
        color3 = White,
        folderMaskBrush = colorStyle.verticalGradient(),
        leftIcon = {
            Box(
                modifier = Modifier.noRippleClickable{
                    if(editStateViewModel.isEditMode){
                        onChangeSharing()
                    }
                }
            ){
                folder.isSharing?.let{ sharing ->
                    if(sharing=="share"){
                        ShareFolderIcon(colorStyle.color1)
                    } else {
                        LockFolderIcon(colorStyle.color1)
                    }
                }
            }
        },
        rightIcon = {
            if(editStateViewModel.isEditMode){
                Box(
                    modifier = Modifier.noRippleClickable{
                        if(editStateViewModel.isEditMode) {
                            onEdit()
                        }
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
        TopFolderItemLayout(
            colorStyle = CategoryColorStyle.categoryStyleList[0],
            folderName = "기본skskskskskskskksksks",
            editStateViewModel = viewModel()
        ){}
        BottomFolderItemLayout(
            folder = FolderSimpleInfo(
                folderId = 0,
                folderName = "기본",
                parentFolderId = 0,
                isBookmarked = false,
                isSharing = "share"
            ),
            colorStyle = CategoryColorStyle.categoryStyleList[0],
            editStateViewModel = viewModel()
        )
    }
}

