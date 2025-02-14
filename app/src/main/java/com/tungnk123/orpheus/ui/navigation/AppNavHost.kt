package com.tungnk123.orpheus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.tungnk123.orpheus.ui.foryou.ForYouScreen
import com.tungnk123.orpheus.ui.songs.SongScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavigationRoute.ForYou.route
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        baseComposable(NavigationBarMetadataItem.ForYou) {
            ForYouScreen()
        }
        baseComposable(NavigationBarMetadataItem.Songs) {
            SongScreen()
        }
        baseComposable(NavigationBarMetadataItem.ForYou) {
            ForYouScreen()
        }

    }
}
