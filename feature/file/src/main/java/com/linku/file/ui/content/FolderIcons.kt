package com.linku.file.ui.content

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.linku.design.theme.linkuColors
import com.linku.file.R

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

// 북마크 별 아이콘
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
    Icon(
        tint = colors.white,
        painter = painterResource(R.drawable.bookmark_star_icon),
        modifier = modifier,
        contentDescription = null,
    )
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