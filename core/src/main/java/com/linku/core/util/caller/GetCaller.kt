package com.linku.core.util.caller

/**
 * 이 함수를 호출한 상위 메서드의 [StackTraceElement] 정보를 가져옵니다.
 *
 * 스택 트레이스의 인덱스 1에 해당하는 요소를 참조하여 이 함수를 직접적으로
 * 호출한 지점의 클래스명, 메서드명, 파일명 및 라인 번호 정보를 반환합니다.
 *
 * @return 호출자의 정보를 담고 있는 [StackTraceElement] 객체
 */
fun getCaller(): StackTraceElement = Throwable().stackTrace[1]
