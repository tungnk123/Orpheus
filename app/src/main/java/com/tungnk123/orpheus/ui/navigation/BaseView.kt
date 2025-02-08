package com.tungnk123.orpheus.ui.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tungnk123.orpheus.MainActivity
import com.tungnk123.orpheus.ui.theme.OrpheusTheme

@Composable
fun BaseView(activity: MainActivity) {
    val navController = rememberNavController()

    OrpheusTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NavHost(
                navController = navController,
                startDestination = NavigationRoute.ForYou,
            ) {
                baseComposable<NavigationRoute.ForYou> {
//                    NavigationRoute.ForYou(context)
                }

            }
        }
    }
}