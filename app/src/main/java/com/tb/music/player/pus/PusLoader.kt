package com.tb.music.player.pus

import android.content.Context
import android.util.Log
import com.tb.music.player.pus.PusManager.Companion.TAG_A
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set
import kotlin.coroutines.CoroutineContext

class PusLoader (private val context: Context) : CoroutineScope {

    /** Loader 自己的作用域（可在 destroy 时取消） */
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext =
        Dispatchers.Main + job

    /** 每个 Type 一个 Mutex */
    private val loadMutexMap = ConcurrentHashMap<Type, Mutex>()
    private val preLoadMutexMap = ConcurrentHashMap<Type, Mutex>()

    /** 超时兜底 */
    private val timeoutJobMap = ConcurrentHashMap<Type, Job>()

    fun destroy() {
        job.cancel()
        loadMutexMap.clear()
        preLoadMutexMap.clear()
        timeoutJobMap.clear()
    }

    /**
     * 正式加载（可等待）
     */
    suspend fun start(
        key: String,
        spec: PusAdConfig,
        adUnit: NodeUnit,
        waitTime: Int
    ): PusAd? {

        if (!spec.isEnable) return null

        val type = spec.cacheType
        val mutex = loadMutexMap.getOrPut(type) { Mutex() }

        // 1️⃣ 快速命中缓存
        PusManager.instance.get(key)?.let { return it }

        // 2️⃣ 已在加载，等待
        if (mutex.isLocked) {
            Log.d(TAG_A, "[${spec.isEnable}] isLocked${mutex.isLocked}")
            repeat(waitTime) {
                PusManager.instance.get(key)?.let { return it }
                delay(200)
            }
            return PusManager.instance.get(key)
        }

        // 3️⃣ 串行加载
        return mutex.withLock {

            PusManager.instance.get(key)?.let { return@withLock it }
            val timeoutJob = launchTimeout(type, loadMutexMap)
            timeoutJobMap[type] = timeoutJob

            val result = runCatching {
                // ✅ AdMob 必须 Main 线程
                LoaderCode.loadByType(context, adUnit, type)
            }.getOrNull()

            timeoutJob.cancel()
            timeoutJobMap.remove(type)

            result
        }
    }

    /**
     * 预加载（不等待）
     */
    suspend fun preStart(
        key: String,
        spec: PusAdConfig,
        adUnit: NodeUnit
    ): PusAd? {

        if (!spec.isEnable || !PusCache.checkCache(spec,key)){
            return null
        }

        val type = spec.cacheType
        val mutex = preLoadMutexMap.getOrPut(type) { Mutex() }

        if (mutex.isLocked) {
            Log.d(TAG_A, "[${key}] isLocked${mutex.isLocked}")
            return null
        }

        return mutex.withLock {

            val timeoutJob = launchTimeout(type, preLoadMutexMap)
            timeoutJobMap[type] = timeoutJob

            val result = runCatching {
                LoaderCode.loadByType(context, adUnit, type)
            }.getOrNull()

            timeoutJob.cancel()
            timeoutJobMap.remove(type)

            result
        }
    }

    /**
     * 超时兜底（防 SDK 卡死）
     */
    private fun launchTimeout(
        type: Type,
        map: ConcurrentHashMap<Type, Mutex>
    ): Job = launch {
        delay(30_000)

        map.remove(type)
        timeoutJobMap.remove(type)
    }
}