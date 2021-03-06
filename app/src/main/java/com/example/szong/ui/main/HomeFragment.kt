package com.example.szong.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.szong.databinding.FragmentHomeBinding
import com.example.szong.databinding.IncludeFoyouBinding

import com.example.szong.api.music.playlist.recommend.netease.RecommendAPI
import com.example.szong.api.music.song.newsong.netease.NewSongAPI
import com.example.szong.ui.base.BaseFragment
import com.example.szong.ui.main.adapter.NewSongAdapter
import com.example.szong.ui.main.adapter.PlaylistRecommendAdapter
import com.example.szong.api.sentence.SentenceAPI
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.ui.main.viewmodel.MainViewModel
import com.example.szong.ui.recommend.RecommendActivity
import com.example.szong.ui.toplist.TopListActivity
import com.example.szong.util.app.runOnMainThread
import com.example.szong.util.net.status.ErrorCode
import com.example.szong.util.ui.animation.AnimationUtil
import com.example.szong.widget.toast

class HomeFragment : BaseFragment(){

    private var _binding: FragmentHomeBinding? = null
    private var inb: IncludeFoyouBinding?=null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        update()
    }

    /**
     * 刷新整个页面
     */
    private fun update() {
        // Banner
        // initBanner()
        // 推荐歌单
        refreshPlaylistRecommend()
        // 新歌速递
        updateNewSong()
        // 更改句子
        changeSentence()
    }

    override fun initListener() {
              binding.includeFoyou.root.setOnClickListener {
            changeSentence()
        }

        binding.clDaily.setOnClickListener {
            if (NeteaseUser.hasCookie) {
                val intent = Intent(this.context, RecommendActivity::class.java)
                startActivity(intent)
            } else {
               toast(ErrorCode.getMessage(ErrorCode.ERROR_NOT_COOKIE))
            }
        }

        binding.clTopList.setOnClickListener {
            val intent = Intent(this.context, TopListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initObserver() {
        with(mainViewModel) {
            neteaseLiveVisibility.observe(viewLifecycleOwner) {
                binding.clDaily.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
            sentenceVisibility.observe(viewLifecycleOwner) {
                if (it) {
                    binding.includeFoyou.root.visibility = View.VISIBLE
                    binding.tvFoyou.visibility = View.VISIBLE
                } else {
                    binding.includeFoyou.root.visibility = View.GONE
                    binding.tvFoyou.visibility = View.GONE
                }
            }
        }

    }

    private fun changeSentence() {
        binding.includeFoyou.tvText.alpha = 0f
        binding.includeFoyou.tvAuthor.alpha = 0f
        binding.includeFoyou.tvSource.alpha = 0f

        SentenceAPI.getSentence {
            runOnMainThread {
                binding.includeFoyou.tvText.text = it.text
                binding.includeFoyou.tvAuthor.text = it.author
                binding.includeFoyou.tvSource.text = it.source
                AnimationUtil.fadeIn(binding.includeFoyou.tvText, 1000, false)
                AnimationUtil.fadeIn(binding.includeFoyou.tvAuthor, 1000, false)
                AnimationUtil.fadeIn(binding.includeFoyou.tvSource, 1000, false)
            }
        }
    }

    private fun refreshPlaylistRecommend() {
        RecommendAPI.getPlaylistRecommend(requireContext(), {
            runOnMainThread {
                binding.rvPlaylistRecommend.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
               // binding.rvPlaylistRecommend.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.rvPlaylistRecommend.adapter = PlaylistRecommendAdapter(it)

            }
        }, {})
    }

    private fun updateNewSong() {
        this.context?.let {
            NewSongAPI.getNewSong(it) {
                runOnMainThread {
                    binding.rvNewSong.layoutManager = GridLayoutManager(this.context, 2)
                    binding.rvNewSong.adapter = NewSongAdapter(it)
                }
            }
        }
    }

}