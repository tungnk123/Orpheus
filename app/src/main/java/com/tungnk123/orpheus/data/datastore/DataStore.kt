package com.tungnk123.orpheus.data.datastore

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.tungnk123.orpheus.utils.extensions.toEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.ReadOnlyProperty

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

operator fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? =
    runBlocking(Dispatchers.IO) {
        data.first()[key]
    }

fun <T> DataStore<Preferences>.get(
    key: Preferences.Key<T>,
    defaultValue: T,
): T =
    runBlocking(Dispatchers.IO) {
        data.first()[key] ?: defaultValue
    }

suspend fun <T> DataStore<Preferences>.getAsync(key: Preferences.Key<T>): T? =
    data.firstOrNull()
        ?.get(key)

suspend fun <T> DataStore<Preferences>.getAsync(
    key: Preferences.Key<T>,
    defaultValue: T
): T =
    data.firstOrNull()
        ?.get(key) ?: defaultValue

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
    ReadOnlyProperty { _, _ ->
        context.dataStore.data.map {
            it[key]?.toEnum(defaultValue) ?: defaultValue
        }
    }

@Composable
fun <T> rememberPreference(
    key: Preferences.Key<T>,
    defaultValue: T
): MutableState<T> {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val state = produceState(
        initialValue = defaultValue,
        key
    ) {
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

    val state = produceState(
        initialValue = defaultValue,
        key
    ) {
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

suspend fun <T> Context.savePreference(
    key: Preferences.Key<T>,
    value: T
) {
    dataStore.edit { it[key] = value }
}

suspend fun <T : Enum<T>> Context.saveEnumPreference(
    key: Preferences.Key<String>,
    value: T
) {
    dataStore.edit { it[key] = value.name }
}
