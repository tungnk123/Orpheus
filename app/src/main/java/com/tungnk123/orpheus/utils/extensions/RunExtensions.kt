package com.tungnk123.orpheus.utils.extensions

fun <T> runIfOrDefault(value: Boolean, defaultValue: T, fn: () -> T) = when {
    value -> fn()
    else -> defaultValue
}

fun <T> T.runIfOrThis(value: Boolean, fn: T.() -> T) = when {
    value -> fn.invoke(this)
    else -> this
}
