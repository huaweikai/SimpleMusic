package com.byexoplayer.music.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import coil.load
import com.byexoplayer.music.R
import com.byexoplayer.music.base.BaseViewHolder
import com.byexoplayer.music.databinding.AlbumItemsBinding
import com.byexoplayer.music.db.AlbumItem

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/9  15:24
 * @Description : 专辑recyclerview适配器
 */
class AlbumMusicAdapter: ListAdapter<AlbumItem, BaseViewHolder>(AlbumItem.albumDiffer) {
    private var listener:((AlbumItem)->Unit)?=null
    fun setListener(listener:(AlbumItem)->Unit){
        this.listener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder=BaseViewHolder(
            AlbumItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false))
        holder.itemView.setOnClickListener {
            listener?.let {
                val found=currentList[holder.adapterPosition]
                it(found)
            }
        }
        return holder

    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item=currentList[position]
        val view=holder.viewBinding as AlbumItemsBinding
        view.apply {
            albumName.text=item.albumTitle
            albumArtist.text=item.artistName
            albumArt.load(item.albumImg){
                error(R.drawable.error)
                crossfade(true)
            }
        }
    }
}