package com.byexoplayer.music.di

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/11  14:01
 * @Description : 文件描述
 */
@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    //获取音乐的一些元数据，属性
    @ServiceScoped
    @Provides
    fun provideAudioAttributes()= AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    //将上面的元数据属性添加
    @ServiceScoped
    @Provides
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    )= SimpleExoPlayer.Builder(context).build().apply {
        //设置
        setAudioAttributes(audioAttributes,true)
        //设置监听，当插入耳机，暂停播放
        setHandleAudioBecomingNoisy(true)
    }

    //数据源工厂，实际来使用他来提供音乐源
    @ServiceScoped
    @Provides
    fun provideDataSourceFactory(
        @ApplicationContext context: Context
    )= DefaultDataSourceFactory(context, Util.getUserAgent(context,"EasyMusic App"))
}