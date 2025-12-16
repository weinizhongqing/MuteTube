package com.tb.music.player.ui.router

import androidx.navigation.NavController
import com.tb.music.player.music.db.info.PlayListInfo

object Router {

    object Route {
        const val START = "start/{type}"
        const val MAIN = "main"
        const val LANGUAGE = "language"
        const val SCAN_LOCAL_MUSIC = "scan_local_music"
        const val PLAY_MUSIC = "play_music/{type}"
        const val MUST_SONGS = "must_songs"
        const val NO_SINGLE = "no_single"
        const val NO_SINGER = "no_singer"
        const val NO_SINGER_LIST = "no_singer_list"
        const val NO_ALBUM = "no_album"
        const val NO_ALBUM_LIST = "no_album_list"

        const val EQUALIZER = "equalizer"

        const val PLAY_LIST_DESC = "play_list_desc"


        fun start(type: Boolean) = "start/$type"

        fun playMusic(type: Boolean) = "play_music/$type"

        fun navigateNoSingerList(info: PlayListInfo) {
            navigate(NO_SINGER_LIST)
            val backStackEntry = navController?.getBackStackEntry(NO_SINGER_LIST)
            backStackEntry?.savedStateHandle?.set("info", info)
        }

        fun navigateNoAlbumList(info: PlayListInfo) {
            navigate(NO_ALBUM_LIST)
            val backStackEntry = navController?.getBackStackEntry(NO_ALBUM_LIST)
            backStackEntry?.savedStateHandle?.set("info", info)
        }

        fun navigatePlayListDesc(info: PlayListInfo) {
            navigate(PLAY_LIST_DESC)
            val backStackEntry = navController?.getBackStackEntry(PLAY_LIST_DESC)
            backStackEntry?.savedStateHandle?.set("info", info)
        }



    }

    private var navController: NavController? = null

    fun bind(navController: NavController) {
        this.navController = navController
    }

    fun navigate(
        route: String,
        popUpToRoute: String? = null,
        inclusive: Boolean = false
    ) {
        navController?.navigate(route) {
            popUpToRoute?.let {
                popUpTo(it) { this.inclusive = inclusive }
            }
        }
    }

    fun back() {
        navController?.popBackStack()
    }

    fun backTo(route: String, inclusive: Boolean = false) {
        navController?.popBackStack(route, inclusive)
    }

}