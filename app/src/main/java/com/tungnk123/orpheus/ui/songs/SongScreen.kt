package com.tungnk123.orpheus.ui.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tungnk123.orpheus.utils.extensions.printLog

@Composable
fun SongScreen(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel = hiltViewModel()
) {
    val songs by songViewModel.songs.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        songs.toString().printLog("test")
    }

    Scaffold(
        modifier = modifier
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        }
    }
}