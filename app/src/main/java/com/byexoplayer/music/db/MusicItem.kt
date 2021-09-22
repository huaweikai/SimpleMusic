package com.byexoplayer.music.db

import android.net.Uri
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  11:55
 * @Description : musicModule类
 */
@Entity
@Parcelize
data class MusicItem (
    var title:String,
    var artist:String,
    @PrimaryKey
    var musicId:String,
    var uri:Uri,
    var duration:Long,
    var album_uri:Uri?=null
):Parcelable{
    object differ: DiffUtil.ItemCallback<MusicItem>(){
        override fun areItemsTheSame(oldItem: MusicItem, newItem: MusicItem): Boolean {
            return newItem.musicId==oldItem.musicId
        }

        override fun areContentsTheSame(oldItem: MusicItem, newItem: MusicItem): Boolean {
            return newItem==oldItem
        }
    }
}

