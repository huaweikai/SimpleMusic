package com.byexoplayer.music.exo.callback

import android.media.session.PlaybackState
import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import com.byexoplayer.music.others.Utils
import com.byexoplayer.music.services.MusicService
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/11  16:03
 * @Description : 音乐操作监听
 */
class MusicPlayerEventListener(
    private val musicService: MusicService
): Player.EventListener {
    //播放状态
    private var mState = PlaybackStateCompat.Builder().build()

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if(playbackState==Player.STATE_READY&&!playWhenReady){
            //不想多次创建notification
            musicService.stopForeground(false)
        }
        if (playWhenReady) {
            setNewState(PlaybackState.STATE_PLAYING)
        } else {
            setNewState(PlaybackState.STATE_PAUSED)
        }

    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService,"播放错误",Toast.LENGTH_SHORT).show()
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        val queueItem = MusicService.servicePlayList.find {
            it.description.mediaUri.toString() == mediaItem?.mediaId
        }
        musicService.mSession.setMetadata(queueItem?.let { Utils.mediaItemToMeta(it) })
        setNewState(PlaybackStateCompat.STATE_PLAYING)
    }
    private fun setNewState(state: Int) {
        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setActions(
            PlaybackStateCompat.ACTION_SEEK_TO
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY)
        stateBuilder.setState(
            state,
            musicService.player.currentPosition,
            1.0f,
            SystemClock.elapsedRealtime()
        )
        mState = stateBuilder.build()
        musicService.mSession.setPlaybackState(mState)
    }

}
