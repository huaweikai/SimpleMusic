package com.byexoplayer.music.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.byexoplayer.music.R
import com.byexoplayer.music.base.BaseViewHolder
import com.byexoplayer.music.databinding.MusicItemsBinding
import com.byexoplayer.music.db.MusicItem

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  12:27
 * @Description : 列表适配器
 */
class SongMusicAdapter: ListAdapter<MusicItem, BaseViewHolder>(MusicItem.differ) {

    private var listener:((Int)->Unit)?=null
    fun setListener(listener:(Int)->Unit){
        this.listener=listener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder=BaseViewHolder(MusicItemsBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
        holder.itemView.setOnClickListener {
            listener?.let {
                it(holder.adapterPosition)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item=currentList[position]
        val view=holder.viewBinding as MusicItemsBinding
        view.apply {
            songTitle.text=item.title
            songArtist.text=item.artist
            songImg.load(item.album_uri){
                error(R.drawable.error)
                crossfade(true)
                transformations(RoundedCornersTransformation(5f))
            }
        }
    }

}