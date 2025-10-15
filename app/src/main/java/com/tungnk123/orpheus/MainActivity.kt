package com.tungnk123.orpheus

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.tungnk123.orpheus.data.repository.SongRepository
import com.tungnk123.orpheus.helper.PermissionHandler
import com.tungnk123.orpheus.playback.LocalPlayerConnection
import com.tungnk123.orpheus.playback.PlayerConnection
import com.tungnk123.orpheus.ui.OrpheusApp
import com.tungnk123.orpheus.ui.common.LoadingScreen
import com.tungnk123.orpheus.ui.theme.OrpheusTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.UnstableApi
import javax.inject.Inject

@OptIn(androidx.media3.common.util.UnstableApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionHandler: PermissionHandler

    @Inject
    lateinit var songRepository: SongRepository

    private var playerConnection by mutableStateOf<PlayerConnection?>(null)
    private val serviceConnection =
        object : ServiceConnection {
            @OptIn(androidx.media3.common.util.UnstableApi::class)
            override fun onServiceConnected(
                name: ComponentName?,
                service: IBinder?,
            ) {
                if (service is MusicService.MusicBinder) {
                    playerConnection =
                        PlayerConnection(
                            binder = service,
                            scope = lifecycleScope,
                            songRepository = songRepository
                        )
                }
            }

            @OptIn(androidx.media3.common.util.UnstableApi::class)
            override fun onServiceDisconnected(name: ComponentName?) {
                playerConnection?.dispose()
                playerConnection = null
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHandler.handle(this)

        setContent {
            OrpheusTheme {
                val connection = playerConnection
                if (connection != null) {
                    CompositionLocalProvider(LocalPlayerConnection provides connection) {
                        OrpheusApp()
                    }
                }
                else {
                    LoadingScreen()
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onStart() {
        super.onStart()
        startService(
            Intent(
                this,
                MusicService::class.java
            )
        )
        bindService(
            Intent(
                this,
                MusicService::class.java
            ),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
    }

    @SuppressLint("ImplicitSamInstance")
    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        super.onDestroy()
        stopService(
            Intent(
                this,
                MusicService::class.java
            )
        )
        if (playerConnection != null) {
            unbindService(serviceConnection)
        }
        playerConnection = null
    }
}