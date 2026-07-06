package com.linku.file.ui.bottom.sheet

import android.content.ClipData
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.modifier.noRippleClickable
import com.linku.design.modifier.skeleton
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.ThemeColorScheme
import com.linku.design.theme.linkuColors
import com.linku.design.util.OuterShadowResourceImage
import com.linku.file.FileViewModel
import com.linku.file.R
import com.linku.file.ui.bottom.sheet.LinkGenerateState.Before
import com.linku.file.ui.bottom.sheet.LinkGenerateState.Done
import com.linku.file.ui.bottom.sheet.LinkGenerateState.Error
import com.linku.file.ui.bottom.sheet.LinkGenerateState.Loading
import com.linku.file.ui.bottom.sheet.ScreenState.Main
import com.linku.file.ui.bottom.sheet.ScreenState.Select
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

// 전체 화면 높이
private const val FULL_HEIGHT = 917f

/*--------------바텀 시트 메인 화면 요소 간 비율 및 상수 --------------*/

// 바텀 시트 메인 화면 크기 상수
private const val SHEET_MAIN_SCREEN_HEIGHT = 850f   // 바텀 시트 메인 화면 높이
private const val SHEET_MAIN_SCREEN_WIDTH = 412f    // 바텀 시트 메인 화면 너비

private const val SHEET_MAIN_SCREEN_HEIGHT_RATIO = SHEET_MAIN_SCREEN_HEIGHT / FULL_HEIGHT   // 바텀 시트 메인 화면 높이 비율

// 폴더 모형 크기 상수
private const val FOLDER_STRUCTURE_HEIGHT = 154.04047f  // 폴더 모형 높이
private const val FOLDER_STRUCTURE_WIDTH = 174f     // 폴더 모형 너비

private const val FOLDER_STRUCTURE_WIDTH_RATIO = FOLDER_STRUCTURE_WIDTH / SHEET_MAIN_SCREEN_WIDTH   // 폴더 모형 너비 비율

private const val CIRCLE_SIZE_IN_FOLDER = 30.70588f     // 폴더 내 원 크기 상수

private const val FOLDER_STRUCTURE_TOP_MARGIN_RATIO = 45f / SHEET_MAIN_SCREEN_HEIGHT  // 폴더 위 여백 비율
private const val FOLDER_STRUCTURE_BOTTOM_MARGIN_RATIO = 29.96f / SHEET_MAIN_SCREEN_HEIGHT    // 폴더 아래 여백 비율

// 폴더 공유 메뉴 크기 상수
private const val SHARE_FOLDER_MENU_WIDTH = 372f - 44f    // 폴더 공유 메뉴 너비 (양 옆 여백 너비 제거)

private const val SHARE_FOLDER_MENU_HEIGHT_RATIO = 52f / SHEET_MAIN_SCREEN_HEIGHT   // 폴더 공유 메뉴 높이 비율
private const val SHARE_FOLDER_MENU_WIDTH_RATIO = SHARE_FOLDER_MENU_WIDTH / SHEET_MAIN_SCREEN_WIDTH // 폴더 공유 메뉴 너비 비율

private const val SHARE_FOLDER_MENU_ARROW_WIDTH_RATIO = 7f / SHARE_FOLDER_MENU_WIDTH    // 폴더 공유 메뉴바 오른쪽 화살표 이미지 크기 비율

private const val SHARE_FOLDER_MENU_TOP_MARGIN_RATIO = 14f / SHEET_MAIN_SCREEN_HEIGHT   // 폴더 공유 메뉴 위 여백
private const val SHARE_FOLDER_MENU_BOTTOM_MARGIN_RATIO = 10f / SHEET_MAIN_SCREEN_HEIGHT    // 폴더 공유 메뉴 아래 여백

// 공유 링크 크기 상수
private const val SHARE_LINK_HEIGHT = 52f    // 공유 링크 높이
private const val SHARE_LINK_WIDTH = 372f    // 공유 링크 너비

private const val SHARE_LINK_HEIGHT_RATIO = SHARE_LINK_HEIGHT / SHEET_MAIN_SCREEN_HEIGHT    // 공유 링크 높이 비율
private const val SHARE_LINK_WIDTH_RATIO = SHARE_LINK_WIDTH / SHEET_MAIN_SCREEN_WIDTH   // 공유 링크 너비 비율

// 하단 버튼 크기 상수
private const val BUTTON_HEIGHT = 50f    // 하단 버튼 높이
private const val BUTTON_WIDTH = 372f    // 하단 버튼 너비

