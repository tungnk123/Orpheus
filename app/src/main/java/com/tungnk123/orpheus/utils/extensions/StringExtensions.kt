package com.tungnk123.orpheus.utils.extensions

fun String.withCase(sensitive: Boolean) = if (!sensitive) lowercase() else this
