package com.tungnk123.orpheus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tungnk123.orpheus.helper.PermissionHandler
import com.tungnk123.orpheus.ui.OrpheusApp
import com.tungnk123.orpheus.ui.theme.OrpheusTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionHandler: PermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        permissionHandler.handle(this)
        setContent {
            OrpheusTheme {
                OrpheusApp()
            }
        }
    }
}