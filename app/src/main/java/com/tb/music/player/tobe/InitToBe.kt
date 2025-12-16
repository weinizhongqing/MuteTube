package com.tb.music.player.tobe

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.datastore.preferences.core.edit
import com.tb.music.player.TB
import com.tb.music.player.tobe.models.YouTubeLocale
import com.tb.music.player.utils.AccountChannelHandleKey
import com.tb.music.player.utils.AccountEmailKey
import com.tb.music.player.utils.AccountNameKey
import com.tb.music.player.utils.ContentCountryKey
import com.tb.music.player.utils.ContentLanguageKey
import com.tb.music.player.utils.CountryCodeToName
import com.tb.music.player.utils.DataSyncIdKey
import com.tb.music.player.utils.InnerTubeCookieKey
import com.tb.music.player.utils.LanguageCodeToName
import com.tb.music.player.utils.ProxyEnabledKey
import com.tb.music.player.utils.ProxyTypeKey
import com.tb.music.player.utils.ProxyUrlKey
import com.tb.music.player.utils.SYSTEM_DEFAULT
import com.tb.music.player.utils.UseLoginForBrowse
import com.tb.music.player.utils.VisitorDataKey
import com.tb.music.player.utils.dataStore
import com.tb.music.player.utils.get
import com.tb.music.player.utils.toEnum
import com.tb.music.player.utils.toInetSocketAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.Proxy
import java.util.Locale

object InitToBe {


    fun initToBe(app: Application) {
        val locale = Locale.getDefault()
        val languageTag = locale.toLanguageTag().replace("-Hant", "") // replace zh-Hant-* to zh-*
        ToBe.locale = YouTubeLocale(
            gl = app.dataStore[ContentCountryKey]?.takeIf { it != SYSTEM_DEFAULT }
                ?: locale.country.takeIf { it in CountryCodeToName }
                ?: "US",
            hl = app.dataStore[ContentLanguageKey]?.takeIf { it != SYSTEM_DEFAULT }
                ?: locale.language.takeIf { it in LanguageCodeToName }
                ?: languageTag.takeIf { it in LanguageCodeToName }
                ?: "en"
        )

        if (app.dataStore[ProxyEnabledKey] == true) {
            try {
                ToBe.proxy = Proxy(
                    app.dataStore[ProxyTypeKey].toEnum(defaultValue = Proxy.Type.HTTP),
                    app.dataStore[ProxyUrlKey]!!.toInetSocketAddress()
                )
            } catch (e: Exception) {
                Toast.makeText(app, "Failed to parse proxy url.", LENGTH_SHORT).show()
                reportException(e)
            }
        }

        if (app.dataStore[UseLoginForBrowse] != false) {
            ToBe.useLoginForBrowse = true
        }

        TB.scope.launch {
            app.dataStore.data
                .map { it[VisitorDataKey] }
                .distinctUntilChanged()
                .collect { visitorData ->
                    ToBe.visitorData = visitorData
                        ?.takeIf { it != "null" } // Previously visitorData was sometimes saved as "null" due to a bug
                        ?: ToBe.visitorData().onFailure {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(app, "Failed to get visitorData.", LENGTH_SHORT).show()
                            }
                            reportException(it)
                        }.getOrNull()?.also { newVisitorData ->
                            app.dataStore.edit { settings ->
                                settings[VisitorDataKey] = newVisitorData
                            }
                        }
                }
        }
        TB.scope.launch {
            app.dataStore.data
                .map { it[DataSyncIdKey] }
                .distinctUntilChanged()
                .collect { dataSyncId ->
                    ToBe.dataSyncId = dataSyncId?.let {
                        /*
                         * Workaround to avoid breaking older installations that have a dataSyncId
                         * that contains "||" in it.
                         * If the dataSyncId ends with "||" and contains only one id, then keep the
                         * id before the "||".
                         * If the dataSyncId contains "||" and is not at the end, then keep the
                         * second id.
                         * This is needed to keep using the same account as before.
                         */
                        it.takeIf { !it.contains("||") }
                            ?: it.takeIf { it.endsWith("||") }?.substringBefore("||")
                            ?: it.substringAfter("||")
                    }
                }
        }
        TB.scope.launch {
            app.dataStore.data
                .map { it[InnerTubeCookieKey] }
                .distinctUntilChanged()
                .collect { cookie ->
                    try {
                        ToBe.cookie = cookie
                    } catch (e: Exception) {
                        // we now allow user input now, here be the demons. This serves as a last ditch effort to avoid a crash loop
                        Log.e("error", "existing cookie.${e.message}")
                        Toast.makeText(app, "Could not parse cookie. Clearing existing cookie.", LENGTH_SHORT).show()
                        forgetAccount(app)
                    }
                }
        }

    }

    fun forgetAccount(context: Context) {
        runBlocking {
            context.dataStore.edit { settings ->
                settings.remove(InnerTubeCookieKey)
                settings.remove(VisitorDataKey)
                settings.remove(DataSyncIdKey)
                settings.remove(AccountNameKey)
                settings.remove(AccountEmailKey)
                settings.remove(AccountChannelHandleKey)
            }
        }
    }


}