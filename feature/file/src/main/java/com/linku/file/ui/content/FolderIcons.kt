package com.linku.file.ui.content

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.linku.file.R
import com.linku.file.ui.theme.Blue300
import com.linku.file.ui.theme.Gray800
import com.linku.file.ui.theme.MainColor
import com.linku.file.ui.theme.Purple200
import com.linku.file.ui.theme.White

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
    val modifier = if(isBookmarked) Modifier
        .graphicsLayer(alpha = 0.99f)
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(MainColor, blendMode = BlendMode.SrcAtop)
            }
        } else Modifier
    Icon(
        tint = White,
        painter = painterResource(R.drawable.bookmark_star_icon),
        modifier = modifier,
        contentDescription = null,
    )
}

@Preview(showBackground = true)
@Composable
private fun FolderIconsTest() {
    Column {
        ShareFolderIcon(Blue300)
        LockFolderIcon(Purple200)
        PencilIcon(Gray800)
        BookMarkStar(true)
    }
}