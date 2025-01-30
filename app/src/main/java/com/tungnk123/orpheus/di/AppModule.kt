package com.tungnk123.orpheus.di

import android.app.Application
import androidx.room.Room
import com.tungnk123.orpheus.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, AppDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideSongDao(db: AppDatabase) = db.songDao()

    @Provides
    @Singleton
    fun providePlaylistDao(db: AppDatabase) = db.playlistDao()

}