package com.tungnk123.orpheus.utils.extensions

import android.util.Log

fun String.withCase(sensitive: Boolean) = if (!sensitive) lowercase() else this

enum class LogLevel {
    VERBOSE, DEBUG, INFO, WARN, ERROR
}

fun String.printLog(tag: String = "Orpheus_Log", logLevel: LogLevel = LogLevel.DEBUG) {
    when (logLevel) {
        LogLevel.VERBOSE -> Log.v(tag, this)
        LogLevel.DEBUG -> Log.d(tag, this)
        LogLevel.INFO -> Log.i(tag, this)
        LogLevel.WARN -> Log.w(tag, this)
        LogLevel.ERROR -> Log.e(tag, this)
    }
}
