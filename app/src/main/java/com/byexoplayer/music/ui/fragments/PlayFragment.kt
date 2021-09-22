package com.byexoplayer.music.ui.fragments

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import coil.load
import coil.transform.BlurTransformation
import coil.transform.RoundedCornersTransformation
import com.byexoplayer.music.R
import com.byexoplayer.music.databinding.FragmentPlayBinding
import com.byexoplayer.music.ui.viewmodels.MainViewModel
import com.gyf.immersionbar.ImmersionBar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PlayFragment : Fragment() {
    private var _bind:FragmentPlayBinding?=null
    private val bind get() = _bind!!
    private val viewModel:MainViewModel by activityViewModels()
    private var shouldUpdateSeekbar = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind=FragmentPlayBinding.inflate(layoutInflater)
        requireActivity().findViewById<Toolbar>(R.id.main_toolBar).isVisible=false
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarDarkFont(true)
            .autoDarkModeEnable(true)
//            .transparentStatusBar()
            .fullScreen(true)
            .init()
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
    }

    private fun initEvent() {
        bind.musicPlayBtn.setOnClickListener {
            viewModel.playOrPause(null,null,true)
        }
        bind.musicNextBtn.setOnClickListener {
            viewModel.skipToNext()
        }
        bind.musicPrevBtn.setOnClickListener {
            viewModel.skipToPrevious()
        }
        bind.musicSeekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    setCurPlayerTimeToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar=false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    viewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar=true
                }
            }
        })
    }

    private fun initView() {
        viewModel.mMetaDataLiveData.observe(viewLifecycleOwner,{
            bind.musicBg.load(it.description.iconUri){
                error(R.drawable.error)
                transformations(BlurTransformation(requireContext(),25f))
            }
            bind.musicCoverImg.load(it.description.iconUri){
                transformations(RoundedCornersTransformation(20F,20F,20F,20F))
                    .error(R.drawable.error)
            }
            bind.musicTitle.text=it.description.title
            bind.musicArtist.text=it.description.subtitle
            val duration=it.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            bind.musicSeekbar.max=duration.toInt()
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            bind.musicEndDuration.text = dateFormat.format(duration)
        })
        viewModel.mPlayStateLiveData.observe(viewLifecycleOwner,{
            if(it.state==PlaybackStateCompat.STATE_PLAYING){
                bind.musicPlayBtn.setImageResource(R.drawable.exo_controls_pause)
            }else if(it.state==PlaybackStateCompat.STATE_PAUSED){
                bind.musicPlayBtn.setImageResource(R.drawable.exo_controls_play)
            }
        })
        viewModel.curPlayPosition.observe(viewLifecycleOwner,{
            if(shouldUpdateSeekbar){
                bind.musicSeekbar.progress=it.toInt()
                setCurPlayerTimeToTextView(it)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<Toolbar>(R.id.main_toolBar).isVisible=true
        _bind=null
    }
    private fun setCurPlayerTimeToTextView(ms: Long) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        bind.musicStartDuration.text = dateFormat.format(ms)
    }
}