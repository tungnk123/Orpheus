package com.tungnk123.orpheus.ui.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tungnk123.orpheus.data.repository.SongRepository
import com.tungnk123.orpheus.di.Dispatcher
import com.tungnk123.orpheus.di.OrpheusDispatchers
import com.tungnk123.orpheus.utils.AppConstants.SHARING_STARTED_STOP_TIMEOUT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songRepository: SongRepository,
    @Dispatcher(OrpheusDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val songs = songRepository.observeSongs().stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(SHARING_STARTED_STOP_TIMEOUT)
    )

}