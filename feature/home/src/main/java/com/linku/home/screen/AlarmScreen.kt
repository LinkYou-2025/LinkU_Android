package com.linku.home.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.design.theme.LocalColorTheme
import com.linku.home.ui.alarm.component.AlarmTopBar
import com.linku.home.ui.alarm.component.AlarmFilterTabs
import com.linku.home.ui.alarm.component.AlarmItem
import com.linku.home.ui.alarm.component.AlarmNothingTab
import com.linku.home.ui.alarm.component.AlarmSettingTab
import com.linku.home.ui.alarm.component.AlarmAppendStateFooter
import com.linku.home.ui.alarm.component.AlarmErrorLayout
import com.linku.home.ui.alarm.component.AlarmLoadingContent
import com.linku.home.viewmodel.AlarmViewModel
import com.linku.design.theme.LinkuPreview
import com.linku.home.model.AlarmIntent
import com.linku.home.model.AlarmSideEffect
import kotlinx.coroutines.flow.flowOf

@Composable
fun AlarmScreen(
    onBack: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLinkDetail: (targetId: Long) -> Unit,
    onNavigateToFolder: (targetId: Long) -> Unit,
    onNavigateToCuration: (targetId: Long) -> Unit,
    onNavigateToNotice: (targetId: Long) -> Unit,
    viewModel: AlarmViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val pushAlarmEnabled by viewModel.pushAlarmEnabled.collectAsStateWithLifecycle()
    val readAlarmIds by viewModel.readAlarmIds.collectAsStateWithLifecycle()

    //탭 선택 상태
    var selectedTab by rememberSaveable { mutableStateOf(AlarmType.ALL) }

    // 사용자가 직접 스와이프 새로고침을 트리거했을 때 활성화되는 state
    var isUserRefreshing by remember { mutableStateOf(false) }

    // 컨테이너와 표시기가 당겨진 거리를 추적하는 state. PullToRefreshBox를 쓰려면 필수
    val pullToRefreshState = rememberPullToRefreshState()

    // 화면에 표시될 페이징될 알람들 데이터
    // Paging3에서 LazyPagingItems는
    // PagingData와 LoadState(로딩/에러 상태)를 함께 관리한다
    val alarmPagingItems = viewModel.getAlarms(selectedTab)
        .collectAsLazyPagingItems()

    // 새로고침이 완료되면 인디케이터 해제
    LaunchedEffect(alarmPagingItems.loadState.refresh) {
        if (alarmPagingItems.loadState.refresh is LoadState.NotLoading ||
            alarmPagingItems.loadState.refresh is LoadState.Error) {
            isUserRefreshing = false
        }
    }

    // 사이드이펙트 채널 구독
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is AlarmSideEffect.NavigateToLinkDetail -> onNavigateToLinkDetail(effect.targetId)
                is AlarmSideEffect.NavigateToSharedFolder -> onNavigateToFolder(effect.targetId)
                is AlarmSideEffect.NavigateToCuration -> onNavigateToCuration(effect.targetId)
                is AlarmSideEffect.NavigateToNotice -> onNavigateToNotice(effect.targetId) // 또는 alarmId — 아래 참고
                is AlarmSideEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    AlarmScreenContent(
        isAlarmAllowed = pushAlarmEnabled,
        selectedTab = selectedTab,
        onSelectedChange = { selectedTab = it },
        alarmPagingItems = alarmPagingItems,
        readAlarmIds = readAlarmIds,
        isUserRefreshing = isUserRefreshing,
        onRefresh = {
            isUserRefreshing = true
            viewModel.handleIntent(AlarmIntent.Refresh)
            alarmPagingItems.refresh()
        },
        pullToRefreshState = pullToRefreshState,
        onBack = onBack,
        onNavigateToMyPage = onNavigateToSetting,
        onNavigateToHome = onNavigateToHome,
        onIntent = viewModel::handleIntent
    )
}

