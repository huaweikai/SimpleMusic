package com.byexoplayer.music.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.byexoplayer.music.base.BaseViewHolder
import com.byexoplayer.music.databinding.ArtistItemBinding
import com.byexoplayer.music.db.ArtistItem

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/9  21:02
 * @Description : 艺术家recyclerview适配器
 */
class ArtistMusicAdapter: ListAdapter<ArtistItem, BaseViewHolder>(ArtistItem.differ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder=BaseViewHolder(ArtistItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        holder.itemView.setOnClickListener {

        }
        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item=currentList[position]
        val view=holder.viewBinding as ArtistItemBinding
        view.artistName.isSelected=true
        view.artistName.text=item.artistName
    }
}