package com.tb.music.player.config

import android.util.Base64
import com.reyun.solar.engine.SolarEngineManager
import com.tb.music.player.TB
import com.tb.music.player.music.status.PlayState
import com.tb.music.player.utils.getDate
import com.tencent.mmkv.MMKV
import org.json.JSONArray

object AppConfig {

    private val config by lazy { MMKV.defaultMMKV() }

    fun initMMKV() {
        MMKV.initialize(TB.instance)
        if (appFirstOpenTime == -1L) {
            appFirstOpenTime = System.currentTimeMillis()
        }
    }

    var appFirstOpenTime: Long
        get() {
            return config.decodeLong("tb_app_first_open_time", -1)
        }
        set(value) {
            config.encode("tb_app_first_open_time", value)
        }

    fun getInstallDay(): String {
        val installTime = appFirstOpenTime
        val currentTime = System.currentTimeMillis()

        val dayOff = (currentTime - installTime) / (24 * 60 * 60 * 1000)

        return if (dayOff < 0) {
            "-1"
        } else {
            "${if (dayOff in 0..14) dayOff else 15}"
        }
    }


    var appFirstSelectLanguage: Boolean
        get() {
            return config.decodeBool("tb_app_first_select_language", false)
        }
        set(value) {
            config.encode("tb_app_first_select_language", value)
        }

     var appLanguage: String?
        get() {
            return config.decodeString("tb_app_language_code","en")
        }
        set(value) {
            config.encode("tb_app_language_code", value)
        }

    var isScanLocalMusicFirst: Boolean
        get() {
            return config.decodeBool("tb_is_scan_first", false)
        }
        set(value) {
            config.encode("tb_is_scan_first", value)
        }


    var stopMode: Int
        get() {
            return config.decodeInt("tb_auto_end_mode", 0)
        }
        set(value) {
            config.encode("tb_auto_end_mode", value)
        }
    var stopTime: Long
        get() {
            return config.decodeLong("tb_auto_end_time", -1)
        }
        set(value) {
            config.encode("tb_auto_end_time", value)
        }

    fun setDefaultPlayListId(listType: Int, listId: Long) {
        config.encode("tb_default_play_list_id_$listType", listId)
    }

    fun getDefaultPlayListId(listType: Int): Long {
        return config.decodeLong("tb_default_play_list_id_$listType", -1)
    }

    var playMode: Int
        get() {
            return config.decodeInt("tb_play_mode", PlayState.LIST)
        }
        set(value) {
            config.encode("tb_play_mode", value)
        }

    var playListId: Long
        get() {
            return config.decodeLong("tb_play_list", -1)
        }
        set(value) {
            config.encode("tb_play_list", value)
        }

    var playMusicId: Long
        get() {
            return config.decodeLong("tb_play_music_id", -1)
        }
        set(value) {
            config.encode("tb_play_music_id", value)
        }

    var selectPresetIndex: Int
        get() {
            return config.decodeInt("tb_select_preset", 0)
        }
        set(value) {
            config.encode("tb_select_preset", value)
        }

    var equalizerOpen: Boolean
        get() {
            return config.decodeBool("tb_equalizer_op", false)
        }
        set(value) {
            config.encode("tb_equalizer_op", value)
        }

    var bassValue: Int
        get() {
            return config.decodeInt("tb_bass_val", 500)
        }
        set(value) {
            config.encode("tb_bass_val", value)
        }

    var virtualizeValue: Int
        get() {
            return config.decodeInt("tb_virtualize_val", 500)
        }
        set(value) {
            config.encode("tb_virtualize_val", value)
        }

    fun setEqualizerHz(pos: Int, hz: Int) {
        config.encode("tb_eq_hz_$pos", hz)
    }

    fun getEqualizerHz(pos: Int): Int {
        return config.decodeInt("tb_eq_hz_$pos", -1)
    }

    var userKey: String?
        get() = config.decodeString("tb_uk")
        set(value) {
            config.encode("tb_uk", value)
        }

    var clickAdValue: Double
        get() {
            return config.decodeDouble("tb_click_ad_val", 0.0)
        }
        set(value) {
            config.encode("tb_click_ad_val", value)
        }

    var clickAdValueUnit: String?
        get() {
            return config.decodeString("tb_click_ad_val_unit")
        }
        set(value) {
            config.encode("tb_click_ad_val_unit", value)
        }

    var showAdValue: Double
        get() {
            return config.decodeDouble("tb_show_ad_val", 0.0)
        }
        set(value) {
            config.encode("tb_show_ad_val", value)
        }

    var showAdValueUnit: String?
        get() {
            return config.decodeString("tb_show_ad_val")
        }
        set(value) {
            config.encode("tb_show_ad_val", value)
        }

    var uploadAdCumValue: Boolean
        get() {
            return config.decodeBool("tb_ad_cum_val", false)
        }
        set(value) {
            config.encode("tb_ad_cum_val", value)
        }

    var showAdSaveValue: Double
        get() {
            return config.decodeDouble("tb_show_ad_save_val", 0.0)
        }
        set(value) {
            config.encode("tb_show_ad_save_val", value)
        }

    var isUploadPassClock: Boolean
        get() {
            return config.decodeBool("tb_up_clock", false)
        }
        set(value) {
            config.encode("tb_up_clock", value)
        }

    var plyRefStr: String?
        get() {
            return config.decodeString("tb_prs")
        }
        set(value) {
            config.encode("tb_prs", value)
        }

    var reyunRefStr: String?
        get() {
            return config.decodeString("tb_ryrs")
        }
        set(value) {
            config.encode("tb_ryrs", value)
        }

    fun isM(): Boolean {
        val rs = plyRefStr
        if (rs.isNullOrEmpty()) return false
        return rs.contains("facebook", true)
                || rs.contains("fb4a", true)
                || rs.contains("instagram", true)
                || rs.contains("fb", true)
                || rs.contains("ig4a", true)
                || checkUserConfig(rs)

    }


    private fun checkUserConfig(attr: String): Boolean {
        val config = MuteRemoteConfig.ins.getUseConfig()
        if (config.isEmpty()) return false
        try {
            JSONArray(String(Base64.decode(config, Base64.NO_WRAP))).run {
                for (i in 0 until length()) {
                    if (attr.contains(getString(i), true)) return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    fun reyunMl(network: String? = null): Boolean {
        var ref = network
        if (ref.isNullOrEmpty()) {
            ref = getReyunNetwork()
            if (ref.isNullOrEmpty()) {
                ref = reyunRefStr
            }
        }
        return !ref.isNullOrEmpty() && ref != "-1"
    }


    private fun getReyunNetwork(): String? {
        return try {
            SolarEngineManager.getInstance().attribution?.optString("channel_id")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun setRpFcmDally() {
        val time = System.currentTimeMillis()
        val timeString = time.getDate()
        config.encode("${timeString}_fd", true)
    }

    fun getRpFcmDally(): Boolean {
        val time = System.currentTimeMillis()
        val timeString = time.getDate()
        return config.decodeBool("${timeString}_fd", false)
    }

    fun setToken(token: String) {
        config.encode("tb_ft", token)
    }

    fun getToken(): String {
        return config.decodeString("tb_ft", "") ?: ""
    }

    fun setGaid(id: String) {
        config.encode("tb_sgaid", id)
    }

    fun getGaid(): String {
        return config.decodeString("tb_sgaid", "") ?: ""
    }

    var isRepUmp: Boolean
        get() = config.decodeBool("tb_rump", false)
        set(value) {
            config.encode("tb_rump", value)
        }

    
}