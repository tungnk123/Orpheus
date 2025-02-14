package com.tungnk123.orpheus.ui

import androidx.lifecycle.ViewModel
import com.tungnk123.orpheus.ui.navigation.NavigationBarMetadataItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val homeTabs = listOf(
        NavigationBarMetadataItem.ForYou,
        NavigationBarMetadataItem.Songs,
        NavigationBarMetadataItem.Playlists,
        NavigationBarMetadataItem.Albums,
        NavigationBarMetadataItem.Artists,
    )

    private val _currentTab: MutableStateFlow<NavigationBarMetadataItem> =
        MutableStateFlow(NavigationBarMetadataItem.ForYou)

    val currentTab = _currentTab.asStateFlow()

    fun updateCurrentTab(tab: NavigationBarMetadataItem) {
        _currentTab.update { tab }
    }
}