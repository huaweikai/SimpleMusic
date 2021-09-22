package com.byexoplayer.music.exo

import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/12  1:54
 * @Description : 文件描述
 */
//处于以下的任何一个状态，播放器都在一个准备好的状态
inline val PlaybackStateCompat.isPrepared
    get() = state==PlaybackStateCompat.STATE_BUFFERING||
            state==PlaybackStateCompat.STATE_PLAYING||
            state==PlaybackStateCompat.STATE_PAUSED

inline val PlaybackStateCompat.isPlaying
    get() = state==PlaybackStateCompat.STATE_BUFFERING||
            state==PlaybackStateCompat.STATE_PLAYING

//检查是否弃用了播放选项，证明我们已经在播放音乐了
inline val PlaybackStateCompat.isPlayEnabled
    get() = actions and PlaybackStateCompat.ACTION_PLAY!=0L||
            (actions and PlaybackStateCompat.ACTION_PLAY_PAUSE!=0L &&
                    state==PlaybackStateCompat.STATE_PAUSED)

inline val PlaybackStateCompat.currentPlaybackPosition: Long
    get() = if(state == PlaybackStateCompat.STATE_PLAYING) {
        val timeDelta = SystemClock.elapsedRealtime() - lastPositionUpdateTime
        (position + (timeDelta * playbackSpeed)).toLong()
    } else position