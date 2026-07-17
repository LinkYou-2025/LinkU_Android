package com.linku.core.util.logging

import android.util.Log

/**
 * 디버그 빌드에서만 debug 메시지를 로그로 출력합니다.
 *
 * 로그가 비활성화된 경우 메시지 생성을 피하려면 지연 메시지 오버로드를 사용하세요.
 *
 * @param tag 로그로 출력할 태그
 * @param msg 로그로 출력할 지연 생성 메시지
 */
@Deprecated(
    message = "불필요한 메시지 생성을 방지하려면 지연 메시지 오버로드를 사용하세요.",
    replaceWith = ReplaceWith("d(tag) { msg }"),
    level = DeprecationLevel.WARNING,
)
fun LinkuLog.d(
    tag: String,
    msg: String,
) = debugOnly {
    Log.d(tag, msg)
}

/**
 * 디버그 빌드에서만 Throwable이 포함된 debug 메시지를 로그로 출력합니다.
 *
 * 로그가 비활성화된 경우 메시지 생성을 피하려면 지연 메시지 오버로드를 사용하세요.
 *
 * @param tag 로그로 출력할 태그
 * @param msg 로그로 출력할 지연 생성 메시지
 * @param tr 그 메시지와 함께 스택 트레이스를 출력할 [Throwable] 객체
 */
@Deprecated(
    message = "불필요한 메시지 생성을 방지하려면 지연 메시지 오버로드를 사용하세요.",
    replaceWith = ReplaceWith("d(tag, tr) { msg }"),
    level = DeprecationLevel.WARNING,
)
fun LinkuLog.d(
    tag: String,
    msg: String,
    tr: Throwable,
) = debugOnly {
    Log.d(tag, msg, tr)
}

/**
 * 디버그 빌드에서만 지연 생성한 debug 메시지를 로그로 출력합니다.
 *
 * @param tag 로그로 출력할 태그
 * @param msg 로그로 출력할 지연 생성 메시지
 */
inline fun LinkuLog.d(
    tag: String,
    msg: () -> String,
) = debugOnly {
    Log.d(tag, msg())
}

/**
 * 디버그 빌드에서만 [StackTraceElement]를 기반으로 지연 생성된 debug 메시지를 로그로 출력합니다.
 *
 * 태그는 파일 이름으로 설정되며, 메시지 끝에 호출된 메서드 이름이 포함됩니다.
 *
 * @param caller 호출자 정보를 담고 있는 스택 트레이스 요소
 * @param msg 로그로 출력할 지연 생성 메시지
 */
inline fun LinkuLog.d(
    caller: StackTraceElement,
    msg: () -> String,
) = debugOnly {
    Log.d(caller.fileName, "${msg()} in ${caller.methodName}")
}

/**
 * 디버그 빌드에서만 Throwable이 포함된 지연 생성 debug 메시지를 로그로 출력합니다.
 *
 * @param tag 로그로 출력할 태그
 * @param msg 로그로 출력할 지연 생성 메시지
 * @param tr 그 메시지와 함께 스택 트레이스를 출력할 [Throwable] 객체
 */
inline fun LinkuLog.d(
    tag: String,
    tr: Throwable,
    msg: () -> String,
) = debugOnly {
    Log.d(tag, msg(), tr)
}


/**
 * 디버그 빌드에서만 [StackTraceElement]를 기반으로 Throwable이 포함된 지연 생성 debug 메시지를 로그로 출력합니다.
 *
 * 태그는 파일 이름으로 설정되며, 메시지 끝에 호출된 메서드 이름이 포함됩니다.
 *
 * @param caller 호출자 정보를 담고 있는 스택 트레이스 요소
 * @param tr 그 메시지와 함께 스택 트레이스를 출력할 [Throwable] 객체
 * @param msg 로그로 출력할 지연 생성 메시지
 */
inline fun LinkuLog.d(
    caller: StackTraceElement,
    tr: Throwable,
    msg: () -> String,
) = debugOnly {
    Log.d(caller.fileName, "${msg()} in ${caller.methodName}", tr)
}