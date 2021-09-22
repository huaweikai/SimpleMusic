package com.byexoplayer.music.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  23:24
 * @Description : 数据库
 */
@Database(entities = [MusicItem::class,AlbumItem::class,ArtistItem::class],version = 1,exportSchema = false)
@TypeConverters(Convert::class)
abstract class MusicRoomDataBase: RoomDatabase() {
    abstract fun getDao():MusicDao
}