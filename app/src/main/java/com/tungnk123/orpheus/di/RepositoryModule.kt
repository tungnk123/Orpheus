package com.tungnk123.orpheus.di

import com.tungnk123.orpheus.data.repository.SongRepository
import com.tungnk123.orpheus.data.repository.SongRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindSongRepository(songRepositoryImpl: SongRepositoryImpl): SongRepository

}