package com.byexoplayer.music.exo.callback

import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import com.byexoplayer.music.services.MusicService
import com.google.android.exoplayer2.MediaItem

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/22  11:24
 * @Description : 文件描述
 */
class MusicPlaySessionCallBack(val musicService: MusicService): MediaSessionCompat.Callback() {
    private val player=musicService.player
    override fun onAddQueueItem(description: MediaDescriptionCompat?, index: Int) {
        super.onAddQueueItem(description, index)
        if (MusicService.servicePlayList.find { it.description.mediaId == description?.mediaId } == null) {
            MusicService.servicePlayList.add(
                MediaSessionCompat.QueueItem(description, description.hashCode().toLong())
            )
        }
        musicService.musicIndex = index
        musicService.mSession.setQueue(MusicService.servicePlayList)
    }


    override fun onPlay() {
        super.onPlay()
        if(player.mediaItemCount==0){
            Toast.makeText(musicService,"未加载歌单,请任意点一首歌",Toast.LENGTH_SHORT).show()
            return
        }
        player.play()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        if (player.hasNext()) {
            player.next()
        } else {
            player.seekTo(0, 0)
        }
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        if (player.hasPrevious()) {
            player.previous()
        } else {
            player.seekTo(MusicService.servicePlayList.size - 1, 0)
        }
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        player.seekTo(pos)
    }

    override fun onPrepare() {
        super.onPrepare()
        player.clearMediaItems()
        MusicService.servicePlayList.forEach {
            player.addMediaItem(MediaItem.fromUri(it.description.mediaUri!!))
        }
        player.prepare()
        player.seekTo(musicService.musicIndex, 0)
        player.play()
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
        //根据我写的列表，不太好找id，直接传过来序号
        mediaId?.toInt()?.let {
            player.seekTo(it, 0)
            player.play()
        }
    }

}