package com.linku

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * 감싸진 [Context]를 따라 올라가 [Activity]를 찾습니다.
 *
 * @return 찾은 [Activity] 또는 없으면 `null`
 */
fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
