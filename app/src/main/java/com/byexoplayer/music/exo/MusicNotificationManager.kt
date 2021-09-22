package com.byexoplayer.music.exo

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.byexoplayer.music.R
import com.byexoplayer.music.others.Constants.NOTIFICATION_CHANNEL_ID
import com.byexoplayer.music.others.Constants.NOTIFICATION_ID
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/11  14:22
 * @Description : 文件描述
 */
class MusicNotificationManager(
    private val context: Context,
    sessionToken:MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
){
    private val notificationManager:PlayerNotificationManager
    val imageLoader = ImageLoader(context)

    init {

        val mediaController=MediaControllerCompat(context,sessionToken)
        notificationManager= PlayerNotificationManager.createWithNotificationChannel(
            context,
            NOTIFICATION_CHANNEL_ID,
            R.string.notification_channel_name,
            R.string.notification_channel_description,
            NOTIFICATION_ID,
            DescriptionAdapter(mediaController),
            notificationListener
        ).apply {
            setSmallIcon(R.drawable.ic_music_icon)
            //让通知manager来访问我们当前的媒体会话
            setMediaSessionToken(sessionToken)
        }
    }

    fun showNotification(player: Player){
        notificationManager.setPlayer(player)
    }




    //用来控制媒体的我们还需要将session给这个，我们可以用它控制当前音乐，并且在服务中也有这样
    //服务链接中有这样的媒体控制器
    private inner class DescriptionAdapter(
        private val mediaControllerCompat: MediaControllerCompat
    ):PlayerNotificationManager.MediaDescriptionAdapter{
        //获取当前内容和标题函数
        override fun getCurrentContentTitle(player: Player): CharSequence {
            //在此我们只想返回播放歌曲的标题给我们的媒体
            return mediaControllerCompat.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            //需要我们的活动未决意图,这个我们在service中已经设置了
            return mediaControllerCompat.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return mediaControllerCompat.metadata.description.subtitle
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val request=ImageRequest.Builder(context)
                .data(mediaControllerCompat.metadata.description.iconUri)
                .target(
                    onSuccess = {
                        callback.onBitmap(it.toBitmap())
                    },
                    onError = {
                        callback.onBitmap(BitmapFactory.decodeResource(context.resources,
                            R.drawable.error
                        ))
                    }
                )
                .build()
            imageLoader.enqueue(request)
            return null
        }
    }
}