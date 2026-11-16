package com.linku.data.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 같은 작업이 동시에 여러 번 요청되면 하나만 실행하고 결과를 공유합니다.
 *
 * @param T 작업 결과 타입
 */
class SingleFlight<T> {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO) // supervisor 다들 잘 알고 있을거라 그냥 쓸게요

    private val lock = Mutex()

    private var inFlight: Deferred<T>? = null

    /**
     * @param block 실제로 수행할 작업
     * @return 진행 중인 작업이 있으면 그 결과, 없으면 새로 실행한 결과
     */
    suspend fun run(block: suspend () -> T): T {
        val flight = lock.withLock {
            if (inFlight == null || inFlight?.isCompleted == true) {
                inFlight = scope.async {
                    block()
                }
            }
            inFlight!!
        }
        return flight.await()
    }

    /** 진행 중인 공유를 끊어, 다음 호출이 반드시 새 요청이 되게 합니다. */
    suspend fun invalidate() {
        lock.withLock {
            inFlight = null
        }
    }
}