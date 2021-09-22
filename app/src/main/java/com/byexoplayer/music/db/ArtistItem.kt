package com.byexoplayer.music.db

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/9  20:25
 * @Description : 艺术家module
 */
@Entity
data class ArtistItem (
    var albumCount:Int,
    @PrimaryKey
    var artistId:String,
    var artistName:String,
    var songCount:Int
){
    object differ: DiffUtil.ItemCallback<ArtistItem>(){
        override fun areItemsTheSame(oldItem: ArtistItem, newItem: ArtistItem): Boolean {
            return oldItem.artistId==newItem.artistId
        }

        override fun areContentsTheSame(oldItem: ArtistItem, newItem: ArtistItem): Boolean {
            return oldItem==newItem
        }
    }
}