package com.byexoplayer.music.services


import android.app.PendingIntent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.byexoplayer.music.exo.MusicNotificationManager
import com.byexoplayer.music.exo.callback.MusicPlaySessionCallBack
import com.byexoplayer.music.exo.callback.MusicPlayerEventListener
import com.byexoplayer.music.exo.callback.MusicPlayerNotificationListener
import com.google.android.exoplayer2.SimpleExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/10  15:36
 * @Description : 文件描述
 */
@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var player: SimpleExoPlayer

    //当前播放音乐的相关信息
    var musicIndex = -1


    //播放会话，将播放状态返回ui
    lateinit var mSession: MediaSessionCompat

    private lateinit var notificationManager: MusicNotificationManager
    var isForegroundService = false


    companion object {
        //ui可能销毁，使用变量来保存播放列表
        var servicePlayList = arrayListOf<MediaSessionCompat.QueueItem>()
    }


    override fun onCreate() {
        super.onCreate()
        //跳转activity
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, 0)
        }
        //获取令牌，获取媒体会话信息，
        mSession = MediaSessionCompat(this, "MusicService").apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        val  mSessionCallback =MusicPlaySessionCallBack(this)
        mSession.setCallback(mSessionCallback)
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
        sessionToken = mSession.sessionToken

        //实例化通知管理器
        notificationManager =
            MusicNotificationManager(
                this,
                mSession.sessionToken,
                MusicPlayerNotificationListener(this)
            )
        //设置通知栏
        player.addListener(MusicPlayerEventListener(this))
        notificationManager.showNotification(player)

    }


    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot("MusicService", null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.detach()
        val list = servicePlayList.map {
            MediaBrowserCompat.MediaItem(
                it.description,
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            )
        }
        result.sendResult(list as MutableList<MediaBrowserCompat.MediaItem>?)
    }
}