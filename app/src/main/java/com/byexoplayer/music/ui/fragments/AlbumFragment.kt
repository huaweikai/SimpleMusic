package com.byexoplayer.music.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.byexoplayer.music.R
import com.byexoplayer.music.adapters.AlbumMusicAdapter
import com.byexoplayer.music.databinding.FragmentAlbumBinding
import com.byexoplayer.music.others.Constants.POST_ALBUM_DATA
import com.byexoplayer.music.others.Constants.POST_MUSIC_BY_ALBUM
import com.byexoplayer.music.others.Utils
import com.byexoplayer.music.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  23:33
 * @Description : 专辑分类
 */

@AndroidEntryPoint
class AlbumFragment : Fragment() {
    private var _bind:FragmentAlbumBinding?=null
    private val bind get()= _bind!!
    private val albumAdapter=AlbumMusicAdapter()
    private val viewModel:MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind= FragmentAlbumBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.albumList.observe(requireActivity(),{
            albumAdapter.submitList(it)
        })
    }

    private fun initView() {
        bind.albumRv.apply {
            adapter=albumAdapter
            layoutManager=GridLayoutManager(requireContext(),2)
        }
        albumAdapter.setListener {
            val list=Utils.getAudioByAlbum(requireContext(),it.albumId)
            Bundle().apply {
                putParcelableArrayList(POST_MUSIC_BY_ALBUM,list)
                putParcelable(POST_ALBUM_DATA,it)
                findNavController().navigate(R.id.action_musicFragment_to_albumDetailFragment,this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind=null
    }
}