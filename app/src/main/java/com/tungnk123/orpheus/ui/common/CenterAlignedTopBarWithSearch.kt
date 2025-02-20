package com.tungnk123.orpheus.ui.common

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.tungnk123.orpheus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopBarWithSearch(
    title: String,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            IconButton(
                content = {
                    Icon(
                        Icons.Filled.Search,
                        null
                    )
                },
                onClick = onSearchClick
            )
        },
        title = {
            Crossfade(
                label = "title",
                targetState = title,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TopAppBarMinimalTitle { Text(it) }
                }
            }
        },
        actions = {
            IconButton(
                content = {
                    Icon(
                        Icons.Filled.MoreVert,
                        null
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = !expanded
                        },
                    ) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Refresh,
                                    stringResource(R.string.rescan),
                                )
                            },
                            text = {
                                Text(stringResource(R.string.rescan))
                            },
                            onClick = {
                                expanded = !expanded
                            }
                        )
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Settings,
                                    stringResource(R.string.settings),
                                )
                            },
                            text = {
                                Text(stringResource(R.string.settings))
                            },
                            onClick = {
                                expanded = !expanded
                            }
                        )
                    }
                },
                onClick = {
                    expanded = !expanded
                }
            )
        }
    )
}