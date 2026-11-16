package com.linku.data.util

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

/**
 * [SingleFlight]가 동시 중복 호출만 하나로 합치고, 끝난 뒤나 무효화한 뒤의 호출은 새로 실행하는지 검증합니다.
 *
 * 작업이 끝나는 시점을 [CompletableDeferred]로 직접 제어해, 실제 시간이나 스레드 타이밍에 의존하지 않고
 * "진행 중"과 "끝남" 상태를 만들어 냅니다.
 */
class SingleFlightTest {

    /** 진행 중인 작업이 있을 때 동시에 들어온 4번의 호출이 작업 한 번과 같은 결과를 공유하는지 검증합니다. */
    @Test
    fun `동시에 여러 번 호출해도 작업은 한 번만 실행하고 결과를 공유한다`() = runTest {
        val flight = SingleFlight<Int>()
        val calls = AtomicInteger(0)
        val gate = CompletableDeferred<Unit>()

        val results = (1..4).map {
            async {
                flight.run {
                    calls.incrementAndGet()
                    gate.await()
                    42
                }
            }
        }
        // 4개 호출이 모두 run에 진입해 진행 중인 작업을 기다리는 상태로 만든 뒤 작업을 끝낸다.
        advanceUntilIdle()
        gate.complete(Unit)

        assertEquals(listOf(42, 42, 42, 42), results.awaitAll())
        assertEquals(1, calls.get())
    }

    /** 작업이 끝난 뒤의 호출은 옛 결과를 재사용하지 않고 새로 실행하는지 검증합니다. */
    @Test
    fun `끝난 뒤에 다시 호출하면 작업을 새로 실행한다`() = runTest {
        val flight = SingleFlight<Int>()
        val calls = AtomicInteger(0)

        val first = flight.run { calls.incrementAndGet() }
        val second = flight.run { calls.incrementAndGet() }

        assertEquals(1, first)
        assertEquals(2, second)
        assertEquals(2, calls.get())
    }

    /** 무효화한 뒤의 호출은 진행 중이던 작업과 합쳐지지 않고 새로 실행하는지 검증합니다. */
    @Test
    fun `무효화하면 진행 중인 작업이 있어도 다음 호출은 새로 실행한다`() = runTest {
        val flight = SingleFlight<Int>()
        val calls = AtomicInteger(0)
        val gate = CompletableDeferred<Unit>()

        val first = async {
            flight.run {
                calls.incrementAndGet()
                gate.await()
                1
            }
        }
        advanceUntilIdle()

        flight.invalidate()

        val second = async {
            flight.run {
                calls.incrementAndGet()
                gate.await()
                2
            }
        }
        advanceUntilIdle()
        gate.complete(Unit)

        // 먼저 기다리던 호출은 자기 작업 결과를, 무효화 뒤의 호출은 새 작업 결과를 받는다.
        assertEquals(1, first.await())
        assertEquals(2, second.await())
        assertEquals(2, calls.get())
    }

    /** 작업이 예외로 실패해도 이후 호출이 막히지 않고 정상 실행되는지 검증합니다. */
    @Test
    fun `작업이 실패해도 다음 호출은 정상적으로 실행한다`() = runTest {
        val flight = SingleFlight<Int>()

        try {
            flight.run { error("일시적인 실패") }
            fail("예외가 호출자에게 전달되어야 합니다.")
        } catch (expected: IllegalStateException) {
            assertEquals("일시적인 실패", expected.message)
        }

        assertEquals(7, flight.run { 7 })
    }
}
