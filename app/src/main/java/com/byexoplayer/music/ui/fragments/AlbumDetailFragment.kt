package com.byexoplayer.music.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.byexoplayer.music.R
import com.byexoplayer.music.adapters.SongMusicAdapter
import com.byexoplayer.music.databinding.FragmentAlbumDetailBinding
import com.byexoplayer.music.db.AlbumItem
import com.byexoplayer.music.db.MusicItem
import com.byexoplayer.music.others.Constants.POST_ALBUM_DATA
import com.byexoplayer.music.others.Constants.POST_MUSIC_BY_ALBUM
import com.byexoplayer.music.services.MusicService
import com.byexoplayer.music.ui.viewmodels.MainViewModel

class AlbumDetailFragment : Fragment() {
    private var _bind: FragmentAlbumDetailBinding? = null
    private val bind get() = _bind!!
    private val musicAdapter = SongMusicAdapter()
    private var list: List<MusicItem>? = null
    private var album: AlbumItem? = null
    private lateinit var viewModel: MainViewModel
    private var isFirst = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentAlbumDetailBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        album = arguments?.getParcelable(POST_ALBUM_DATA)
        list = arguments?.getParcelableArrayList(POST_MUSIC_BY_ALBUM)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        initView()
        initEvent()
    }

    private fun initEvent() {
        musicAdapter.setListener {
            SongFragment.isFirst = true
            if (isFirst) {
                MusicService.servicePlayList.clear()
                viewModel.setMusicList(list!!, it)
                viewModel.prepare()
                isFirst = false
            } else {
                viewModel.playOrPause(list!![it], it, false)
            }
        }
    }

    private fun initView() {
        album?.let {
            bind.appBarImage.load(it.albumImg) {
                error(R.drawable.error)
            }
            requireActivity().findViewById<Toolbar>(R.id.main_toolBar).title = it.albumTitle
        }
        bind.albumMusicRv.apply {
            adapter = musicAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        musicAdapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }
}
