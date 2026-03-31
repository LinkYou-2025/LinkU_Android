package com.linku.curation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.linku.curation.ui.CurationScreen
import com.linku.curation.ui.screen.detail.CurationMonthDetailScreen

/**
 * 큐레이션 기능의 내비게이션 그래프 정의
 * 이건 아예 전면 변경이라 일단 MainAPP에서 분리만 시켰습니다.
 */
@OptIn (ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.curationGraph(
    navigator: NavHostController,
    showNavBar: (Boolean) -> Unit
) {
    navigation(
        startDestination = "curation_list",
        route = "curation"
    ) {
        composable("curation_list") { backStackEntry ->
            showNavBar(true)

            val parentEntry = remember(backStackEntry) {
                navigator.getBackStackEntry("curation")
            }
            val curationVm: CurationViewModel = hiltViewModel(parentEntry)

            CurationScreen(viewModel = curationVm)
        }

        composable(
            route = "curation_month_detail/{curationId}?imageUrl={imageUrl}&cardIndex={cardIndex}",
//            enterTransition = { EnterTransition.None },
//            exitTransition = { ExitTransition.None }
        ) { backStack ->
            showNavBar(false)

            val curationId = backStack.arguments?.getString("curationId")?.toLong() ?: 0L
            val imageUrl = backStack.arguments?.getString("imageUrl")
            val cardIndex = backStack.arguments?.getString("cardIndex")?.toInt() ?: 0

            CurationMonthDetailScreen(
                curationId = curationId,
                imageUrl = imageUrl,
                onBack = { navigator.popBackStack() }
            )
        }
    }
}
//fun NavGraphBuilder.curationGraph(
//    navigator: NavHostController,
//    showNavBar: (Boolean) -> Unit
//) {
//    navigation(
//        startDestination = "curation_list",
//        route = "curation"
//    ) {
//        // 1. 리스트 화면
//        composable("curation_list") { backStackEntry ->
//            // 리스트 화면에서는 바텀바 표시
//            showNavBar(true)
//
//            // 부모 그래프(curation_graph)의 스코프를 가져옴
//            val parentEntry = remember(backStackEntry) {
//                navigator.getBackStackEntry("curation")
//            }
//            val curationVm: CurationViewModel = hiltViewModel(parentEntry)
//
//            CurationScreen(
//                viewModel = curationVm,
//                onOpenDetail = { userId, curationId ->
//                    navigator.navigate("curation_detail/$userId/$curationId") {
//                        launchSingleTop = true
//                    }
//                }
//            )
//        }
//
//        // 2. 상세 화면
//        composable("curation_detail/{userId}/{curationId}") { backStack ->
//            // 상세 화면 진입 시 필요하다면 바텀바를 숨길 수도 있음 (현재는 유지 중)
//
//            val userId = backStack.arguments?.getString("userId")?.toLong() ?: 0L
//            val curationId = backStack.arguments?.getString("curationId")?.toLong() ?: 0L
//
//            val parentEntry = remember(backStack) {
//                navigator.getBackStackEntry("curation")
//            }
//
//            // 공유 ViewModel (부모 스코프)
//            val sharedVm: CurationViewModel = hiltViewModel(parentEntry)
//            // 상세 전용 ViewModel (현재 상세화면 스코프)
//            val detailVm: CurationDetailViewModel = hiltViewModel(backStack)
//
//            CurationDetailScreen(
//                userId = userId,
//                curationId = curationId,
//                detailViewModel = detailVm,
//                homeViewModel = sharedVm,
//                onBack = { navigator.popBackStack() }
//            )
//        }
//    }
//}