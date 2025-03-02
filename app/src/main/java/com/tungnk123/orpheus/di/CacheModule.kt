package com.tungnk123.orpheus.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.tungnk123.orpheus.data.datastore.MaxSongCacheSizeKey
import com.tungnk123.orpheus.data.datastore.dataStore
import com.tungnk123.orpheus.data.datastore.get
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
object CacheModule {

    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    @PlayerCache
    fun providePlayerCache(
        @ApplicationContext context: Context,
        databaseProvider: DatabaseProvider,
    ): SimpleCache {
        val constructor = {
            SimpleCache(
                context.filesDir.resolve("exoplayer"),
                when (val cacheSize = context.dataStore[MaxSongCacheSizeKey] ?: 1024) {
                    -1 -> NoOpCacheEvictor()
                    else -> LeastRecentlyUsedCacheEvictor(cacheSize * 1024 * 1024L)
                },
                databaseProvider,
            )
        }
        constructor().release()
        return constructor()
    }

    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    @DownloadCache
    fun provideDownloadCache(
        @ApplicationContext context: Context,
        databaseProvider: DatabaseProvider,
    ): SimpleCache {
        val constructor = {
            SimpleCache(
                context.filesDir.resolve("download"),
                NoOpCacheEvictor(),
                databaseProvider
            )
        }
        constructor().release()
        return constructor()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlayerCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadCache