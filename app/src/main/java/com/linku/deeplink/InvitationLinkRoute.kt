package com.linku.deeplink

import android.util.Log
import androidx.navigation.NavHostController
import com.linku.NavigationRoute
import com.linku.core.util.caller.getCaller
import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.d
import com.linku.core.util.logging.e

internal fun invitationLinkRoute(
    token: String,
    isLoggedIn: Boolean,
    onReceiveSharedFolderInvitation: (String) -> Unit,
    onUpdateIsSharedFolders: (Boolean) -> Unit,
    onSetPendingInvitation: (String) -> Unit,
    onInvalidLink: () -> Unit = {},
    navigator: NavHostController
) {
    val caller = getCaller()

    LinkuLog.d(caller) {"token: $token"}
    LinkuLog.d(caller) {"isLoggedIn: $isLoggedIn"}

    try{
        LinkuLog.d(caller) {"Enter try"}

        if (isLoggedIn) {
            LinkuLog.d(caller) {"Enter if isLoggedIn"}

            LinkuLog.d(caller) {"start onReceiveSharedFolderInvitation"}
            onReceiveSharedFolderInvitation(token)
            LinkuLog.d(caller) {"onReceiveSharedFolderInvitation done"}

            LinkuLog.d(caller) {"start onUpdateIsSharedFolders"}
            onUpdateIsSharedFolders(true)
            LinkuLog.d(caller) {"onUpdateIsSharedFolders done"}


            LinkuLog.d(caller) {"start NavigationRoute.File"}
            navigator.navigate(NavigationRoute.File.route) {
                popUpTo(NavigationRoute.Splash.route) { inclusive = false }
                launchSingleTop = true
            }
            LinkuLog.d(caller) {"NavigationRoute.File done"}

            LinkuLog.d(caller) {"Escape if isLoggedIn"}
        } else {
            LinkuLog.d(caller) {"Enter else isLoggedIn"}


            LinkuLog.d(caller) {"start onSetPendingInvitation"}
            onSetPendingInvitation(token)
            LinkuLog.d(caller) {"onSetPendingInvitation done"}

            LinkuLog.d(caller) {"start NavigationRoute.Login"}
            navigator.navigate("${NavigationRoute.Login.route}?showModal=true") {
                popUpTo(NavigationRoute.Splash.route) { inclusive = false }
                launchSingleTop = true
            }
            LinkuLog.d(caller) {"NavigationRoute.Login done"}

            LinkuLog.d(caller) {"Escape else isLoggedIn"}
        }
    } catch (e: Exception){
        LinkuLog.e(caller, e) {"Enter catch"}

        LinkuLog.d(caller) {"start onInvalidLink"}
        onInvalidLink()
        LinkuLog.d(caller) {"onInvalidLink done"}


        LinkuLog.d(caller) {"start navigator.popBackStack"}
        navigator.popBackStack()
        LinkuLog.d(caller) {"navigator.popBackStack done"}

        LinkuLog.d(caller, e) {"Enter catch"}
    } finally {
        LinkuLog.d(caller) {"return"}
    }
}
