package com.tungnk123.orpheus.ui.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SongScreen(
    modifier: Modifier = Modifier
) {
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
            Text(text = "Songs Screen")
        }
    }
}