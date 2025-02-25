package com.tungnk123.orpheus.ui.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tungnk123.orpheus.data.repository.SongRepository
import com.tungnk123.orpheus.di.Dispatcher
import com.tungnk123.orpheus.di.OrpheusDispatchers
import com.tungnk123.orpheus.media.MediaStoreSongProvider
import com.tungnk123.orpheus.utils.AppConstants.SHARING_STARTED_STOP_TIMEOUT
import com.tungnk123.orpheus.utils.extensions.printLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val mediaStoreSongProvider: MediaStoreSongProvider,
    @Dispatcher(OrpheusDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val songs = songRepository.observeSongs()
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_STOP_TIMEOUT)
        )

    fun fetchSongsFromMediaStoreAndSaveToDatabase() {
        viewModelScope.launch(ioDispatcher) {
            try {
                mediaStoreSongProvider.getAllSongs()
                    .collect { songs ->
                        songRepository.insertSongs(songs)
                    }
            }
            catch (e: Exception) {
                e.toString().printLog(TAG)
            }
        }
    }

    companion object {
        private const val TAG = "SongViewModel"
    }
}