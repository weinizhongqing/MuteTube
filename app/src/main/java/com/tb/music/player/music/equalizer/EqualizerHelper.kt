package com.tb.music.player.music.equalizer

import android.annotation.SuppressLint
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.util.Log
import androidx.media3.common.C
import com.tb.music.player.config.AppConfig

@SuppressLint("UnsafeOptInUsageError")
class EqualizerHelper private constructor() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { EqualizerHelper() }
        private const val TAG = "EqualizerHelper"
    }

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    private val equalizerBands = mutableListOf<BandInfo>()

    /** 绑定到新的 AudioSessionId（ExoPlayer 1.8.0 推荐调用） */
    fun attachToSession(sessionId: Int) {

        if (sessionId == C.AUDIO_SESSION_ID_UNSET) {
            Log.w(TAG, "Invalid session id, skip EQ creation")
            return
        }

        release()

        equalizer = safeCreate("Equalizer") { Equalizer(0, sessionId) }
        bassBoost = safeCreate("BassBoost") { BassBoost(0, sessionId) }
        virtualizer = safeCreate("Virtualizer") { Virtualizer(0, sessionId) }

        init()
    }

    /** 安全创建，避免某些机型崩溃 */
    private inline fun <T> safeCreate(name: String, creator: () -> T): T? {
        return try {
            creator().also { Log.d(TAG, "$name created") }
        } catch (e: Exception) {
            Log.e(TAG, "$name create failed: ${e.message}")
            null
        }
    }

    /** 释放旧的音效 */
    fun release() {
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()

        equalizer = null
        bassBoost = null
        virtualizer = null
        equalizerBands.clear()
    }

    /** 初始化 EQ + Bass + Virtualizer */
    private fun init() {
        val eq = equalizer ?: return

        setEqualizerEnable(AppConfig.equalizerOpen)

        if (AppConfig.bassValue > 0) setBassStrength(AppConfig.bassValue)
        if (AppConfig.virtualizeValue > 0) setVirtualizerStrength(AppConfig.virtualizeValue)

        equalizerBands.addAll(loadBandInfo())

        for (i in 0 until eq.numberOfBands) {
            val hz = AppConfig.getEqualizerHz(i)
            if (hz >= 0) setEqualizer(i, hz)
        }
    }

    /** 打开/关闭 EQ（含 Bass & Virtualizer） */
    fun setEqualizerEnable(enable: Boolean) {
        AppConfig.equalizerOpen = enable

        equalizer?.enabled = enable
        bassBoost?.enabled = enable
        virtualizer?.enabled = enable
    }

    fun setBassStrength(strength: Int) {
        try {
            bassBoost?.takeIf { it.strengthSupported }
                ?.setStrength(strength.toShort())
        } catch (e: Exception) {
            Log.e(TAG, "BassBoost set failed: ${e.message}")
        }
    }

    fun setVirtualizerStrength(strength: Int) {
        try {
            virtualizer?.takeIf { it.strengthSupported }
                ?.setStrength(strength.toShort())
        } catch (e: Exception) {
            Log.e(TAG, "Virtualizer set failed: ${e.message}")
        }
    }

    /** 设置 EQ 单频 */
    fun setEqualizer(band: Int, hz: Int) {
        val eq = equalizer ?: return
        val info = equalizerBands.getOrNull(band) ?: return

        try {
            eq.setBandLevel(band.toShort(), hzToLevel(hz, info))
        } catch (e: Exception) {
            Log.e(TAG, "setEqualizer failed: ${e.message}")
        }
    }

    /** 一次设置全部 EQ */
    fun setEqualizer(hzList: List<Int>) {
        val eq = equalizer ?: return
        if (hzList.size != equalizerBands.size) return

        hzList.forEachIndexed { index, hz ->
            try {
                eq.setBandLevel(index.toShort(), hzToLevel(hz, equalizerBands[index]))
            } catch (_: Exception) {
            }
        }
    }

    /** 使用系统预设 */
    fun setUsePreset(preset: Short): List<Int> {
        equalizer?.usePreset(preset)
        return getCurrentHz()
    }

    /** 频率 → 电平值 */
    private fun hzToLevel(hz: Int, info: BandInfo): Short {
        val clamped = hz.coerceIn(info.minFreq, info.maxFreq)
        val percent = (clamped - info.minFreq).toFloat() /
                (info.maxFreq - info.minFreq)

        return (info.minLevel +
                percent * (info.maxLevel - info.minLevel))
            .toInt().toShort()
    }

    /** 电平值 → 频率 */
    private fun levelToHz(level: Short, info: BandInfo): Int {
        val percent = (level - info.minLevel).toFloat() /
                (info.maxLevel - info.minLevel)

        return (info.minFreq +
                percent * (info.maxFreq - info.minFreq))
            .toInt()
    }

    /** 读取当前 EQ 参数 */
    fun getCurrentHz(): List<Int> {
        val eq = equalizer ?: return emptyList()
        return equalizerBands.mapIndexed { i, info ->
            levelToHz(eq.getBandLevel(i.toShort()), info)
        }
    }

    /** 读取设备提供的 EQ 频率范围 */
    private fun loadBandInfo(): List<BandInfo> {
        val eq = equalizer ?: return emptyList()

        val range = eq.bandLevelRange
        val minLevel = range[0]
        val maxLevel = range[1]
        val bandCount = eq.numberOfBands.toInt()

        return List(bandCount) { i ->
            val freqRange = eq.getBandFreqRange(i.toShort())

            BandInfo(
                minFreq = freqRange[0] / 1000,
                maxFreq = freqRange[1] / 1000,
                currentFreq = eq.getCenterFreq(i.toShort()) / 1000,
                minLevel = minLevel,
                maxLevel = maxLevel
            )
        }
    }

    data class BandInfo(
        val minFreq: Int,
        val maxFreq: Int,
        val currentFreq: Int,
        val minLevel: Short,
        val maxLevel: Short
    )
}
