package com.tungnk123.orpheus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.tungnk123.orpheus.ui.navigation.AppNavHost
import androidx.hilt.navigation.compose.hiltViewModel
import com.tungnk123.orpheus.ui.navigation.BottomNavigationBar
import com.tungnk123.orpheus.ui.navigation.NavigationBarMetadataItem

@Composable
fun OrpheusApp(
    modifier: Modifier = Modifier,
//    viewModel: MainViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        topBar = {},
        bottomBar = {
            BottomNavigationBar(
                currentTab = NavigationBarMetadataItem.ForYou.route,
                onTabSelected = {}
            )
        }
    ) { contentPaddings ->
        AppNavHost(
            navController,
            modifier = Modifier.padding(contentPaddings)
        )
    }
}