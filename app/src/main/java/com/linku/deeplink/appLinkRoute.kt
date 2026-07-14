package com.linku.deeplink

import android.util.Log
import androidx.navigation.NavHostController
import com.linku.NavigationRoute

internal fun appLinkRoute(
    action: String?,
    folderId: Long?,
    token: String? = null,
    onReceiveSharedFolder: (Long) -> Unit,
    onReceiveSharedFolderInvitation: (String) -> Unit,
    onUpdateIsSharedFolders: (Boolean) -> Unit,
    onSetPendingShare: (Long) -> Unit,
    onSetPendingInvitation: (String) -> Unit,
    navigator: NavHostController
) {
    if (action == "share" && (folderId != null || token != null)) {
        Log.d("MainApp", "route: appLink file screen")

        try {
            if (token != null) {
                onReceiveSharedFolderInvitation(token)
            } else if (folderId != null) {
                onReceiveSharedFolder(folderId)
            }

            onUpdateIsSharedFolders(true)

            navigator.navigate(NavigationRoute.File.route) {
                popUpTo(NavigationRoute.Splash.route) { inclusive = false }
                launchSingleTop = true
            }
        } catch (e: Exception) {
            Log.e("MainApp", "appLinkRoute failed: $e")

            if (token != null) {
                onSetPendingInvitation(token)
            } else if (folderId != null) {
                onSetPendingShare(folderId)
            }

            navigator.navigate("${NavigationRoute.Login.route}?showModal=true") {
                popUpTo(NavigationRoute.Splash.route) { inclusive = false }
                launchSingleTop = true
            }
        }
    }
}
