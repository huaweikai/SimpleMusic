package com.byexoplayer.music.ui.viewmodels

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.byexoplayer.music.db.AlbumItem
import com.byexoplayer.music.db.ArtistItem
import com.byexoplayer.music.db.MusicItem
import com.byexoplayer.music.exo.*
import com.byexoplayer.music.repostiories.MusicRepository
import com.byexoplayer.music.services.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  23:14
 * @Description : viewModel类
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private lateinit var vContext: Context

    //播放控制器，对service发出操作
    private lateinit var mMediaControllerCompat: MediaControllerCompat

    //媒体浏览器，接受service的回调
    private lateinit var mMediaBrowserCompat: MediaBrowserCompat

    //播放状态的数据，是否播放，播放进度
    var mPlayStateLiveData = MutableLiveData<PlaybackStateCompat>()

    //播放歌曲的数据（歌曲，歌手等等）
    var mMetaDataLiveData = MutableLiveData<MediaMetadataCompat>()

    //播放列表数据
    var mMusicLiveData = MutableLiveData<MutableList<MediaDescriptionCompat>>()

    private val _curPlayPosition= MutableLiveData<Long>()
    val curPlayPosition:LiveData<Long> =_curPlayPosition

    //播放控制器回调
    private var mMediaControllerCompatCallback = object : MediaControllerCompat.Callback() {
        //service的歌曲发生改变
        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            mMusicLiveData.postValue(queue?.map { it.description } as MutableList<MediaDescriptionCompat>)
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            mPlayStateLiveData.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            mMetaDataLiveData.postValue(metadata)
        }

        override fun onSessionReady() {
            super.onSessionReady()
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
        }
    }

    private var mMediaBrowserCompatConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                //链接成功
                mMediaControllerCompat =
                    MediaControllerCompat(vContext, mMediaBrowserCompat.sessionToken)
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback)
                mMediaBrowserCompat.subscribe(
                    mMediaBrowserCompat.root,
                    mMediaBrowserCompatSubscription
                )
            }

            override fun onConnectionSuspended() {
                super.onConnectionSuspended()
            }

            override fun onConnectionFailed() {
                super.onConnectionFailed()
            }
        }
    private var mMediaBrowserCompatSubscription =
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
            }
        }

    init {
        updateCurrentPlayerPosition()
    }
    fun init(context: Context) {
        vContext = context
        mMediaBrowserCompat = MediaBrowserCompat(
            context,
            ComponentName(context, MusicService::class.java),
            mMediaBrowserCompatConnectionCallback, null
        )
        mMediaBrowserCompat.connect()
    }

    val musicList = repository.getAllMusic()
    val artistList = repository.getAllArtist()
    val albumList = repository.getAllAlbum()


    fun skipToPrevious() {
        mMediaControllerCompat.transportControls.skipToPrevious()
    }

    fun skipToNext() {
        mMediaControllerCompat.transportControls.skipToNext()
    }

    fun seekTo(duration:Long){
        mMediaControllerCompat.transportControls.seekTo(duration)
    }

    fun playOrPause(musicItem: MusicItem?, duration: Int?, toggle: Boolean) {
        if (toggle) {
            if (mPlayStateLiveData.value?.state == PlaybackStateCompat.STATE_PLAYING) {
                mMediaControllerCompat.transportControls.pause()
            } else {
                mMediaControllerCompat.transportControls.play()
            }
        } else {
            if (musicItem!!.musicId == mMetaDataLiveData.value?.description?.mediaId) {
                if (mPlayStateLiveData.value?.state == PlaybackStateCompat.STATE_PLAYING) {
                    mMediaControllerCompat.transportControls.pause()
                } else {
                    mMediaControllerCompat.transportControls.play()
                }
            } else {
                mMediaControllerCompat.transportControls.playFromMediaId(duration.toString(), null)
            }
        }


    }

    fun prepare() {
        mMediaControllerCompat.transportControls.prepare()
    }

    fun setMusicList(list: List<MusicItem>, index: Int) {
        fetchMediaData(list).forEach {
            val bundle = Bundle().also { bundle ->
                bundle.putLong("duration", it.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))
            }
            val descriptionCompat = MediaDescriptionCompat.Builder()
                .setExtras(bundle)
                .setIconUri(it.description.iconUri)
                .setMediaUri(it.description.mediaUri)
                .setTitle(it.description.title)
                .setSubtitle(it.description.subtitle)
                .setMediaId(it.description.mediaId)
                .build()
            mMediaControllerCompat.addQueueItem(descriptionCompat, index)
        }
    }

    private fun updateCurrentPlayerPosition(){
        viewModelScope.launch {
            while (true){
                val pos=mPlayStateLiveData.value?.currentPlaybackPosition
                if(curPlayPosition.value!=pos){
                    _curPlayPosition.postValue(pos)
                }
                delay(500L)
            }
        }
    }

    private fun fetchMediaData(list: List<MusicItem>): List<MediaMetadataCompat> {
        var songs = emptyList<MediaMetadataCompat>()
        list.let {
            songs = it.map { song ->
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
                    .putString(METADATA_KEY_MEDIA_ID, song.musicId)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.title)
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                        song.album_uri.toString()
                    )
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.uri.toString())
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                        song.album_uri.toString()
                    )
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, song.artist)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, song.artist)
                    .build()

            }
        }
        return songs
    }

    //先对比再添加
    fun comMusicList(
        musicItems: List<MusicItem>,
        artistItems: List<ArtistItem>,
        albumItems: List<AlbumItem>
    ) {
        val scanMusicId: ArrayList<String> = arrayListOf()
        val scanArtistId: ArrayList<String> = arrayListOf()
        val scanAlbumId: ArrayList<String> = arrayListOf()
        musicItems.forEach {
            scanMusicId.add(it.musicId)
        }
        artistItems.forEach {
            scanArtistId.add(it.artistId)
        }
        albumItems.forEach {
            scanAlbumId.add(it.albumId)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val musicIdList = repository.getMusicId()
            val albumIdList = repository.getAlbumId()
            val artistIdList = repository.getArtistId()
            musicIdList.forEach {
                if (it !in scanMusicId) {
                    repository.comMusic(it)
                }
            }
            albumIdList.forEach {
                if (it !in scanMusicId) {
                    repository.comAlbum(it)
                }
            }
            artistIdList.forEach {
                if (it !in scanMusicId) {
                    repository.comArtist(it)
                }
            }
        }
    }

    fun scanAllMusic(
        musicItems: List<MusicItem>,
        artistItems: List<ArtistItem>,
        albumItems: List<AlbumItem>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            for (item in musicItems) {
                if (!repository.updateMusic(item.musicId)) {
                    repository.insertMusic(item)
                }
            }
            for (item in artistItems) {
                repository.insertArtist(item)
            }
            for (item in albumItems) {
                repository.insertAlbum(item)
            }
        }
    }
}