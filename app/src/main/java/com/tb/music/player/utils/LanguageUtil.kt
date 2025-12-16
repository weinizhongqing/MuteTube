package com.tb.music.player.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.tb.music.player.R
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

object LanguageUtil {


    fun init() {
        getLanguageList()
    }


    fun getSelectLanguageCode(): String = AppConfig.appLanguage ?: "en"

    val langListData = ArrayList<AppLangModel>()



    fun getLanguageList() {
        val codes = TB.instance.resources.getStringArray(R.array.tb_language_code)
        val names = TB.instance.resources.getStringArray(R.array.tb_language_name)
        val list = codes.indices.map { AppLangModel(it, codes[it], names[it]) }
        langListData.clear()
        langListData.addAll(list)
    }


    private val _currentLocale = MutableStateFlow(Locale.ENGLISH)
    val currentLocale: StateFlow<Locale> = _currentLocale

    fun switchLanguage(code: String) {
        AppConfig.appLanguage = code
        _currentLocale.value = Locale(code)
    }

}


@SuppressLint("LocalContextConfigurationRead")
@Composable
fun LocalizedContext(content: @Composable (Context) -> Unit) {
    val locale by LanguageUtil.currentLocale.collectAsState()
    val context = LocalContext.current

    val localizedContext = remember(locale) {
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }

    content(localizedContext)
}


data class AppLangModel(
    val index: Int,
    val code: String,
    val name: String
)