private const val BUTTON_HEIGHT_RATIO = BUTTON_HEIGHT / SHEET_MAIN_SCREEN_HEIGHT    // 하단 버튼 높이 비율
private const val BUTTON_WIDTH_RATIO = BUTTON_WIDTH / SHEET_MAIN_SCREEN_WIDTH   // 하단 버튼 너비 비율

private const val SMALL_BUTTON_WIDTH = 181f     // 작은 버튼 너비

private const val SMALL_BUTTON_WIDTH_RATIO = SMALL_BUTTON_WIDTH / BUTTON_WIDTH   // 작은 버튼 너비 비율

private const val COPY_ICON_WIDTH_RATIO = 19.00195f / SMALL_BUTTON_WIDTH  // 복사 아이콘 너비 비율

private const val SHARE_ICON_WIDTH_RATIO = 19.00195f / SMALL_BUTTON_WIDTH  // 공유 아이콘 너비 비율

/*--------------바텀 시트 메인 화면 요소 간 비율 및 상수 --------------*/

/*--------------바텀 시트 폴더 선택 화면 요소 간 비율 및 상수 --------------*/

// 바텀 시트 메인 화면 크기 상수
private const val SHEET_Select_SCREEN_HEIGHT = 598f // 바텀 시트 메인 화면 높이
private const val SHEET_Select_SCREEN_WIDTH = 412f  // 바텀 시트 메인 화면 너비

private const val SHEET_SELECT_SCREEN_HEIGHT_RATIO = SHEET_Select_SCREEN_HEIGHT / FULL_HEIGHT   // 바텀 시트 메인 화면 높이 비율

/*--------------바텀 시트 폴더 선택 화면 요소 간 비율 및 상수 --------------*/




/**
 * 공유할 대상의 탐색 깊이와 선택된 폴더 정보를 관리하는 실드 인터페이스(Sealed Interface).
 *
 * 바텀 시트 내에서 사용자가 어느 단계까지 폴더를 탐색했는지, 그리고 어떤 폴더가 최종적으로 선택되었는지를
 * 상태로 나타내기 위해 사용됩니다.
 *
 * - [None]: 아무것도 선택되지 않은 초기 상태.
 * - [Category]: 상위 카테고리만 선택되어 하위 폴더 목록을 탐색 중인 상태.
 * - [Folder]: 상위 카테고리와 그 내부의 특정 폴더까지 모두 선택 완료된 상태.
 *
 * @property depthOrder 계층 간의 비교를 위한 깊이 순서값.
 * [None] < [Category] < [Folder] 순서.
 */
private sealed interface SelectDepth: Comparable<SelectDepth> {
    val depthOrder: Int

    override fun compareTo(other: SelectDepth): Int =
        depthOrder.compareTo(other.depthOrder)
}

/**
 * 선택된 폴더가 없음을 나타내는 기본 상태 객체입니다.
 * [SelectDepth] 인터페이스를 구현하며, 폴더 공유 프로세스에서 초기 선택 상태를 정의할 때 사용됩니다.
 */
private object None : SelectDepth{
    override val depthOrder = 0
}

/**
 * 선택된 공유 대상의 깊이가 '카테고리' 수준임을 나타내는 데이터 클래스입니다.
 *
 * 사용자가 특정 카테고리를 선택했지만, 그 하위의 구체적인 폴더는 아직 선택하지 않았거나
 * 해당 카테고리 자체를 컨텍스트로 가질 때 사용됩니다.
 *
 * @property category 선택된 카테고리에 대한 간략한 정보 ([FolderSimpleInfo]).
 */
private open class Category(open val category: FolderSimpleInfo) : SelectDepth{
    override val depthOrder = 1
}

/**
 * 사용자가 선택한 폴더의 계층 구조 정보를 나타내는 데이터 클래스입니다.
 * [SelectDepth] 인터페이스의 구현체로, 상위 카테고리와 그 하위의 구체적인 폴더가 모두 선택된 상태를 의미합니다.
 *
 * @property category 선택된 폴더가 속한 상위 카테고리 정보.
 * @property folder 실제 공유 대상으로 선택된 하위 폴더 정보.
 */
private class Folder(override val category: FolderSimpleInfo, val folder: FolderSimpleInfo) : Category(category) {
    override val depthOrder = 2
}




/**
 * 바텀 시트 내에서 전환되는 화면의 종류를 정의하는 열거형 클래스.
 *
 * - [Main]: 폴더 공유를 위한 메인 미리보기 및 링크 생성 화면.
 * - [Select]: 공유할 폴더를 탐색하고 선택하는 화면.
 */
