package com.linku.file.ui.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.linku.design.theme.linkuColors
import com.linku.file.R

/**
 * 북마크 별 벡터의 21×21 viewport 좌표를 실제 아이콘 크기에 맞춰 스케일한 도형이다.
 *
 * drawable과 동일한 윤곽을 사용해 안쪽 그림자가 별 내부에만 그려지도록 한다.
 */
private val BookmarkStarShape = GenericShape { size, _ ->
    val scaleX = size.width / 21f
    val scaleY = size.height / 21f

    fun x(value: Float) = value * scaleX
    fun y(value: Float) = value * scaleY

    moveTo(x(9.522f), y(1.705f))
    cubicTo(x(10.042f), y(0.496f), x(11.757f), y(0.496f), x(12.277f), y(1.705f))
    lineTo(x(14.007f), y(5.721f))
    cubicTo(x(14.224f), y(6.225f), x(14.699f), y(6.57f), x(15.246f), y(6.621f))
    lineTo(x(19.599f), y(7.025f))
    cubicTo(x(20.91f), y(7.146f), x(21.44f), y(8.776f), x(20.451f), y(9.645f))
    lineTo(x(17.166f), y(12.531f))
    cubicTo(x(16.753f), y(12.893f), x(16.572f), y(13.452f), x(16.693f), y(13.988f))
    lineTo(x(17.654f), y(18.253f))
    cubicTo(x(17.943f), y(19.537f), x(16.557f), y(20.544f), x(15.425f), y(19.872f))
    lineTo(x(11.665f), y(17.64f))
    cubicTo(x(11.193f), y(17.36f), x(10.606f), y(17.36f), x(10.134f), y(17.64f))
    lineTo(x(6.374f), y(19.872f))
    cubicTo(x(5.243f), y(20.544f), x(3.856f), y(19.537f), x(4.145f), y(18.253f))
    lineTo(x(5.107f), y(13.988f))
    cubicTo(x(5.227f), y(13.452f), x(5.046f), y(12.893f), x(4.633f), y(12.531f))
    lineTo(x(1.348f), y(9.645f))
    cubicTo(x(0.36f), y(8.776f), x(0.889f), y(7.146f), x(2.2f), y(7.025f))
    lineTo(x(6.553f), y(6.621f))
    cubicTo(x(7.1f), y(6.57f), x(7.575f), y(6.225f), x(7.793f), y(5.721f))
    close()
}

/** Figma의 북마크 별에 지정된 안쪽 그림자 값이다. */
private val BookmarkStarInnerShadow = Shadow(
    radius = 2.dp,
    spread = 0.dp,
    color = Color.Black.copy(alpha = 0.12f),
    offset = DpOffset(x = 0.dp, y = 2.dp),
)

// 공유 폴더 사람 아이콘
@Composable
internal fun ShareFolderIcon(
    tint: Color
) {
    Icon(
        tint = tint,
        painter = painterResource(R.drawable.shared_folder_person_icon),
        contentDescription = null,
    )
}

// 잠금 폴더 자물쇠 아이콘
@Composable
internal fun LockFolderIcon(
    tint: Color
) {
    Icon(
        tint = tint,
        painter = painterResource(R.drawable.lock_icon),
        contentDescription = null,
    )
}

// 수정 연필 아이콘
@Composable
internal fun PencilIcon(
    tint: Color
) {
    Icon(
        tint = tint,
        painter = painterResource(R.drawable.edit_pencil_icon),
        contentDescription = null,
    )
}

/**
 * 북마크 상태에 맞는 색상과 Figma 안쪽 그림자를 적용한 별 아이콘을 그린다.
 *
 * @param isBookmarked 북마크된 폴더이면 `true`, 아니면 `false`
 */
@Composable
internal fun BookMarkStar(
    isBookmarked: Boolean
) {
    val colors = MaterialTheme.linkuColors
    val modifier = if(isBookmarked) Modifier
        .graphicsLayer(alpha = 0.99f)
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(colors.maincolor, blendMode = BlendMode.SrcAtop)
            }
        } else Modifier
    Box {
        Icon(
            tint = colors.white,
            painter = painterResource(R.drawable.bookmark_star_icon),
            modifier = modifier,
            contentDescription = null,
        )
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .innerShadow(
                    shape = BookmarkStarShape,
                    shadow = BookmarkStarInnerShadow,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FolderIconsTest() {
    val colors = MaterialTheme.linkuColors
    Column {
        ShareFolderIcon(colors.blue[300])
        LockFolderIcon(colors.purple[200])
        PencilIcon(colors.gray[800])
        BookMarkStar(true)
    }
}
