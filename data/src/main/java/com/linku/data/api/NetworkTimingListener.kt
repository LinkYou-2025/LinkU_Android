package com.linku.data.api

import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.d
import com.linku.data.BuildConfig
import okhttp3.Call
import okhttp3.Connection
import okhttp3.EventListener
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 요청 하나의 지연 시간을 구간별로 나누어 Logcat 태그 [TAG]로 한 줄씩 기록하는 리스너입니다.
 *
 * API 응답이 느릴 때 원인이 서버 처리, 네트워크, 앱 중 어디인지 가려내기 위한 디버그 전용 도구이며,
 * 요청 URL의 경로만 기록하고 쿼리, 헤더, 본문은 남기지 않습니다. 호출(Call)마다 새 인스턴스가 만들어집니다.
 *
 * 로그 필드(단위 ms, 해당 없으면 `-`):
 * - `pre`: 호출 시작부터 첫 요청이 실제로 나가기 시작할 때까지(디스패처 대기열, 인터셉터 처리,
 *   새 연결이면 dns, tcp, tls까지 포함). 연결을 재사용했는데 크다면 대기열이나 인터셉터 지연을 의심한다.
 * - `dns`: 도메인 조회
 * - `tcp`: TCP 연결(TLS 제외)
 * - `tls`: TLS 핸드셰이크
 * - `reqBody`: 요청 본문 전송(이미지 업로드 포함)
 * - `wait`: 요청을 다 보낸 뒤 첫 응답까지(서버 처리와 왕복 시간)
 * - `resp`: 응답 헤더 수신부터 본문 수신 완료까지
 * - `total`: 호출 시작부터 종료까지
 * - `reuse`: 기존 연결 재사용 여부
 * - `proto`: 사용한 HTTP 프로토콜
 * - `attempts`: 실제로 서버로 나간 요청 횟수(토큰 갱신 후 재시도 등이 있으면 2 이상)
 */
internal class NetworkTimingListener : EventListener() {

    private var callStartNs = 0L
    private var dnsStartNs = 0L
    private var dnsMs = UNSET
    private var connectStartNs = 0L
    private var connectMs = UNSET
    private var tlsStartNs = 0L
    private var tlsMs = UNSET
    private var requestHeadersEndNs = 0L
    private var requestBodyStartNs = 0L
    private var requestBodyMs = UNSET
    private var requestBodyEndNs = 0L
    private var responseHeadersStartNs = 0L
    private var responseBodyEndNs = 0L
    private var reused = true
    private var protocol: Protocol? = null
    private var attempts = 0
    private var firstRequestHeadersStartNs = 0L

    /** 호출 시작 시각을 기록합니다. */
    override fun callStart(call: Call) {
        callStartNs = System.nanoTime()
    }

    override fun dnsStart(call: Call, domainName: String) {
        dnsStartNs = System.nanoTime()
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<java.net.InetAddress>) {
        dnsMs = elapsedMs(dnsStartNs)
    }

    /** 새 연결을 맺기 시작하면 연결 재사용이 아님을 표시합니다. */
    override fun connectStart(
        call: Call,
        inetSocketAddress: java.net.InetSocketAddress,
        proxy: java.net.Proxy,
    ) {
        reused = false
        connectStartNs = System.nanoTime()
    }

    override fun secureConnectStart(call: Call) {
        tlsStartNs = System.nanoTime()
    }

    override fun secureConnectEnd(call: Call, handshake: okhttp3.Handshake?) {
        tlsMs = elapsedMs(tlsStartNs)
    }

    override fun connectEnd(
        call: Call,
        inetSocketAddress: java.net.InetSocketAddress,
        proxy: java.net.Proxy,
        protocol: Protocol?,
    ) {
        connectMs = elapsedMs(connectStartNs)
    }

    override fun connectionAcquired(call: Call, connection: Connection) {
        protocol = connection.protocol()
    }