private enum class ScreenState {

    /**
     * 폴더 공유를 위한 메인 미리보기 및 링크 생성 화면.
     */
    Main,

    /**
     * 공유할 폴더를 탐색하고 선택하는 화면.
     */
    Select
}




/**
 * 공유 링크 생성 프로세스의 현재 상태를 나타내는 열거형 클래스.
 *
 * 로딩 상태 별 UI 표시(공유 링크 레이아웃 스켈레톤, 하단 버튼 리스트 전환 등)에 활용.
 *
 * - [Before]: 링크 생성이 아직 시작되지 않은 초기 상태.
 * - [Loading]: 링크 생성을 서버나 내부 로직에 요청하여 현재 처리 중인 상태.
 * - [Done]: 공유 링크 생성이 성공적으로 완료된 상태.
 * - [Error]: 공유 링크 생성 중 오류가 발생한 상태.
 */
private enum class LinkGenerateState {

    /**
     * 링크 생성이 아직 시작되지 않은 초기 상태.
     * - **UI 동작**: 사용자에게 '공유 링크 생성' 버튼이 활성화.
     */
    Before,

    /**
     * 링크 생성을 서버나 내부 로직에 요청하여 현재 처리 중인 상태.
     * - **UI 동작**: 사용자에게 스켈레톤으로 처리 중임을 표시하고 하단 버튼 비활성화.
     */
    Loading,

    /**
     * 공유 링크 생성이 성공적으로 완료된 상태.
     * - **UI 동작**: 공유 링크를 보이고, 생성된 링크를 클립보드에 복사하는 버튼과
     * 시스템 공유 창(Share Sheet)을 띄우는 버튼 활성화.
     */
    Done,

    /**
     * 공유 링크 생성 중 오류가 발생한 상태.
     * - **UI 동작**: 미정
     */
    Error
}




@Composable
internal fun _ShareBottomSheet(
    modifier: Modifier,
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel
){
    fileViewModel.getFolderTree()

    if(folderStateViewModel.shareBottomSheetVisible){
        ShareBottomSheetLayout(
            modifier = modifier,
            folderTree = fileViewModel.folderTree.collectAsState().value,
            onDismissRequest = {
                folderStateViewModel.updateShareBottomSheetVisible(false)
            },
            onLinkGenerate = fileViewModel::makeInvitationLink
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShareBottomSheetLayout(
    modifier: Modifier = Modifier,
    folderTree: List<FolderSimpleInfo>,
    onDismissRequest: () -> Unit,
    onLinkGenerate: (Long) -> String?
){
    // 메인 & 선택 화면 공통 인스턴스
    /** 색상 공통 인스턴스*/
    val colors = MaterialTheme.linkuColors

    /** 선택된 폴더의 깊이 ([None], [Category], [Folder])*/
    var selectDepth: SelectDepth by remember { mutableStateOf(None) }

    /** 바텀 시트의 화면 상태 ([Main], [Select])*/
    var screenState: ScreenState by remember { mutableStateOf(Main) }

    // 바텀 시트 오픈마다 한 번 초기화
    LaunchedEffect(Unit) {
        selectDepth = None
        screenState = Main
    }

    /** 바텀 시트 상태 인스턴스*/
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true    // 항상 활짝 펴진 상태로 설정
    )

    /**
     * **내부 컨텐츠 구조 (content)**
     *
     * `when(screenState)` 조건문에 따라 두 가지 화면 중 하나를 렌더링:
     * - [Main]: 메인 공유 설정 화면 (폴더 미리보기, 링크 생성 등).
     * -Select]: 공유할 폴더를 선택하기 위한 탐색 화면.
     */
    ModalBottomSheet(
        modifier = modifier
            .fillMaxWidth(),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                width = 40.dp,
                color = colors.gray[300]
            )
        },
        containerColor = colors.white,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        // ScreenState에 따른 컨텐츠
        when (screenState) {

            // 폴더 공유 바텀 시트의 메인 화면
            Main -> ShareBottomSheetMainScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(SHEET_MAIN_SCREEN_HEIGHT_RATIO),
                colors = colors,
                selectDepth = selectDepth,
                onMenuClick = { screenState = Select },
                onLinkGenerate = onLinkGenerate
            )

            // 공유할 폴더 선택 화면
           Select -> SelectFolderToShareScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(SHEET_SELECT_SCREEN_HEIGHT_RATIO),
                colors = colors,
                preSelectDepth = selectDepth,
                categories = folderTree,
            )
        }
    }
}




