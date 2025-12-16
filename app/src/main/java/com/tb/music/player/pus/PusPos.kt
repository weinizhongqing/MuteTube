package com.tb.music.player.pus

object PusPos {

    const val AD_START = "tb_start"

    //语言页点击插屏
    const val AD_LANGUAGE_I = "tb_language_i"

    //语言选择页底部原生
    const val AD_LANGUAGE_N = "tb_language_n"

    //扫描页原生
    const val AD_SCAN_N: String = "tb_scan_n"
    //扫描结果插屏
    const val AD_SCAN_I: String = "tb_scan_i"
    //扫描结果点击插屏
    const val AD_SCAN_CLICK_I: String = "tb_scan_click_i"

    //首页点击插屏
    const val AD_CLICK_I: String = "tb_click_i"

    //播放页返回插屏
    const val AD_BACK_I: String = "tb_back_i"

    //功能页原生
    const val AD_FUNC_N = "tb_func_n"


    fun String.isEnable(): Boolean {
        val t = PusCache.getOrInitSpec(this)
        return t?.isEnable ?: true
    }

}