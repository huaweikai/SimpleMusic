package com.byexoplayer.music.exo.callback

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.byexoplayer.music.others.Constants.NOTIFICATION_ID
import com.byexoplayer.music.services.MusicService
import com.google.android.exoplayer2.ui.PlayerNotificationManager

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/11  15:13
 * @Description : 文件描述
 */
class MusicPlayerNotificationListener(
    private val musicService: MusicService
):PlayerNotificationManager.NotificationListener {

    //通知取消功能
    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        //通知取消时，要做的动作
        musicService.apply {
            stopForeground(true)
            isForegroundService=false
            stopSelf()
        }
    }
    //通知发布
    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        musicService.apply {
            //如果是持续存在的，且不在后台
            if(ongoing &&!isForegroundService){
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext,this::class.java)
                )
                startForeground(NOTIFICATION_ID,notification)
                isForegroundService=true
            }
        }
    }
}