/*----------------폴더 공유 바텀 시트 메인 스크린----------------*/
/**
 * 폴더 공유 바텀 시트의 메인 화면을 구성하는 Composable 함수입니다.
 *
 * 이 화면은 선택된 폴더의 정보를 시각적으로 보여주는 폴더 모형, 폴더 선택을 위한 메뉴 바,
 * 그리고 선택된 폴더의 공유 링크 생성 및 관리(복사, 공유)를 위한 UI 요소들을 포함합니다.
 *
 * @param modifier 이 컴포저블의 레이아웃, 크기, 클릭 이벤트 등을 정의하기 위한 [Modifier].
 * @param colors 앱의 디자인 시스템에 정의된 테마 색상 구성 객체 ([ThemeColorScheme]).
 * @param selectDepth 현재 사용자가 선택한 폴더의 계층 구조 정보를 담고 있는 상태 ([SelectDepth]).
 * @param onMenuClick 폴더 선택 메뉴(경로 표시 바)를 클릭했을 때 호출되는 콜백. 주로 [Select] 화면으로 전환하는 로직이 들어갑니다.
 * @param onLinkGenerate 특정 폴더 ID를 기반으로 공유 링크 생성을 요청하는 람다 함수.
 *                       성공 시 생성된 링크 URL을, 실패 시 null을 반환해야 합니다.
 *
 * @see SelectDepth
 * @see LinkGenerateState
 * @see ShareFolderMenu
 * @see ShareLink
 * @see LinkGenerationButton
 */
