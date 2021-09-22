package com.byexoplayer.music.others

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/12  0:10
 * @Description : 事件类
 */
//没有继承mediasession的事件，这很常见
open class Event<out T>(private val data:T) {

    //如果触发了事件，就把他设置为true
    var hasBeenHandled=false
     private set


    //如果已经触发过就返回null，如果没有触发过，就设置为true，并返回data
    fun getContentIfNotHandled():T?{
        return if(hasBeenHandled){
            null
        }else{
            hasBeenHandled=true
            data
        }
    }

    fun peekContent()=data
}