package com.byexoplayer.music.db

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  23:19
 * @Description : Dao
 */
@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusic(musicItem: MusicItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(albumItem: AlbumItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artistItem: ArtistItem)



    @Delete
    suspend fun deleteMusic(vararg musicItem: MusicItem)

    @Query("SELECT * FROM MusicItem ORDER BY title")
    fun getAllMusic():LiveData<List<MusicItem>>

    @Query("SELECT * FROM MusicItem ORDER BY title")
    fun getAllMusiclist():List<MusicItem>


    @Query("SELECT * FROM ArtistItem ORDER BY artistName")
    fun getAllArtist():LiveData<List<ArtistItem>>

    @Query("SELECT * FROM AlbumItem ORDER BY albumTitle")
    fun getAllAlbum():LiveData<List<AlbumItem>>


    @Query("SELECT musicId FROM MusicItem WHERE :id=musicId")
    fun updateMusic(id:String):Boolean

    //对比，删除本地不存在的音乐，防止被用户点击
    @Query("SELECT musicId FROM MusicItem")
    fun selectMusic():List<String>

    @Query("DELETE FROM MusicItem WHERE :id=musicId")
    suspend fun comMusicItem(id:String)


    //对比，删除本地不存在的专辑，防止被用户点击
    @Query("SELECT albumId FROM AlbumItem")
    fun selectAlbum():List<String>

    @Query("DELETE FROM albumitem WHERE :id=albumId")
    suspend fun comAlbumItem(id:String)

    //对比，删除本地不存在的专辑，防止被用户点击
    @Query("SELECT artistId FROM ArtistItem")
    fun selectArtist():List<String>

    @Query("DELETE FROM artistitem WHERE :id=artistId")
    suspend fun comArtistItem(id:String)
}