@Composable
private fun ShareBottomSheetMainScreen(
    modifier: Modifier,
    colors: ThemeColorScheme,
    selectDepth: SelectDepth,
    onMenuClick: () -> Unit,
    onLinkGenerate: (Long) -> String?
){
    /** 링크 생성 프로세스의 현재 진행 상태 ([Before], [Loading], [Done], [Error])*/
    var linkGenerateState: LinkGenerateState by remember { mutableStateOf(Before) }

    /** 공유 링크 생성 상태에 따른 문자열
     *
     * - [Before]: "링크 생성 전"
     * - [Loading]: "링크 생성 중..."
     * - [Done]: "링크 생성 완료"
     * - [Error]: "링크 생성 실패"
     */
    var link: String by remember { mutableStateOf("링크 생성 전") }

    /**
     * 선택된 폴더의 경로와 상태에 따라 메뉴 바에 표시될 텍스트.
     * [AnnotatedString]을 사용하여 상위 카테고리는 회색, 현재 폴더는 기본색으로 스타일을 다르게 적용합니다.
     *
     * - [None]: 선택된 폴더가 없을 때 안내 문구 표시.
     * - [Category]: 상위 카테고리만 선택되었을 때 해당 카테고리 이름 표시.
     * - [Folder]: 카테고리와 하위 폴더가 모두 선택되었을 때 '카테고리 > 폴더' 형식으로 표시.
     */
    var menuText: AnnotatedString by remember(selectDepth){ mutableStateOf(
        buildAnnotatedString {
            when (selectDepth) {
                is None -> {
                    withStyle(SpanStyle(colors.gray[400])) {
                        append("공유하실 폴더를 선택해주세요.")
                    }
                }
                is Category -> {
                    append(selectDepth.category.folderName)
                }
                is Folder -> {
                    withStyle(SpanStyle(colors.gray[400])){
                        append(selectDepth.category.folderName + " > ")
                    }
                    append(selectDepth.folder.folderName)
                }
            }
        }
    )}

    /** 클립보드에 복사를 위한 객체*/
    val clipboardManager = LocalClipboard.current

    /** 바텀 시트 내 컨텍스트*/
    val context = LocalContext.current

    /**
     * 생성된 공유 링크를 시스템 클립보드에 복사하는 람다 함수.
     * 복사 성공 시 사용자에게 알림 메시지(Toast)를 표시합니다.
     */
    val linkCopyToClipboard = {
        clipboardManager.nativeClipboard.setPrimaryClip(
            ClipData.newPlainText("label", link)
        )
        Toast.makeText(context, "링크가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
    }

    /**
     * 안드로이드 시스템 공유 시트(Share Sheet)를 열어 링크를 다른 앱으로 전달하는 람다 함수.
     * [Intent.ACTION_SEND]를 사용하여 텍스트 형식의 링크를 공유합니다.
     */
    val linkShareToShareSheet = {
        try{
            Log.d("ShareBottomSheet", "linkShareToShareSheet")
            
            val sendIntent = Intent().apply {
                Log.d("ShareBottomSheet", "linkShareToShareSheet intent")

                action = Intent.ACTION_SEND // 공유 액션 설정
                putExtra(Intent.EXTRA_TEXT, link)
                type = "text/plain"
            }
            
            Log.d("ShareBottomSheet", "linkShareToShareSheet intent done")

            val shareIntent = Intent.createChooser(sendIntent, "링크 공유하기")
            
            Log.d("ShareBottomSheet", "linkShareToShareSheet shareIntent")
            
            context.startActivity(shareIntent)
            
            Log.d("ShareBottomSheet", "linkShareToShareSheet context.startActivity")
        } catch (e: Exception){
            Log.e("ShareBottomSheet", "linkShareToShareSheet error", e)

            Toast.makeText(context, "링크 공유에 실패하였습니다. 다시 시도해주십시오.", Toast.LENGTH_SHORT).show()
        }

        Unit
    }

    // 폴더 공유 바텀 시트 메인 스크린
    /**
     * [ShareBottomSheetMainScreen]의 컴포넌트 배치 구조:
     *
     * Column (Main Container)
     * ├── Text ("폴더 공유하기" 타이틀)
     * ├── FolderStructure (폴더 시각적 모형)
     * ├── Text ("폴더 이름" 라벨)
     * ├── ShareFolderMenu (폴더 선택 경로 바)
     * └── if (selectDepth is Folder) {  // 폴더가 최종 선택된 경우에만 표시
     *      ├── ShareLink (생성된 링크 표시 바 - Loading/Done/Error 상태 시)
     *      └── Link Management UI (상태에 따라 [LinkGenerationButton] 또는 [Row(Copy/Share Buttons)] 표시)
     *     }
     */
    Column(
        modifier = modifier.padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        // "폴더 공유하기" 타이틀
        Text(
            text = "폴더 공유하기",
            fontSize = 16.sp,
            fontWeight = FontWeight(500)
        )

        Spacer(modifier = Modifier.fillMaxHeight(FOLDER_STRUCTURE_TOP_MARGIN_RATIO))

        // 폴더 시각적 모형
        FolderStructure(
            modifier = Modifier
                .fillMaxWidth(FOLDER_STRUCTURE_WIDTH_RATIO),
            colors = colors
        )

        Spacer(modifier = Modifier.fillMaxHeight(FOLDER_STRUCTURE_BOTTOM_MARGIN_RATIO))

        // "폴더 이름" 라벨
        Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .align(Alignment.Start),
            text = "폴더 이름",
            fontSize = 15.sp,
            fontWeight = FontWeight(500)
        )

        Spacer(modifier = Modifier.fillMaxHeight(SHARE_FOLDER_MENU_TOP_MARGIN_RATIO))

        // 폴더 선택 경로 바
        ShareFolderMenu(
            modifier = Modifier
                .fillMaxWidth(/*SHARE_FOLDER_MENU_WIDTH_RATIO*/)
                .fillMaxHeight(SHARE_FOLDER_MENU_HEIGHT_RATIO),
            colors = colors,
            menuText = menuText,
            selectable = true,
            onClick = onMenuClick
        )

        // 폴더가 최종 선택된 경우에만 표시
        if(selectDepth is Folder) {

            Spacer(modifier = Modifier.fillMaxHeight(SHARE_FOLDER_MENU_BOTTOM_MARGIN_RATIO))

            // 생성된 링크 표시 바 - Loading/Done/Error 상태 시
            if(linkGenerateState != Before) {
                ShareLink(
                    modifier = Modifier
                        .fillMaxWidth(SHARE_LINK_WIDTH_RATIO)
                        .fillMaxHeight(SHARE_LINK_HEIGHT_RATIO),
                    colors = colors,
                    link = link,
                    isLoading = linkGenerateState == Loading
                )
            }

            // 상태에 따라 [LinkGenerationButton] 또는 [Row(Copy/Share Buttons)] 표시
            when(linkGenerateState) {
                Before -> // [LinkGenerationButton] 표시
                    // 링크 생성 버튼
                    LinkGenerationButton(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(BUTTON_WIDTH_RATIO)
                            .fillMaxHeight(BUTTON_HEIGHT_RATIO),
                        colors = colors
                    ){  // 링크 생성 버튼 클릭 콜백

                        // 링크 생성 시작 시 생성 상태 = Loading
                        linkGenerateState = Loading

                        // onLinkGenerate 실행 후 링크 대입. null일 경우, 실패 문장 대입.
                        link = onLinkGenerate(selectDepth.folder.folderId)?:"링크 생성을 실패했습니다. 다시 시도해주십시오.".also {

                            // 또한 null일 경우, 에러 상태로 전환
                            linkGenerateState = Error
                            return@LinkGenerationButton
                        }

                        // 링크 대입 성공 시 생성 상태 = Done
                        linkGenerateState = Done
                    }

                Loading -> link = "링크 생성 중..."

                Done -> // [Row(Copy/Share Buttons)] 표시
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(BUTTON_WIDTH_RATIO)
                            .fillMaxHeight(BUTTON_HEIGHT_RATIO),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        // 링크 복사 버튼
                        LinkCopyButton(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(SMALL_BUTTON_WIDTH_RATIO),
                            colors = colors,
                            onCopy = linkCopyToClipboard
                        )

                        // 링크 공유 버튼
                        LinkShareButton(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(SMALL_BUTTON_WIDTH_RATIO),
                            colors = colors,
                            onShare = linkShareToShareSheet
                        )
                    }

                Error -> link = "링크 생성을 실패했습니다. 다시 시도해주십시오."
            }
        }
    }
}

