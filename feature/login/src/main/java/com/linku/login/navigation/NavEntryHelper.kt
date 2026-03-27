package com.linku.login.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

/**
 * auth_graph 스코프의 NavBackStackEntry를 안전하게 remember.
 * 백스택에 없으면 null 반환.
 */
@Composable
fun rememberAuthParentEntry(
    navController: NavHostController,
    currentEntry: NavBackStackEntry
): NavBackStackEntry? = remember(currentEntry) {
    runCatching { navController.getBackStackEntry("auth_graph") }.getOrNull()
}

/**
 * social_auth_graph 스코프의 NavBackStackEntry를 remember.
 * 소셜 그래프 내 모든 composable에서 중복 remember 블록 제거용.
 */
@Composable
fun rememberSocialParentEntry(
    navController: NavHostController,
    entry: NavBackStackEntry
): NavBackStackEntry = remember(entry) {
    navController.getBackStackEntry("social_auth_graph")
}

/**
 * parentEntry가 null일 때 로그인 화면으로 안전하게 이동.
 * auth_graph 백스택이 비어있는 예외 상황 처리용.
 */
@Composable
fun NavigateToLoginOnError(navController: NavHostController) {
    LaunchedEffect(Unit) {
        navController.navigate("login") {
            popUpTo("auth_graph") { inclusive = true }
        }
    }
}
