package com.tungnk123.orpheus.di

import com.tungnk123.orpheus.data.dao.SongDao
import com.tungnk123.orpheus.data.datasource.LocalSongDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideLocalSongDataSource(songDao: SongDao) = LocalSongDataSource(songDao)

}