package com.byexoplayer.music.repostiories

import com.byexoplayer.music.db.AlbumItem
import com.byexoplayer.music.db.ArtistItem
import com.byexoplayer.music.db.MusicDao
import com.byexoplayer.music.db.MusicItem

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  23:33
 * @Description : 仓库类，用于viewModel与数据之间
 */
class MusicRepository(
    private val dao: MusicDao
) {
    fun getAllMusic()=dao.getAllMusic()

    fun getAllArtist()=dao.getAllArtist()

    fun getAllAlbum()=dao.getAllAlbum()

    suspend fun insertMusic(musicItem: MusicItem){
        dao.insertMusic(musicItem)
    }

    suspend fun insertArtist(artistItem: ArtistItem){
        dao.insertArtist(artistItem)
    }

    suspend fun insertAlbum(albumItem: AlbumItem){
        dao.insertAlbum(albumItem)
    }

    fun updateMusic(id:String)=dao.updateMusic(id)

    fun getMusicId()=dao.selectMusic()

    suspend fun comMusic(id: String){
        dao.comMusicItem(id)
    }

    fun getArtistId()=dao.selectArtist()

    suspend fun comArtist(id: String){
        dao.comArtistItem(id)
    }

    fun getAlbumId()=dao.selectAlbum()

    suspend fun comAlbum(id: String){
        dao.comAlbumItem(id)
    }

}