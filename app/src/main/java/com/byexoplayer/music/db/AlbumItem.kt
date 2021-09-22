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
 * @Time : 2021/9/9  17:00
 * @Description : 专辑module
 */
@Entity
@Parcelize
data class AlbumItem (
    @PrimaryKey
    val albumId: String,
    val artistName: String="",
    val songCount:Int=0,
    val albumTitle: String="",
    val year: Int,
    var albumImg: Uri
):Parcelable{
    object albumDiffer: DiffUtil.ItemCallback<AlbumItem>(){
        override fun areItemsTheSame(oldItem: AlbumItem, newItem: AlbumItem): Boolean {
            return newItem.albumId==oldItem.albumId
        }

        override fun areContentsTheSame(oldItem: AlbumItem, newItem: AlbumItem): Boolean {
            return newItem==oldItem
        }
    }
}