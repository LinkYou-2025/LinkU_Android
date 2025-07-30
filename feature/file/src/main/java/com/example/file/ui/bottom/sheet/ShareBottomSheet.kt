package com.example.file.ui.bottom.sheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.file.R
import com.example.file.ui.item.EmptyFolderItemLayout
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray400
import com.example.file.ui.theme.MainColor

@Composable
fun ShareBottomSheet(
    topFolderName: String,
    visible: Boolean,
    onDismiss: () -> Unit
){
    FileBottomSheet(
        title = "폴더를 공유하시겠습니까?",
        body = "공유하실 파일의 카테고리와 폴더를 선택해주세요!",
        buttonText = "공유 링크 생성",
        visible = visible,
        onDismiss = onDismiss
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            EmptyFolderItemLayout(topFolderName)

            Spacer(modifier = Modifier.height(45.59.dp))

            Row(
                modifier = Modifier
                    .width(300.dp)
                    .height(51.dp)
                    .border(1.dp, MainColor, RoundedCornerShape(18.dp))
                    .padding(horizontal = 21.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "공유하실 폴더의 카테고리를 선택해주세요.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = DefaultFont,
                    fontWeight = FontWeight(400),
                    color = Gray400,
                )
                Image(
                    painter = painterResource(R.drawable.check_img),
                    contentDescription = null,
                    modifier = Modifier
                        .width(14.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(MainColor, blendMode = BlendMode.SrcAtop)
                            }
                        }
                        .shadow(
                            elevation = 10.dp,
                            ambientColor = Color.Black.copy(alpha = 0.5f),
                            spotColor = Color.Black.copy(alpha = 0.5f),
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .width(300.dp)
                    .height(51.dp)
                    .border(1.dp, MainColor, RoundedCornerShape(18.dp))
                    .padding(horizontal = 21.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "공유하실 폴더를 선택해주세요.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = DefaultFont,
                    fontWeight = FontWeight(400),
                    color = Gray400,
                )
                Image(
                    painter = painterResource(R.drawable.check_img),
                    contentDescription = null,
                    modifier = Modifier
                        .width(14.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(MainColor, blendMode = BlendMode.SrcAtop)
                            }
                        }
                        .shadow(
                            elevation = 10.dp,
                            ambientColor = Color.Black.copy(alpha = 0.5f),
                            spotColor = Color.Black.copy(alpha = 0.5f),
                        )
                )
            }


        }
        Spacer(modifier = Modifier.height(43.dp))
    }
}

@Preview(showBackground = true, heightDp = 2000)
@Composable
fun ShareBottomSheetPreview(){
    ShareBottomSheet(
        "세나의 폴더",
        true,
    ) {}
}