// 폴더 단위의 레이아웃

package com.example.file.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.file.R
import com.example.file.modifier.noRippleClickable
import com.example.file.ui.content.BookMarkStar
import com.example.file.ui.content.PencilIcon
import com.example.file.ui.state.EditStateViewModel
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.CategoryColorStyle
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray100
import com.example.file.ui.theme.Gray200
import com.example.file.ui.theme.Gray300
import com.example.file.ui.theme.Gray500
import com.example.file.ui.theme.White

@Composable
fun FolderItemLayout(
    backgroundColor: Color,
    color1: Color,
    color2: Color,
    color3: Color,
    folderMaskBrush: Brush,
    leftIcon: @Composable () -> Unit,
    rightIcon: @Composable () -> Unit,
    textBackgroundColor: Color,
    categoryName: String = "",
) {

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
                .padding(padding)
                .rotate(rotation)
                .width(size)
                .height(height)
                .shadow(
                    elevation = 4.dp,
                    ambientColor = Color.Black.copy(alpha = 0.5f), // 그림자 강도 조절!
                    spotColor = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            color = color,
        ) {}
    }

    Surface(
        modifier = Modifier
            .width(174.dp)
            .height(153.53.dp),
        shape = RoundedCornerShape(30.dp),
        color = backgroundColor, // Surface에서 배경 색상을 지정!
        shadowElevation = 4.dp // 그림자도 Surface에서 한 번만!
    ) {
        // 내부 Box: 배경/클립/쉐도우 중복 제거
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 1. Folder 컬러 Layer Box들 (세 개를 함수화해서 코드 간결)
            FolderLayerBox(
                color = color1,
                size = 111.dp,
                padding = PaddingValues(bottom = 6.dp),
                rotation = -7.39f
            )
            FolderLayerBox(
                color = color2,
                size = 111.dp,
                padding = PaddingValues(bottom = 3.35.dp),
                rotation = 4.86f
            )
            FolderLayerBox(
                color = color3,
                size = 133.06.dp,
                height = 112.59.dp,
                padding = PaddingValues(top = 8.dp),
                rotation = 0f
            )

            // 2. 폴더 마스크 + 그라데이션
            Box(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Image(
                    painter = painterResource(R.drawable.folder_mask),
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
                            elevation = 10.dp,
                            ambientColor = Color.Black.copy(alpha = 0.5f),
                            spotColor = Color.Black.copy(alpha = 0.5f),
                        )
                )

                // 3. 왼쪽 아이콘
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 13.21.dp, start = 21.dp)
                ) {
                    leftIcon()
                }

                // 4. 오른쪽 아이콘
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 27.23.dp, end = 20.dp)
                ) {
                    rightIcon()
                }

                // 5. 카테고리명 라벨(아이콘+텍스트)
                if(categoryName.isNotEmpty()){
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = 18.42.dp, y = (-18.42).dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.19.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.71.dp)
                                .clip(CircleShape)
                                .background(color = textBackgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = categoryName.first().toString(),
                                fontSize = 15.sp,
                                fontFamily = DefaultFont,
                                fontWeight = FontWeight.Bold,
                                color = White,
                            )
                        }

                        Text(
                            text = categoryName,
                            fontSize = 15.sp,
                            fontFamily = DefaultFont,
                            fontWeight = FontWeight.Medium,
                            color = Black,
                        )
                    }
                }
            }
        }
    }
}

val topFolderMaskBrush = Brush.verticalGradient(
    colorStops = arrayOf(
        1.0f to Gray100.copy(alpha = 0.7f),
        0.2f to Gray200.copy(alpha = 1.0f),
    )
)

@Composable
fun EmptyFolderItemLayout(
    categoryName: String = ""
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
        categoryName = categoryName
    )
}

@Composable
fun TopFolderItemLayout(
    categoryColorStyle: CategoryColorStyle,
    categoryName: String = "",
    isBookmarked: Boolean = false
){
    var bookmark by remember { mutableStateOf(isBookmarked) }

    FolderItemLayout(
        backgroundColor = Gray200,
        color1 = categoryColorStyle.color3,
        color2 = categoryColorStyle.color2,
        color3 = categoryColorStyle.color1,
        folderMaskBrush = topFolderMaskBrush,
        leftIcon = {},
        rightIcon = {
            Box(
                modifier = Modifier
                    .noRippleClickable{ bookmark = !bookmark }
            ){
                BookMarkStar(bookmark)
            }
        },
        textBackgroundColor = categoryColorStyle.color4,
        categoryName = categoryName
    )
}

@Composable
fun BottomFolderItemLayout(
    categoryColorStyle: CategoryColorStyle,
    categoryName: String,
    editStateViewModel: EditStateViewModel
){
    FolderItemLayout(
        backgroundColor = categoryColorStyle.color1,
        color1 = categoryColorStyle.color2,
        color2 = categoryColorStyle.color1,
        color3 = White,
        folderMaskBrush = categoryColorStyle.verticalGradient(),
        leftIcon = {},
        rightIcon = {
            if(editStateViewModel.isEditMode){
                Box(
                    modifier = Modifier
                ) {
                    PencilIcon(categoryColorStyle.color2)
                }
            }
        },
        textBackgroundColor = categoryColorStyle.color4,
        categoryName = categoryName
    )
}

@Preview(showBackground = true)
@Composable
fun FolderItemTest() {
    Column{
        EmptyFolderItemLayout()
        TopFolderItemLayout(
            categoryColorStyle = CategoryColorStyle.categoryStyleList[0],
            categoryName = "기본"
        )
        BottomFolderItemLayout(
            categoryColorStyle = CategoryColorStyle.categoryStyleList[0],
            categoryName = "기본",
            editStateViewModel = viewModel()
        )
    }
}

