package com.tb.music.player.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tb.music.player.R
import com.tb.music.player.ui.theme.Color0C0D0E
import com.tb.music.player.ui.theme.Color6A6F87
import com.tb.music.player.utils.LocalizedContext

@Composable
fun AppRoot() {

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: "home"

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            CustomBottomNavBar(
                selected = currentRoute,
                onSelect = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        modifier = Modifier.navigationBarsPadding()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen() }
            composable("list") { MusicListScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}


@Composable
fun CustomBottomNavBar(
    selected: String,
    onSelect: (String) -> Unit
) {

    LocalizedContext { context1 ->


        val items = listOf(
            BottomItem(
                "home",
                context1.getString(R.string.home),
                R.drawable.img_home_select,
                R.drawable.img_home_un_select
            ),
            BottomItem(
                "list",
                context1.getString(R.string.my_music),
                R.drawable.img_list_select,
                R.drawable.img_list_un_select
            ),
            BottomItem(
                "settings",
                context1.getString(R.string.settings),
                R.drawable.img_settings_select,
                R.drawable.img_settings_un_select
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color0C0D0E),   // 底部栏背景
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {


            items.forEach { item ->
                val isSelected = selected == item.route
                Box(
                    modifier = Modifier
                        .clickable {
                            if (!isSelected) onSelect(item.route)
                        }

                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Log.d("CustomBottomNavBar", "isSelected: $isSelected")
                        Image(
                            painter = painterResource(id = if (isSelected) item.select else item.unselect),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.label,
                            color = if (isSelected) Color.White else Color6A6F87,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}


data class BottomItem(
    val route: String,
    val label: String,
    val select: Int,
    val unselect: Int
)

