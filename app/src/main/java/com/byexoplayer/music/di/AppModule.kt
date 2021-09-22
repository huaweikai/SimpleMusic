package com.byexoplayer.music.di

import android.content.Context
import androidx.room.Room
import com.byexoplayer.music.db.MusicDao
import com.byexoplayer.music.db.MusicRoomDataBase
import com.byexoplayer.music.others.Constants.DATABASE_NAME
import com.byexoplayer.music.repostiories.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  13:18
 * @Description : hilt注入全局单例
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Provides
    @Singleton
    fun provideRoomDataBase(
        @ApplicationContext context: Context
    )= Room.databaseBuilder(context,MusicRoomDataBase::class.java,DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideRoomDao(musicRoomDataBase: MusicRoomDataBase)=
        musicRoomDataBase.getDao()

    @Singleton
    @Provides
    fun provideMusicRepository(
        dao: MusicDao
    )=MusicRepository(dao)

}