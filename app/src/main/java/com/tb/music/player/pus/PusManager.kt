package com.tb.music.player.pus

import android.content.Context
import android.util.Base64
import com.google.android.gms.ads.MobileAds
import com.tb.music.player.TB
import com.tb.music.player.config.MuteEvent
import com.tb.music.player.config.MuteRemoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PusManager private constructor() {

    companion object {
        val instance: PusManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { PusManager() }
        const val TAG_A = "AdManage"
    }

    private lateinit var context: Context
    private lateinit var pusLoader: PusLoader
    private var initialized = false

    private val preloadSemaphore = Semaphore(2)

    fun initialize(context: Context): PusManager {
        this.context = context.applicationContext
        return try {
            MobileAds.initialize(context)
            initCfg()
            pusLoader = PusLoader(this.context)
            initialized = true
            this
        } catch (e: Exception) {
            initialized = false
            e.printStackTrace()
            MuteEvent.event("tb_ainit_fai")
            this
        }
    }

    suspend fun load(
        key: String,
        waitTime: Int = 25,
    ): PusAd? = withContext(Dispatchers.Main) {
        if (!initialized) return@withContext null
        val spec = PusCache.getOrInitSpec(key) ?: return@withContext null

        for (unit in spec.id) {
            pusLoader.start(key, spec, unit, waitTime)?.let { return@withContext it }
        }
        null
    }

    suspend fun preLoad(key: String) = withContext(Dispatchers.Main){
        if (!initialized) return@withContext
        val spec = PusCache.getOrInitSpec(key) ?: return@withContext

        for (unit in spec.id) {
            pusLoader.preStart(key, spec, unit)?.let { return@withContext }
        }
    }

    suspend fun preLoads(vararg keys: String) = coroutineScope {
        keys.forEach { k ->
            launch {
                preloadSemaphore.withPermit {
                    preLoad(k)
                }
            }
        }
    }

    fun get(key: String): PusAd? = PusCache.getCache(key)


    private val KEY_CONFIG = "tb_config"
    private val KEY_ADS_OPEN = "tb_ads_open"
    private val KEY_ADS_INTER = "tb_ads_inter"
    private val KEY_ADS_NATIVE = "tb_ads_native"
    private val KEY_A_COUNT = "tb_a_count"
    private val KEY_NATIVE = "native"
    private val KEY_INTER = "inter"
    private val KEY_OPEN = "open"
    private val KEY_CFG_KEY = "tb_key"
    private val KEY_CFG_ENABLE = "tb_enable"
    private val KEY_CFG_TYPE = "tb_type"

    private fun getAdJson(): JSONObject {
        val encodedConfig = MuteRemoteConfig.ins.getAdConfig()
        if (encodedConfig.isBlank()) return JSONObject()
        return try {
            val decodedBytes = Base64.decode(encodedConfig.toByteArray(), Base64.DEFAULT)
            JSONObject(String(decodedBytes))
        } catch (e: Exception) {
            // 返回空对象以触发后续的安全返回
            JSONObject()
        }
    }

    fun initCfg() {
        try {
            val json = getAdJson()
            val configArray = json.optJSONArray(KEY_CONFIG) ?: return
            val cacheCounts = json.optJSONObject(KEY_A_COUNT) ?: JSONObject()

            val nativeIds = parseAdUnits(json.optJSONArray(KEY_ADS_NATIVE))
            val interIds = parseAdUnits(json.optJSONArray(KEY_ADS_INTER))
            val openIds = parseAdUnits(json.optJSONArray(KEY_ADS_OPEN))

            PusCache.cacheInterCount = cacheCounts.optInt(KEY_INTER, 1)
            PusCache.cacheNativeCount = cacheCounts.optInt(KEY_NATIVE, 1)
            PusCache.cacheOpenCount = cacheCounts.optInt(KEY_OPEN, 1)

            // 重建配置映射，避免保留过期项
            val newCfg = HashMap<String, PusAdConfig>()
            for (i in 0 until configArray.length()) {
                val obj = configArray.optJSONObject(i) ?: continue
                val key = obj.optString(KEY_CFG_KEY).trim()
                if (key.isEmpty()) continue
                val enabledCode = obj.optInt(KEY_CFG_ENABLE, -1)

                val isEnabled = adEnable(enabledCode)

                val typeStr = obj.optString(KEY_CFG_TYPE)
                val (adList, cType) = when (typeStr) {
                    KEY_NATIVE -> nativeIds to Type.NAV
                    KEY_INTER -> interIds to Type.INTer
                    else -> openIds to Type.OPEN
                }

                if (adList.isNotEmpty()) {
                    newCfg[key] = PusAdConfig(adList.toMutableList(), isEnabled, cType)
                }
            }
            PusCache.cfgList.clear()
            PusCache.cfgList.putAll(newCfg)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun adEnable(code: Int): Boolean {
        return when (code) {
            0 -> {
                true
            }

            1 -> {
                !TB.instance.isBuy
            }

            2 -> {
                TB.instance.isBuy
            }

            else -> {
                false
            }
        }
    }


    private fun parseAdUnits(jsonArray: JSONArray?): List<NodeUnit> {
        if (jsonArray == null) return emptyList()
        val result = mutableListOf<NodeUnit>()
        for (i in 0 until jsonArray.length()) {
            val pair = jsonArray.optJSONArray(i) ?: continue
            if (pair.length() < 2) continue
            val key = pair.optString(0).trim()
            val value = pair.optString(1).trim()
            if (value.isEmpty()) continue
            result.add(NodeUnit(value, getTypeFromKey(key)))
        }
        return result
    }

    private fun getTypeFromKey(type: String): Type = when (type.lowercase()) {
        KEY_NATIVE -> Type.NAV
        KEY_INTER -> Type.INTer
        else -> Type.OPEN
    }
}