    /** 서버로 요청을 보낼 때마다 호출되므로 재시도 횟수를 센다. */
    override fun requestHeadersStart(call: Call) {
        if (attempts == 0) firstRequestHeadersStartNs = System.nanoTime()
        attempts++
        requestBodyMs = UNSET
        requestBodyEndNs = 0L
    }

    override fun requestHeadersEnd(call: Call, request: okhttp3.Request) {
        requestHeadersEndNs = System.nanoTime()
    }

    override fun requestBodyStart(call: Call) {
        requestBodyStartNs = System.nanoTime()
    }

    override fun requestBodyEnd(call: Call, byteCount: Long) {
        requestBodyEndNs = System.nanoTime()
        requestBodyMs = elapsedMs(requestBodyStartNs)
    }

    override fun responseHeadersStart(call: Call) {
        responseHeadersStartNs = System.nanoTime()
    }

    override fun responseBodyEnd(call: Call, byteCount: Long) {
        responseBodyEndNs = System.nanoTime()
    }

    override fun callEnd(call: Call) {
        log(call, error = null)
    }

    override fun callFailed(call: Call, ioe: IOException) {
        log(call, error = ioe)
    }

    /** 수집한 구간 시간을 한 줄로 만들어 Logcat에 기록합니다. */
    private fun log(call: Call, error: IOException?) {
        val request = call.request()
        val sentEndNs = if (requestBodyEndNs > 0L) requestBodyEndNs else requestHeadersEndNs
        val waitMs = if (sentEndNs > 0L && responseHeadersStartNs >= sentEndNs) {
            TimeUnit.NANOSECONDS.toMillis(responseHeadersStartNs - sentEndNs)
        } else {
            UNSET
        }
        val respMs = if (responseHeadersStartNs > 0L && responseBodyEndNs >= responseHeadersStartNs) {
            TimeUnit.NANOSECONDS.toMillis(responseBodyEndNs - responseHeadersStartNs)
        } else {
            UNSET
        }
        val tcpMs = if (connectMs != UNSET) {
            (connectMs - (if (tlsMs != UNSET) tlsMs else 0L)).coerceAtLeast(0L)
        } else {
            UNSET
        }
        val preMs = if (firstRequestHeadersStartNs > 0L) {
            TimeUnit.NANOSECONDS.toMillis(firstRequestHeadersStartNs - callStartNs)
        } else {
            UNSET
        }
        val message = buildString {
            append(request.method).append(' ').append(request.url.encodedPath)
            append("  pre=").append(format(preMs))
            append(" dns=").append(format(dnsMs))
            append(" tcp=").append(format(tcpMs))
            append(" tls=").append(format(tlsMs))
            append(" reqBody=").append(format(requestBodyMs))
            append(" wait=").append(format(waitMs))
            append(" resp=").append(format(respMs))
            append(" total=").append(elapsedMs(callStartNs))
            append(" reuse=").append(reused)
            append(" proto=").append(protocol ?: "-")
            append(" attempts=").append(attempts)
            if (error != null) append(" FAILED=").append(error.javaClass.simpleName)
        }
        LinkuLog.d(TAG) { message }
    }

    private fun elapsedMs(startNs: Long): Long =
        TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

    private fun format(valueMs: Long): String = if (valueMs == UNSET) "-" else valueMs.toString()

    companion object {
        private const val TAG = "NetTiming"
        private const val UNSET = -1L

        /** 호출마다 새 [NetworkTimingListener]를 만드는 팩토리입니다. */
        val FACTORY = Factory { NetworkTimingListener() }
    }
}

/**
 * debug 빌드에서만 [NetworkTimingListener]를 연결합니다. release 빌드에서는 아무것도 바꾸지 않습니다.
 *
 * @return 같은 [OkHttpClient.Builder]
 */
internal fun OkHttpClient.Builder.withNetworkTiming(): OkHttpClient.Builder = apply {
    if (BuildConfig.DEBUG) eventListenerFactory(NetworkTimingListener.FACTORY)
}
