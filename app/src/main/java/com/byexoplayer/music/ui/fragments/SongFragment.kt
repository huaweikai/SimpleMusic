package com.byexoplayer.music.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.byexoplayer.music.R
import com.byexoplayer.music.adapters.SongMusicAdapter
import com.byexoplayer.music.databinding.FragmentSongBinding
import com.byexoplayer.music.db.MusicItem
import com.byexoplayer.music.others.Utils
import com.byexoplayer.music.services.MusicService
import com.byexoplayer.music.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  23:33
 * @Description : 本地音乐列表
 */

@AndroidEntryPoint
class SongFragment : Fragment() {
    private var _bind: FragmentSongBinding? = null
    private val bind get() = _bind!!
    private val songMusicAdapter = SongMusicAdapter()
    private val viewModel: MainViewModel by activityViewModels()
    private var list: ArrayList<MusicItem> = arrayListOf()

    companion object {
        //在其他音乐歌单点击播放后，也就是切换到其他歌单了，所以变成true
        var isFirst = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _bind = FragmentSongBinding.inflate(layoutInflater)
        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.songRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songMusicAdapter
        }

        viewModel.musicList.observe(viewLifecycleOwner, {
            list.clear()
            songMusicAdapter.submitList(it)
            list.addAll(it)
        })
        songMusicAdapter.setListener {
            if (isFirst) {
                MusicService.servicePlayList.clear()
                viewModel.setMusicList(list, it)
                viewModel.prepare()
                isFirst = false
            } else {
                viewModel.playOrPause(list[it], it, false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.song_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.song_refresh -> {
                bind.songRv.isVisible = false
                bind.musicLoading.isVisible = true
                lifecycleScope.launch(Dispatchers.Main) {
                    val musicList = Utils.getAllAudio(requireContext())
                    val artistList = Utils.getArtistLoader(requireContext())
                    val albumList = Utils.getAlbumLoader(requireContext())
                    viewModel.comMusicList(musicList, artistList, albumList)
                    delay(2000L)
                    viewModel.scanAllMusic(musicList, artistList, albumList)

                    //取消progressbar，暂时放在这
                    withContext(Dispatchers.Main) {
                        bind.musicLoading.isVisible = false
                        bind.songRv.isVisible = true
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }
}