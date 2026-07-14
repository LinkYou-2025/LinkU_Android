package com.linku.file.ui.item.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.linku.design.modifier.skeleton
import com.linku.design.theme.LinkuPreview
import com.linku.file.R

private const val baseW = 165.3f
private const val baseH = 145.8535f
private const val aspect = baseW / baseH

/**
 * 폴더 아이템의 로딩 상태를 표시하는 스켈레톤 로더 컴포넌트입니다.
 *
 * 여러 개의 레이어를 회전시키고 겹쳐서 폴더 내부의 종이 뭉치를 표현하며,
 * 하단 마스크를 통해 폴더의 외형을 완성합니다. 지정된 종횡비([aspect])를 유지하며
 * 전달된 [modifier]의 크기에 맞춰 내부 요소들이 동적으로 스케일링됩니다.
 *
 * @param modifier 컨테이너 레이아웃에 적용할 [Modifier]
 */
@Composable
internal fun SkeletonFolderItem(
    modifier: Modifier
){

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(aspect, matchHeightConstraintsFirst = false)
    ) {
        val scaleW = maxWidth / baseW.dp
        val scaleH = maxHeight / baseH.dp
        val scale = minOf(scaleW, scaleH)

        /** 디자인 기준 dp, sp를 현재 카드 크기에 맞게 변환하는 헬퍼입니다. */
        fun s(dp: Dp) = dp * scale

        /**
         * 폴더 일러스트를 구성하는 단일 레이어를 현재 카드 크기에 맞춰 그립니다.
         *
         * @param size 레이어의 기준 너비입니다.
         * @param height 레이어의 기준 높이입니다. 지정하지 않으면 [size]와 같은 값으로 사용합니다.
         * @param padding 기준 크기에서 적용할 외부 여백입니다.
         * @param rotation 레이어에 적용할 회전 각도입니다.
         */
        @Composable
        fun FolderLayerBox(
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
                    .skeleton(isLoading = true),
                shape = RoundedCornerShape(s(18.dp)),
            ) {}
        }

        /** 폴더 레이어와 하단 마스크를 카드 중앙 기준으로 겹쳐 배치하는 루트 컨테이너입니다. */
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 배경면. SkeletonFolderItem 자체가 갖는 크기를 가짐.
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .skeleton(isLoading = true),
                shape = RoundedCornerShape(28.5.dp)
            ){}

            // 첫 번째 레이어: 가장 뒤쪽 종이입니다. 원본: 105.45, padding bottom 5.7, rot -7.39
            FolderLayerBox(
                size = 105.45.dp,
                padding = PaddingValues(bottom = 5.7.dp),
                rotation = -7.39f
            )

            // 두 번째 레이어: 가운데 종이입니다. 원본: 105.45, padding bottom 3.1825, rot 4.86
            FolderLayerBox(
                size = 105.45.dp,
                padding = PaddingValues(bottom = 3.1825.dp),
                rotation = 4.86f
            )

            // 세 번째 레이어: 가장 앞쪽 흰색/컬러 종이입니다. 원본: size 126.407 x 107.9605, padding top 7.6
            FolderLayerBox(
                size = 126.407.dp,
                height = 107.9605.dp,
                padding = PaddingValues(top = 7.6.dp),
                rotation = 0f
            )

            // 하단 폴더 마스크.
            Image(
                painter = painterResource(R.drawable.folder_mask),
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .skeleton(isLoading = true)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SkeletonFolderItemPreview(){
    LinkuPreview {
        SkeletonFolderItem(Modifier.fillMaxSize())
    }
}