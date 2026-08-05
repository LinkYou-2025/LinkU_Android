package com.linku.core.util.logging

import android.util.Log

/**
 * 디버그 빌드에서만 error 메시지를 로그로 출력합니다.
 *
 * 로그가 비활성화된 경우 메시지 생성을 피하려면 지연 메시지 오버로드를 사용하세요.
 *
 * @param tag 로그로 출력할 태그
 * @param msg 로그로 출력할 지연 생성 메시지
 */
@Deprecated(
    message = "불필요한 메시지 생성을 방지하려면 지연 메시지 오버로드를 사용하세요.",
    replaceWith = ReplaceWith("e(tag) { msg }"),
    level = DeprecationLevel.WARNING,
)
fun LinkuLog.e(
    tag: String,
    msg: String,
) = debugOnly {
    Log.e(tag, msg)
}

/**
 * 디버그 빌드에서만 Throwable이 포함된 error 메시지를 로그로 출력합니다.
 *
 * 로그가 비활성화된 경우 메시지 생성을 피하려면 지연 메시지 오버로드를 사용하세요.
 *
 * @param tag 로그로 출력할 태그
 * @param msg 로그로 출력할 지연 생성 메시지
 * @param tr 그 메시지와 함께 스택 트레이스를 출력할 [Throwable] 객체
 */
@Deprecated(
    message = "불필요한 메시지 생성을 방지하려면 지연 메시지 오버로드를 사용하세요.",
    replaceWith = ReplaceWith("e(tag, tr) { msg }"),
    level = DeprecationLevel.WARNING,
)
fun LinkuLog.e(
    tag: String,
    msg: String,
    tr: Throwable,
) = debugOnly {
    Log.e(tag, msg, tr)
}

/**
 * 디버그 빌드에서만 지연 생성한 error 메시지를 로그로 출력합니다.
 *
 * @param tag 로그로 출력할 태그
 * @param msg 로그로 출력할 지연 생성 메시지
 */
inline fun LinkuLog.e(
    tag: String,
    msg: () -> String,
) = debugOnly {
    Log.e(tag, msg())
}

/**
 * 디버그 빌드에서만 [StackTraceElement]를 기반으로 지연 생성된 error 메시지를 로그로 출력합니다.
 *
 * 태그는 파일 이름으로 설정되며, 호출된 메서드 이름은 메시지 앞에 대괄호로 감싸 포함됩니다.
 *
 * @param caller 호출자 정보를 담고 있는 스택 트레이스 요소
 * @param msg 로그로 출력할 지연 생성 메시지
 */
inline fun LinkuLog.e(
    caller: StackTraceElement,
    msg: () -> String,
) = debugOnly {
    Log.e(caller.getTag, caller.getMsg(msg))
}

/**
 * 디버그 빌드에서만 [StackTraceElement]를 기반으로 지정한 위치 정보가 포함된 지연 생성 error 메시지를 로그로 출력합니다.
 *
 * 태그는 파일 이름으로 설정되며, 지정한 위치 정보는 메시지 앞에 대괄호로 감싸 포함됩니다.
 *
 * @param caller 호출자 정보를 담고 있는 스택 트레이스 요소
 * @param locate 로그 메시지 앞에 표시할 위치 정보
 * @param msg 로그로 출력할 지연 생성 메시지
 */
inline fun LinkuLog.e(
    caller: StackTraceElement,
    locate: String,
    msg: () -> String,
) = debugOnly {
    Log.e(caller.getTag, getMsg(locate, msg))
}

/**
 * 디버그 빌드에서만 Throwable이 포함된 지연 생성 error 메시지를 로그로 출력합니다.
 *
 * @param tag 로그로 출력할 태그
 * @param msg 로그로 출력할 지연 생성 메시지
 * @param tr 그 메시지와 함께 스택 트레이스를 출력할 [Throwable] 객체
 */
inline fun LinkuLog.e(
    tag: String,
    tr: Throwable,
    msg: () -> String,
) = debugOnly {
    Log.e(tag, msg(), tr)
}

/**
 * 디버그 빌드에서만 [StackTraceElement]를 기반으로 Throwable이 포함된 지연 생성 error 메시지를 로그로 출력합니다.
 *
 * 태그는 파일 이름으로 설정되며, 호출된 메서드 이름은 메시지 앞에 대괄호로 감싸 포함됩니다.
 *
 * @param caller 호출자 정보를 담고 있는 스택 트레이스 요소
 * @param tr 그 메시지와 함께 스택 트레이스를 출력할 [Throwable] 객체
 * @param msg 로그로 출력할 지연 생성 메시지
 */
inline fun LinkuLog.e(
    caller: StackTraceElement,
    tr: Throwable,
    msg: () -> String,
) = debugOnly {
    Log.e(caller.getTag, caller.getMsg(msg), tr)
}

/**
 * 디버그 빌드에서만 [StackTraceElement]를 기반으로 지정한 위치 정보와 [Throwable]이 포함된 지연 생성 error 메시지를 로그로 출력합니다.
 *
 * 태그는 파일 이름으로 설정되며, 지정한 위치 정보는 메시지 앞에 대괄호로 감싸 포함됩니다.
 *
 * @param caller 호출자 정보를 담고 있는 스택 트레이스 요소
 * @param locate 로그 메시지 앞에 표시할 위치 정보
 * @param tr 그 메시지와 함께 스택 트레이스를 출력할 [Throwable] 객체
 * @param msg 로그로 출력할 지연 생성 메시지
 */
inline fun LinkuLog.e(
    caller: StackTraceElement,
    locate: String,
    tr: Throwable,
    msg: () -> String,
) = debugOnly {
    Log.e(caller.getTag, getMsg(locate, msg), tr)
}
