package com.byexoplayer.music.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil.load
import com.byexoplayer.music.R
import com.byexoplayer.music.databinding.FragmentMusicBinding
import com.byexoplayer.music.ui.viewmodels.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  23:33
 * @Description : 主界面
 */

@AndroidEntryPoint
class MusicFragment : Fragment() {
    private var _bind:FragmentMusicBinding?=null
    private val bind get() = _bind!!
    private val viewModel:MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind= FragmentMusicBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initEvent()
        initView()
    }

    private fun initView() {
        viewModel.mMetaDataLiveData.observe(viewLifecycleOwner,{
            bind.musicController.exoIcon.load(it.description.iconUri){
                error(R.drawable.error)
            }
            bind.musicController.exoTitle.text=it.description.title
        })
        viewModel.mPlayStateLiveData.observe(viewLifecycleOwner,{
            if(it.state==PlaybackStateCompat.STATE_PAUSED){
                bind.musicController.exoPlayPause.setImageResource(R.drawable.exo_controls_play)
            }else if(it.state==PlaybackStateCompat.STATE_PLAYING){
                bind.musicController.exoPlayPause.setImageResource(R.drawable.exo_controls_pause)
            }
        })
    }

    private fun initEvent() {
        bind.musicController.exoPlayPause.setOnClickListener {
            viewModel.playOrPause(null,null,true)
        }
        bind.musicController.exoNext.setOnClickListener {
            viewModel.skipToNext()
        }
        bind.musicController.exoPrev.setOnClickListener {
            viewModel.skipToPrevious()
        }
        bind.musicController.bottomController.setOnClickListener {
            findNavController().navigate(R.id.action_musicFragment_to_playFragment)
        }
    }

    private fun initViewPager() {
        bind.mainViewpager.adapter=object :FragmentStateAdapter(this){
            override fun getItemCount()=3
            override fun createFragment(position: Int)=when(position){
                0->SongFragment()
                1->AlbumFragment()
                else->ArtistFragment()
            }
        }
        TabLayoutMediator(bind.mainTabLayout,bind.mainViewpager){tab,position->
            tab.text=when(position){
                0->"Song"
                1->"Album"
                else->"Artist"
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind=null
    }
}