package com.byexoplayer.music.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  13:06
 * @Description : Application
 */

@HiltAndroidApp
class BaseApplication: Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext:Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext=baseContext
    }
}