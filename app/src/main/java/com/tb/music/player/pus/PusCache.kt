package com.tb.music.player.pus

object PusCache {

    private val cacheList = HashMap<String, MutableList<PusAd>>()
    val cfgList = HashMap<String, PusAdConfig>()

    var cacheOpenCount = 1
    var cacheInterCount = 1
    var cacheNativeCount = 1


    /** 添加缓存广告 */
    fun addCache(type: Type, ad: PusAd) {
        synchronized(cacheList) {
            val list = cacheList.getOrPut(type.desc) { mutableListOf() }
            list.add(ad)
        }
    }

    /** 检查缓存是否未满，true = 可继续加载 */
    fun checkCache(spec: PusAdConfig, key: String): Boolean {
        synchronized(cacheList) {
            val cacheType = spec.cacheType
            val currentList = cacheList[cacheType.desc] ?: return true

            // 清除不可用项
            val validList = currentList.filter { it.isAva() }.toMutableList()
            cacheList[cacheType.desc] = validList

            val limit = when (cacheType.desc) {
                Type.INTer.desc -> cacheInterCount
                Type.NAV.desc -> cacheNativeCount
                Type.OPEN.desc -> cacheOpenCount
                else -> 0
            }

            return validList.size < limit
        }
    }

    /** 获取指定 key 的有效缓存广告 */
    fun getCache(key: String): PusAd? {
        val spec = getOrInitSpec(key) ?: return null
        synchronized(cacheList) {
            return cacheList[spec.cacheType.desc]?.firstOrNull { it.isAva() }
        }
    }

    /** 获取配置项并初始化（懒加载） */
    fun getOrInitSpec(key: String): PusAdConfig? {
        return cfgList[key] ?: run {
            PusManager.instance.initCfg()
            cfgList[key]
        }
    }

}