/**
 * 공유할 폴더의 정보를 시각적으로 나타내는 폴더 모형 UI를 구성하는 Composable 함수입니다.
 *
 * 배경 이미지, 그림자가 적용된 마스크 레이어, 그리고 폴더 내부에 위치한 원형 및 직사각형 형태의
 * 플레이스홀더 요소를 중첩하여 입체적인 폴더 디자인을 구현합니다.
 *
 * @param modifier 이 컴포저블의 레이아웃, 크기 등을 정의하기 위한 [Modifier].
 * @param colors 앱의 디자인 시스템에 정의된 테마 색상 구성 객체 ([ThemeColorScheme]).
 *
 * @see ShareBottomSheetMainScreen
 */
@Composable
private fun FolderStructure(
    modifier: Modifier,
    colors: ThemeColorScheme,
){
    // 폴더 모형 레이아웃
    Box(
        modifier = modifier
    ){
        Image(
            contentDescription = "폴더 배경 레이어",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.img_shared_bottom_sheet_folder_background),
        )

        // TODO: 그림자 해결
        //val bitmap = ImageBitmap.imageResource(id = R.drawable.img_shared_bottom_sheet_folder_mask)
        OuterShadowResourceImage(
            resId = R.drawable.img_shared_bottom_sheet_folder_mask,
            contentDescription = "폴더 앞 부분 레이어",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )

        /*PngImageWithShadow(
            contentDescription = "폴더 앞 부분 레이어",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            painter = painterResource(id = R.drawable.img_shared_bottom_sheet_folder_mask)
        )*/

        // 빈 폴더 이름 칸
        Row(
            modifier = Modifier
                .padding(start = 18.42.dp, bottom = 18.93.dp)
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.87.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(CIRCLE_SIZE_IN_FOLDER.dp)
                    .background(
                        shape = CircleShape,
                        color = colors.gray[400]
                    )
            )

            Box(
                modifier = Modifier
                    .width(58.dp)
                    .height(21.dp)
                    .background(
                        shape = RoundedCornerShape(size = 8.dp),
                        color = colors.gray[300]
                    )
            )
        }
    }
}

/**
 * 폴더 경로를 표시하고 폴더 선택 화면으로 이동할 수 있는 메뉴 바 컴포저블입니다.
 *
 * 현재 사용자가 선택한 폴더의 계층 구조(카테고리 > 폴더)를 텍스트로 보여주며,
 * 클릭 시 공유할 폴더를 탐색하고 선택할 수 있는 화면으로 전환하는 트리거 역할을 합니다.
 *
 * @param modifier 이 컴포저블의 레이아웃, 크기, 클릭 이벤트 등을 정의하기 위한 [Modifier].
 * @param colors 앱의 디자인 시스템에 정의된 테마 색상 구성 객체 ([ThemeColorScheme]).
 * @param menuText 현재 선택된 폴더 경로를 나타내는 서식 있는 문자열 ([AnnotatedString]).
 * @param selectable 메뉴의 선택 가능 여부.
 * @param onClick 메뉴 바를 클릭했을 때 실행될 콜백 함수.
 *
 * @see ShareBottomSheetMainScreen
 */
@Composable
private fun ShareFolderMenu(
    modifier: Modifier,
    colors: ThemeColorScheme,
    menuText: AnnotatedString,
    selectable: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = colors.gray[200],
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 22.dp)
            .noRippleClickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = menuText,
            fontSize = 14.sp,
            fontWeight = FontWeight(400)
        )

        Image(
            contentDescription = "메뉴 오른쪽 화살표 이미지",
            painter = painterResource(R.drawable.img_right_arrow),
            modifier = Modifier
                .fillMaxWidth(SHARE_FOLDER_MENU_ARROW_WIDTH_RATIO),
            colorFilter = ColorFilter.tint(colors.gray[600])
        )
    }
}

