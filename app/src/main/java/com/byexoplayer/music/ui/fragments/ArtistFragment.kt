package com.byexoplayer.music.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.byexoplayer.music.adapters.ArtistMusicAdapter
import com.byexoplayer.music.databinding.FragmentArtistBinding
import com.byexoplayer.music.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistFragment : Fragment() {
    private var _bind:FragmentArtistBinding?=null
    private val bind get() = _bind!!
    private val viewModel:MainViewModel by viewModels()
    private val artistAdapter=ArtistMusicAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _bind= FragmentArtistBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.artistList.observe(requireActivity(),{
            if(it.size>0){
                artistAdapter.submitList(it)
            }
        })
        initView()
    }

    private fun initView() {
        bind.artistRv.apply {
            adapter=artistAdapter
            layoutManager=GridLayoutManager(requireContext(),2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind=null
    }
}