@Composable
private fun AlarmScreenContent(
    isAlarmAllowed: Boolean = true,
    selectedTab: AlarmType,
    onSelectedChange: (AlarmType) -> Unit,
    alarmPagingItems: LazyPagingItems<AlarmSummary>,
    readAlarmIds: Set<Long>,
    isUserRefreshing: Boolean,
    onRefresh: () -> Unit,
    pullToRefreshState: PullToRefreshState,
    onBack: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    onNavigateToHome: () -> Unit,
    onIntent: (AlarmIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.gray[100])
            .padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlarmTopBar(
            onBack = onBack,
            onHomeClick = onNavigateToHome,
            topText = "알림함"
        )

        Spacer(modifier = Modifier.height(30.dp))

        AlarmFilterTabs(
            selected = selectedTab,
            onSelectedChange = onSelectedChange
        )

        Spacer(modifier = Modifier.height(15.dp))

        AlarmSettingTab(
            isVisible = !isAlarmAllowed,
            onClick = onNavigateToMyPage
        )

        if (isAlarmAllowed) {
            PullToRefreshBox( //스와이프 새로고침 기능을 내장하는 컴포저블
                modifier = Modifier.weight(1f),
                isRefreshing = isUserRefreshing,
                onRefresh = onRefresh,
                state = pullToRefreshState,
                indicator = {
                    Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isRefreshing = isUserRefreshing,
                        containerColor = LocalColorTheme.current.gray[200],
                        color = LocalColorTheme.current.black,
                        state = pullToRefreshState
                    )
                }
            ) {
                /**
                 * 허용이 되어있는 경우. Paging3의 loadState를 사용하여 분기처리
                 * 디벨로퍼 문서의 코드 스니펫을 참고해서 구현하였음
                 *
                 * - loadState.refresh: 목록 전체를 새로 불러올 때의 상태
                 * - loadState.append: 스크롤로 다음 페이지를 불러올 때의 상태. [AlarmAppendStateFooter]에서 사용
                 */
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (val refreshState = alarmPagingItems.loadState.refresh) {

                        is LoadState.Loading -> {
                            item { AlarmLoadingContent() }
                        }

                        is LoadState.Error -> {
                            item {
                                AlarmErrorLayout(
                                    alarmPagingItems = alarmPagingItems,
                                    errorState = refreshState,
                                    modifier = Modifier.fillParentMaxSize()
                                )
                            }
                        }

                        else -> {
                            if (alarmPagingItems.itemCount == 0) {
                                item {
                                    AlarmNothingTab(isVisible = true)
                                }
                            } else {
                                items(
                                    count = alarmPagingItems.itemCount,
                                    key = alarmPagingItems.itemKey { it.id }
                                ) { index ->
                                    alarmPagingItems[index]?.let { alarm ->
                                        AlarmItem(
                                            alarm = alarm,
                                            isRead = alarm.isRead || alarm.id in readAlarmIds,
                                            onClick = { onIntent(AlarmIntent.ClickAlarm(alarm)) }
                                        )
                                    }
                                }

                                item {
                                    AlarmAppendStateFooter(alarmPagingItems)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmScreenContentPreview() {
    val sampleAlarms = listOf(
        AlarmSummary(
            id = 1L,
            alarmType = AlarmType.LINK,
            whenSubmitted = "2023-10-27 10:00:00",
            message = "새로운 링크가 추가되었습니다.",
            targetId = 101L,
            isRead = false
        ),
        AlarmSummary(
            id = 2L,
            alarmType = AlarmType.FOLDER,
            whenSubmitted = "2023-10-27 11:00:00",
            message = "폴더에 초대되었습니다.",
            targetId = 102L,
            isRead = true
        ),
        AlarmSummary(
            id = 3L,
            alarmType = AlarmType.CURATION,
            whenSubmitted = "2023-10-27 12:00:00",
            message = "큐레이션이 업데이트되었습니다.",
            targetId = 103L,
            isRead = false
        )
    )
    val alarmPagingItems = flowOf(PagingData.from(sampleAlarms)).collectAsLazyPagingItems()

    LinkuPreview {
        AlarmScreenContent(
            isAlarmAllowed = false,
            selectedTab = AlarmType.ALL,
            onSelectedChange = {},
            alarmPagingItems = alarmPagingItems,
            readAlarmIds = emptySet(),
            isUserRefreshing = false,
            onRefresh = {},
            pullToRefreshState = rememberPullToRefreshState(),
            onBack = {},
            onNavigateToMyPage = {},
            onNavigateToHome = {},
            onIntent = {}
        )
    }
}