/**
 * 생성된 공유 링크를 사용자에게 시각적으로 표시하는 UI 컴포넌트입니다.
 *
 * 링크 생성 상태([LinkGenerateState])가 초기 상태([LinkGenerateState.Before])가 아닐 때 나타나며,
 * 배경색이 있는 둥근 테두리 박스 안에 링크 텍스트를 표시합니다.
 *
 * @param modifier 이 컴포저블의 크기 및 레이아웃을 설정하기 위한 [Modifier].
 * @param colors 앱의 디자인 시스템에 정의된 테마 색상 구성 객체 ([ThemeColorScheme]).
 * @param link 화면에 표시할 공유 링크 문자열 또는 상태 메시지.
 * @param isLoading 현재 링크를 생성 중인지 여부. true일 경우 텍스트 영역에 스켈레톤 애니메이션이 적용됩니다.
 *
 * @see ShareBottomSheetMainScreen
 * @see LinkGenerateState
 * @see LinkGenerationButton
 */
@Composable
private fun ShareLink(
    modifier: Modifier,
    colors: ThemeColorScheme,
    link: String,
    isLoading: Boolean
){
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = colors.gray[200],
                shape = RoundedCornerShape(18.dp)
            )
            .background(colors.gray[100]),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .clip(RoundedCornerShape(18.dp))
                .skeleton(isLoading = isLoading),
            text = link,
            fontSize = 15.sp,
            fontWeight = FontWeight(400),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

/**
 * 공유 링크 생성을 실행하기 위한 버튼 컴포저블입니다.
 *
 * 사용자가 선택한 폴더에 대한 공유 링크 생성을 요청할 때 사용되며,
 * 클릭 시 전달받은 [onLinkGenerate] 콜백을 실행합니다.
 *
 * @param modifier 버튼의 레이아웃, 크기 등을 설정하기 위한 [Modifier].
 * @param colors 앱의 디자인 시스템 테마 색상을 적용하기 위한 [ThemeColorScheme].
 * @param onLinkGenerate 버튼이 클릭되었을 때 실행될 링크 생성 로직 콜백.
 *
 * @see ShareBottomSheetMainScreen
 */
@Composable
private fun LinkGenerationButton(
    modifier: Modifier,
    colors: ThemeColorScheme,
    onLinkGenerate: () -> Unit
) {
    Text(
        modifier = modifier
            .background(
                brush = colors.maincolor,
                shape = RoundedCornerShape(size = 18.dp)
            )
            .noRippleClickable {
                onLinkGenerate()
            },
        text = "공유 링크 생성",
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        color = colors.white
    )
}

/**
 * 생성된 공유 링크를 클립보드에 복사하기 위한 버튼 컴포저블입니다.
 *
 * 이 버튼은 테두리가 있는 디자인으로 구성되며, 복사 아이콘과 '링크 복사' 텍스트를 포함합니다.
 * 사용자가 클릭 시 [onCopy] 콜백을 실행하여 외부 로직(클립보드 저장 등)을 처리합니다.
 *
 * @param modifier 이 컴포저블의 레이아웃 크기 및 위치 조정을 위한 [Modifier].
 * @param colors 앱의 테마 색상 구성 객체 ([ThemeColorScheme]).
 * @param onCopy 버튼 클릭 시 호출되는 콜백 함수.
 *
 * @see ShareBottomSheetMainScreen
 */
@Composable
private fun LinkCopyButton(
    modifier: Modifier,
    colors: ThemeColorScheme,
    onCopy: () -> Unit
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = colors.gray[300],
                shape = RoundedCornerShape(size = 18.dp)
            )
            .noRippleClickable(onClick = onCopy),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            contentDescription = "링크 복사 이미지",
            modifier = Modifier
                .fillMaxWidth(COPY_ICON_WIDTH_RATIO),
            painter = painterResource(R.drawable.icon_copy),
            colorFilter = ColorFilter.tint(colors.gray[800])
        )

        Text(
            text = "링크 복사",
            fontSize = 16.sp,
            color = colors.gray[800]
        )
    }
}

