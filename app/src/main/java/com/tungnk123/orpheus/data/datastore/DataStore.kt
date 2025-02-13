package com.tungnk123.orpheus.data.datastore

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.tungnk123.orpheus.utils.extensions.toEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

suspend fun <T> DataStore<Preferences>.getAsync(key: Preferences.Key<T>): T? =
    data.firstOrNull()?.get(key)

suspend fun <T> DataStore<Preferences>.getAsync(key: Preferences.Key<T>, defaultValue: T): T =
    data.firstOrNull()?.get(key) ?: defaultValue

fun <T> preferenceAsync(
    context: Context,
    key: Preferences.Key<T>,
    defaultValue: T
): ReadOnlyProperty<Any?, Flow<T>> =
    ReadOnlyProperty { _, _ -> context.dataStore.data.map { it[key] ?: defaultValue } }

inline fun <reified T : Enum<T>> enumPreferenceAsync(
    context: Context,
    key: Preferences.Key<String>,
    defaultValue: T
): ReadOnlyProperty<Any?, Flow<T>> =
    ReadOnlyProperty { _, _ -> context.dataStore.data.map { it[key]?.toEnum(defaultValue) ?: defaultValue } }

@Composable
fun <T> rememberPreference(
    key: Preferences.Key<T>,
    defaultValue: T
): MutableState<T> {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val state = produceState(initialValue = defaultValue, key) {
        context.dataStore.data
            .map { it[key] ?: defaultValue }
            .distinctUntilChanged()
            .collect { value = it }
    }

    return remember {
        object : MutableState<T> {
            override var value: T
                get() = state.value
                set(value) {
                    coroutineScope.launch {
                        context.dataStore.edit { it[key] = value }
                    }
                }

            override fun component1() = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

@Composable
inline fun <reified T : Enum<T>> rememberEnumPreference(
    key: Preferences.Key<String>,
    defaultValue: T
): MutableState<T> {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val state = produceState(initialValue = defaultValue, key) {
        context.dataStore.data
            .map { it[key]?.toEnum(defaultValue) ?: defaultValue }
            .distinctUntilChanged()
            .collect { value = it }
    }

    return remember {
        object : MutableState<T> {
            override var value: T
                get() = state.value
                set(value) {
                    coroutineScope.launch {
                        context.dataStore.edit { it[key] = value.name }
                    }
                }

            override fun component1() = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

suspend fun <T> Context.savePreference(key: Preferences.Key<T>, value: T) {
    dataStore.edit { it[key] = value }
}

suspend fun <T : Enum<T>> Context.saveEnumPreference(key: Preferences.Key<String>, value: T) {
    dataStore.edit { it[key] = value.name }
}
