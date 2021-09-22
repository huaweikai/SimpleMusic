package com.byexoplayer.music.others

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/11  23:46
 * @Description : 文件描述
 */
//以后可以尝试使用密封类来达到相同效果
data class Resource<out T>(val status:Status,val data:T?,val message:String?){
    companion object{
        fun<T> success(data:T?)=Resource(Status.SUCCESS,data,null)
        //可选性的，可能会有数据传送
        fun<T> error(message: String,data:T?)=Resource(Status.ERROR,data,message)
        fun<T> loading(data:T?)=Resource(Status.LOADING,data,null)
    }
}

//资源状态枚举
enum class Status{
    SUCCESS,
    ERROR,
    LOADING
}