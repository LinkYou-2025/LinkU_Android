package com.linku.file.ui.bottom.sheet

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.modal.ModalWindow
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.FileViewModel
import com.linku.file.R
import com.linku.file.ui.item.items.CategoryItemLayout
import com.linku.file.ui.item.items.EmptyFolderItemLayout
import com.linku.file.ui.item.items.MyFolderItemLayout
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShareBottomSheet(
    userName: String,
    folderStateViewModel: FolderStateViewModel,
    fileViewModel: FileViewModel
){
    val colors = MaterialTheme.linkuColors

    // 선택한 폴더들
    var selectedTopFolder by remember { mutableStateOf<FolderSimpleInfo?>(null) }
    var selectedBottomFolder by remember { mutableStateOf<FolderSimpleInfo?>(null) }

    // 카테고리 내 소분류 폴더들
    val bottomFolderList by fileViewModel.shareBottomSheetSubFolders.collectAsStateWithLifecycle()

    // 고른 상태
    var state by remember { mutableStateOf(FolderState.TOP) }

    // 메뉴가 열렸는지 확인
    var menuOpen by remember { mutableStateOf(false) }

    // 모달창 띄우기
    var modalOpen by remember { mutableStateOf(false) }

    // 복사할 앱링크
    var link by remember { mutableStateOf("") }

    // 클립보드에 복사를 위한 객체
    val clipboardManager = LocalClipboardManager.current

    // 바텀 시트 내 컨텍스트
    val context = LocalContext.current

    // 링크를 클립보드에 복사
    fun linkCopyToClipboard(){
        clipboardManager.setText(AnnotatedString(link))
        Toast.makeText(context, "복사되었습니다", Toast.LENGTH_SHORT).show()
    }

    // 메뉴 레이아웃
    @Composable
    fun ShareFolderMenu(
        text: String,
        folderList: List<FolderSimpleInfo>,
        selectable: Boolean,
        colorStyle: CategoryColorStyle? = null,
        onClick: (FolderSimpleInfo) -> Unit
    ){
        var expanded by remember { mutableStateOf(false) }
        val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "")

        Box(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 21.dp),
            contentAlignment = Alignment.TopCenter
        ){
            Row(
                modifier = Modifier
                    .width(300.dp)
                    .height(51.dp)
                    .border(
                        1.dp,
                        if (selectable) colors.maincolor else Brush.horizontalGradient(
                            listOf(colors.gray[400], colors.gray[400])
                        ),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 21.dp)
                    .noRippleClickable {
                        if (selectable) {
                            expanded = true
                            menuOpen = true
                        }
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(400),
                    color = if (selectable) colors.black else colors.gray[400],
                )

                val modifier = if (expanded) Modifier
                    .width(14.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                brush = colors.maincolor,
                                blendMode = BlendMode.SrcAtop,
                                alpha = if (selectable) 1f else 0.5f
                            )
                        }
                    } else Modifier.width(14.dp)

                Image(
                    painter = painterResource(R.drawable.check_img),
                    contentDescription = null,
                    modifier = modifier.rotate(rotation)
                )
            }

            DropdownMenu(
                modifier = Modifier
                    .width(300.dp)
                    .heightIn(max = 224.dp),
                shape = RoundedCornerShape(18.dp),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    menuOpen = false
                },
                containerColor = colors.white
            ) {
                for ((i, folder) in folderList.withIndex()) {
                    val categoryColorStyle =
                        fileViewModel.categoryColorMap.collectAsState().value[folder.folderName]

                    DropdownMenuItem(
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(CircleShape)
                                    .background(
                                        color = colorStyle?.color1
                                            ?: categoryColorStyle!!.color4
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = colorStyle?.color4
                                            ?: categoryColorStyle!!.color4,
                                        shape = CircleShape
                                    )
                            )
                        },
                        text = {
                            Text(
                                text = folder.folderName,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight(400),
                                color = colors.gray[800],
                                maxLines = 1,  // 한 줄만 보여주고
                                overflow = TextOverflow.Ellipsis  // 넘치면 ...으로 대체
                            )
                        },
                        onClick = {
                            onClick(folder)
                            expanded = false
                            menuOpen = false
                        }
                    )
                }
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // 바로 Expanded 상태
    )

    FileBottomSheet(
        sheetState = sheetState,
        title = "폴더를 공유하시겠습니까?",
        body = "공유하실 파일의 카테고리와 폴더를 선택해주세요!",
        buttonText = "공유 링크 생성",
        visible = folderStateViewModel.shareBottomSheetVisible,
        isReady = state == FolderState.LINKS,
        onOkay = {
            if(state == FolderState.LINKS){
                link = fileViewModel.shareFolder(selectedBottomFolder!!.folderId)
                linkCopyToClipboard()
                modalOpen = true
            }
        },
        onDismiss = {
            folderStateViewModel.updateShareBottomSheetVisible(false)
        }
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val categoryColorStyle = fileViewModel.categoryColorMap.collectAsState().value[selectedTopFolder?.folderName]

            when(state){
                FolderState.TOP -> {
                    EmptyFolderItemLayout(
                        modifier = Modifier.fillMaxSize(201.10968f/412f),
                        folderName = "${userName}의 폴더"
                    )
                }
                FolderState.BOTTOM -> {
                    CategoryItemLayout(
                        modifier = Modifier.fillMaxSize(201.10968f/412f),
                        colorStyle = categoryColorStyle?:CategoryColorStyle.categoryStyleList[0],
                        folder = selectedTopFolder!!
                    ) { }
                }
                FolderState.LINKS -> {
                    MyFolderItemLayout(
                        modifier = Modifier.fillMaxSize(201.10968f/412f),
                        colorStyle = categoryColorStyle?:CategoryColorStyle.categoryStyleList[0],
                        folder = selectedBottomFolder!!
                    )
                }
            }

            Spacer(modifier = Modifier.height(45.59.dp))

            // 중분류 선택
            ShareFolderMenu(
                text = selectedTopFolder?.folderName?:"공유하실 폴더의 카테고리를 선택해주세요.",
                folderList = fileViewModel.parentFolders.collectAsState().value,
                selectable = true
            ) {
                selectedTopFolder = it
                selectedBottomFolder = null
                fileViewModel.fetchSubfolders(parentFolderId = it.folderId)
                state = FolderState.BOTTOM
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 소분류 선택
            ShareFolderMenu(
                text = selectedBottomFolder?.folderName?:"공유하실 폴더를 선택해주세요.",
                folderList = bottomFolderList,
                selectable = state != FolderState.TOP,
                colorStyle = categoryColorStyle
            ) {
                selectedBottomFolder = it
                state = FolderState.LINKS
            }
        }

        // menuOpen 상태에 따라 높이를 애니메이션
        val bottomSpacerHeight by animateDpAsState(
            targetValue = if (menuOpen) 204.dp else 43.dp,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
            label = "bottom spacer height"
        )

        Spacer(modifier = Modifier.height(bottomSpacerHeight))
    }

    ModalWindow(
        visible = modalOpen,
        title = "링크가 복사되었습니다!",
        onOkay = {
            modalOpen = false
        },
        onDismiss = {},
        positiveText = "확인",
    ) {
        // 앱링크 주소
        Text(
            modifier = Modifier
                .noRippleClickable{
                    linkCopyToClipboard()
                },
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        // 폰트 크기 (14sp)
                        fontSize = 14.sp,

                        // 폰트 굵기 (Bold)
                        fontWeight = FontWeight.Normal,

                        // 텍스트 그라데이션 색상(링큐 메인 색상)
                        brush = colors.maincolor,
                    )
                ) {
                    // 실제 표시할 텍스트
                    append(link)
                }
            },
            textAlign = TextAlign.Center
        )

        //Spacer(modifier = Modifier.height(10.dp))

        // 설명
        Text(
            text = "이 링크를 전송하면, 받은 사용자가 해당 폴더에 있는\n링크를 열람하고 저장할 수 있습니다.\n\n단, 열람자는 링큐 계정으로 로그인되어 있어야 합니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal,
            color = colors.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}
