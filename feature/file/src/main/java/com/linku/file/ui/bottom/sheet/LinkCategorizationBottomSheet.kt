package com.linku.file.ui.bottom.sheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import com.linku.core.model.LinkItemInfo
import com.linku.design.component.CheckIndicator
import com.linku.design.component.CheckIndicatorStyle
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.file.FileViewModel
import com.linku.file.R
import com.linku.file.ui.theme.domainLogoPainterOrNull
import com.linku.file.ui.theme.extractDomainHost
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import kotlinx.coroutines.launch
private val LinkCategorizationThumbnailShape = RoundedCornerShape(12.dp)
private val LinkCategorizationThumbnailShadow = Shadow(
    radius = 15.dp,
    spread = 0.dp,
    offset = DpOffset(x = 0.dp, y = 4.dp),
    color = Color.Black,
    alpha = 0.03f,
)

/**
 * 현재 폴더에 추가할 미분류 링크를 선택하고 순차적으로 분류하는 바텀시트입니다.
 *
 * 선택 상태와 분류 작업은 이 기능 진입점이 소유하며, 공통 셸에는 버튼 활성화 여부와
 * 확인·dismiss 콜백만 전달합니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LinkCategorizationBottomSheet(
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
) {
    val links by fileViewModel.notCategorizationLinks.collectAsStateWithLifecycle()
    val selectedLinks = remember { mutableStateListOf<LinkItemInfo>() }
    val scope = rememberCoroutineScope()
    val selectedTopFolderName = folderStateViewModel.selectedTopFolder?.folderName.orEmpty()

    FileBottomSheet(
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        ),
        title = stringResource(
            R.string.link_categorization_bottom_sheet_title,
            selectedTopFolderName,
        ),
        body = stringResource(R.string.link_categorization_bottom_sheet_body),
        buttonText = stringResource(R.string.link_categorization_bottom_sheet_add),
        isReady = selectedLinks.isNotEmpty(),
        visible = folderStateViewModel.linkCategorizationBottomSheetVisible,
        onOkay = {
            // 공통 셸은 onOkay 직후 dismiss하므로 coroutine이 시작되기 전에 선택을 고정합니다.
            val linksToCategorize = selectedLinks.toList()
            scope.launch {
                val folderId = requireNotNull(folderStateViewModel.selectedBottomFolder?.folderId)

                linksToCategorize.forEach {
                    fileViewModel.updateLinkFolder(it, folderId)
                }

                // 여러 링크를 순차 분류한 뒤 한 번만 새 Pager를 만들어 중복 네트워크 요청을 피합니다.
                fileViewModel.refreshLinks(folderId)

                selectedLinks.clear()
            }
        },
        onDismiss = {
            selectedLinks.clear()
            folderStateViewModel.updateLinkCategorizationBottomSheetVisible(false)
        },
    ) {
        LinkCategorizationLinkList(
            links = links,
            selectedLinks = selectedLinks,
            onSelectionChange = { link, selected ->
                if (selected) {
                    if (selectedLinks.none { it.userLinkuId == link.userLinkuId }) {
                        selectedLinks.add(link)
                    }
                } else {
                    selectedLinks.removeAll { it.userLinkuId == link.userLinkuId }
                }
            },
        )
    }
}

@Composable
private fun LinkCategorizationLinkList(
    links: List<LinkItemInfo>,
    selectedLinks: List<LinkItemInfo>,
    onSelectionChange: (link: LinkItemInfo, selected: Boolean) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 270.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            items = links,
            key = LinkItemInfo::userLinkuId,
        ) { link ->
            LinkCategorizationLinkItem(
                link = link,
                selected = selectedLinks.any { it.userLinkuId == link.userLinkuId },
                onSelectionChange = { selected ->
                    onSelectionChange(link, selected)
                },
            )
        }
    }
}

@Composable
private fun LinkCategorizationLinkItem(
    link: LinkItemInfo,
    selected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val context = LocalContext.current
    val domain = extractDomainHost(link.url)
    val domainIcon = domain?.let { domainLogoPainterOrNull(it) }
    val thumbnailRequest = remember(context, link.linkuImageUrl) {
        ImageRequest.Builder(context)
            .data(link.linkuImageUrl)
            .crossfade(true)
            .placeholder(R.drawable.link_categorization_default)
            .error(R.drawable.link_categorization_default)
            .fallback(R.drawable.link_categorization_default)
            .build()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .semantics {
                toggleableState = ToggleableState(selected)
            }
            .noRippleClickable(role = Role.Checkbox) {
                onSelectionChange(!selected)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckIndicator(
            checked = selected,
            style = CheckIndicatorStyle.Outlined,
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(60.dp)
                .dropShadow(
                    shape = LinkCategorizationThumbnailShape,
                    shadow = LinkCategorizationThumbnailShadow,
                )
                .clip(LinkCategorizationThumbnailShape)
                .background(colors.gray[100]),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = thumbnailRequest,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            Spacer(modifier = Modifier.height(7.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = link.title,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(500),
                color = colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(colors.gray[200]),
                    contentAlignment = Alignment.Center,
                ) {
                    domainIcon?.let { icon ->
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = icon,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    }
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = domain
                        ?: stringResource(R.string.link_categorization_domain_placeholder),
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight(400),
                    color = colors.gray[800],
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(7.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun LinkCategorizationLinkItemPreview() {
    ThemeProvider {
        Box(modifier = Modifier.padding(20.dp)) {
            LinkCategorizationLinkItem(
                link = LinkItemInfo(
                    userLinkuId = 1L,
                    parentFolderId = 1L,
                    title = "일본어 듣기 연습 유튜브",
                    url = "",
                    linkuImageUrl = null,
                    createdAt = null,
                ),
                selected = true,
                onSelectionChange = {},
            )
        }
    }
}
