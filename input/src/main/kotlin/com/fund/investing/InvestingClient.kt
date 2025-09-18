package com.fund.investing

import com.alibaba.fastjson2.JSON
import mu.KotlinLogging
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@Component
class InvestingClient {

    private val log = KotlinLogging.logger {}

    private val cookieStore: MutableMap<String, MutableList<Cookie>> = ConcurrentHashMap()
    private val cookiesInitialized = AtomicBoolean(false)

    // 串行请求与节流控制：全局仅允许单请求进入，确保 3 QPS
    private val requestSemaphore = Semaphore(5, true)

    // Cookie 初始化互斥，避免并发初始化
    private val cookieInitSemaphore = Semaphore(1, true)

    // 记录上一次请求的开始时间（受 requestSemaphore 保护）
    private var lastRequestStartMs: Long = 0

    private val client: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookieStore.computeIfAbsent(url.topPrivateDomain() ?: url.host) { mutableListOf() }
                    .apply {
                        removeIf { c -> cookies.any { it.name == c.name } }
                        addAll(cookies)
                    }
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val key = url.topPrivateDomain() ?: url.host
                return cookieStore[key] ?: emptyList()
            }
        })
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // 对外方法：传入完整URL
    @Throws(IOException::class)
    fun loadData(url: String): String {
        // 1) 先访问首页，拿到 Cloudflare/站点 Cookie
        ensureCookies()

        // 2) 再调 API（自动带 Cookie）
        val apiReq = buildApiRequest(url)

        requestSemaphore.acquire()
        try {
            // 保证距离上一次开始至少 1 秒
            waitIfNeededSinceLast()
            lastRequestStartMs = System.currentTimeMillis()

            client.newCall(apiReq).execute().use { response ->
                if (!response.isSuccessful) {
                    log.error { "API request failed: ${response.code}, URL: $url, response: ${response.body?.string()}" }
                    error("api failed: ${response.code}")
                }
                return response.body?.string() ?: ""
            }
        } finally {
            requestSemaphore.release()
        }
    }

    private fun buildApiRequest(url: String): Request {
        return Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept", "application/json, text/plain, */*")
            .header("Accept-Language", "zh-CN,zh;q=0.9")
            .header("Origin", "https://cn.investing.com")
            .header("Referer", "https://cn.investing.com/")
            .header("X-Requested-With", "XMLHttpRequest")
            .build()
    }

    // 首次/失效时访问首页以拿站点Cookie
    private fun ensureCookies(force: Boolean = false) {
        if (!force && cookiesInitialized.get()) return

        cookieInitSemaphore.acquire()
        try {
            if (!force && cookiesInitialized.get()) return

            log.info { "Initializing cookies from home page${if (force) " (forced)" else ""}" }

            val homeReq = Request.Builder()
                .url(HOME_URL)
                .header("User-Agent", USER_AGENT)
                .build()

            client.newCall(homeReq).execute().use { response ->
                if (!response.isSuccessful) {
                    log.error { "Failed to initialize cookies: ${response.code}, response: ${response.body?.string()}" }
                    error("home page request failed: ${response.code}")
                }

                cookiesInitialized.set(true)
                log.info { "Successfully initialized cookies from home page" }
            }
        } finally {
            cookieInitSemaphore.release()
        }
    }

    // 等待至距离上一次请求开始至少 1 秒（在 requestSemaphore 保护下调用）
    private fun waitIfNeededSinceLast() {
        val now = System.currentTimeMillis()
        val delta = now - lastRequestStartMs
        if (delta < 1000) {
            try {
                Thread.sleep(1000 - delta)
            } catch (_: InterruptedException) { /* ignore */
            }
        }
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 1000
        private const val HOME_URL = "https://cn.investing.com/"
        private const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"
    }
}