/**
 * 생성된 공유 링크를 외부 앱으로 전달하기 위한 공유 버튼 컴포저블입니다.
 *
 * 버튼 클릭 시 시스템 공유 시트(Share Sheet)를 호출하는 기능을 수행하며,
 * 디자인 시스템의 메인 컬러 그라데이션 배경과 공유 아이콘을 포함합니다.
 *
 * @param modifier 이 컴포저블의 레이아웃, 크기 등을 정의하기 위한 [Modifier].
 * @param colors 앱의 디자인 시스템에 정의된 테마 색상 구성 객체 ([ThemeColorScheme]).
 * @param onShare 버튼을 클릭했을 때 호출되는 콜백 함수. 시스템 공유 액션을 수행하는 로직이 포함되어야 합니다.
 *
 * @see ShareBottomSheetMainScreen
 */
@Composable
private fun LinkShareButton(
    modifier: Modifier,
    colors: ThemeColorScheme,
    onShare: () -> Unit
) {
    Row(
        modifier = modifier
            .background(
                brush = colors.maincolor,
                shape = RoundedCornerShape(size = 18.dp)
            )
            .noRippleClickable(onClick = onShare),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            contentDescription = "링크 공유 이미지",
            modifier = Modifier
                .fillMaxWidth(SHARE_ICON_WIDTH_RATIO),
            painter = painterResource(R.drawable.icon_copy),
            colorFilter = ColorFilter.tint(colors.white)
        )

        Text(
            text = "링크 공유",
            fontSize = 16.sp,
            color = colors.white
        )
    }
}
/*----------------폴더 공유 바텀 시트 메인 스크린----------------*/




/*----------------공유 폴더 선택 스크린----------------*/
// 공유할 폴더 선택 스크린
@Composable
private fun SelectFolderToShareScreen(
    modifier: Modifier,
    colors: ThemeColorScheme,
    preSelectDepth: SelectDepth,
    categories: List<FolderSimpleInfo>
){
    var _newSelectDepth: SelectDepth by remember { mutableStateOf(preSelectDepth) }

    Column(
        modifier = modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ){
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            /** newSelectDepth의 스냅샷 */
            val newSelectDepth = _newSelectDepth

            // 카테고리 리스트
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(categories) { category ->

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .padding(start = 15.dp)
                            .then(
                                if (newSelectDepth is Category && newSelectDepth.category == category)
                                    Modifier
                                        .background(
                                            color = colors.gray[100],
                                            shape = RoundedCornerShape(size = 16.dp)
                                        )
                                        .noRippleClickable { _newSelectDepth = None }
                                else Modifier.noRippleClickable {
                                    _newSelectDepth = Category(category)
                                }
                            ),
                        text = category.folderName,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        color = if (newSelectDepth is Category && newSelectDepth.category == category) colors.gray[800] else colors.gray[600]
                    )
                }
            }

            if (newSelectDepth is Category) {

                val folders = newSelectDepth.category.children

                // 폴더 리스트
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    items(folders) { folder ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .padding(start = 15.dp)
                                .then(
                                    if (newSelectDepth is Folder && newSelectDepth.folder == folders)
                                        Modifier
                                            .background(
                                                color = colors.gray[100],
                                                shape = RoundedCornerShape(size = 16.dp)
                                            )
                                            .noRippleClickable { _newSelectDepth = None }
                                    else Modifier.noRippleClickable {
                                        _newSelectDepth = Folder(newSelectDepth.category, folder)
                                    }
                                ),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_shared_bottom_sheet_folder_select),
                                contentDescription = "폴더 아이콘",
                                tint = if (newSelectDepth is Folder && newSelectDepth.folder == folder) colors.blue[200] else colors.gray[400]
                            )

                            Text(
                                text = folder.folderName,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                color = if (newSelectDepth is Folder && newSelectDepth.folder == folder) colors.gray[800] else colors.gray[600]
                            )
                        }
                    }
                }
            }
        }
    }
}
/*----------------공유 폴더 선택 스크린----------------*/




@Preview(showBackground = true)
@Composable
private fun ShareBottomSheetMainScreenPreview() {
    val sampleCategory = FolderSimpleInfo(
        folderId = 1,
        folderName = "카테고리",
        parentFolderId = 0,
        isBookmarked = false
    )
    val sampleFolder = FolderSimpleInfo(
        folderId = 2,
        folderName = "공유할 폴더",
        parentFolderId = 1,
        isBookmarked = false
    )
    ThemeProvider {
        ShareBottomSheetMainScreen(
            modifier = Modifier.fillMaxWidth(),
            colors = MaterialTheme.linkuColors,
            selectDepth = Folder(sampleCategory, sampleFolder),
            onMenuClick = {},
            onLinkGenerate = { "https://linku.com/share/test" }
        )
    }
}
