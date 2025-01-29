package com.tungnk123.orpheus.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Singleton
//    @Provides
//    fun provideMusicDatabase(songCollection: CollectionReference) =
//        MusicRemoteDatabase(songCollection)
//
//
//    @Singleton
//    @Provides
//    fun provideMusicRepository(
//        musicRemoteDatabase: MusicRemoteDatabase,
//    ): MusicRepository =
//        MusicRepositoryImpl(musicRemoteDatabase)
//
//    @Singleton
//    @Provides
//    fun provideMusicController(@ApplicationContext context: Context): MusicController =
//        MusicControllerImpl(context)
}