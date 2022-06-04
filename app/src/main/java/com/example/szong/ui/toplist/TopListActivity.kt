package com.example.szong.ui.toplist

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.szong.App
import com.example.szong.api.music.song.toplist.netease.NeteaseTopList
import com.example.szong.databinding.ActivityTopListBinding
import com.example.szong.ui.base.BaseActivity
import com.example.szong.ui.playlist.viewmodel.TAG_NETEASE
import com.example.szong.ui.toplist.adapter.TopListAdapter
import com.example.szong.util.app.runOnMainThread


class TopListActivity : BaseActivity() {

    private lateinit var binding: ActivityTopListBinding

    override fun initBinding() {
        binding = ActivityTopListBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        NeteaseTopList.getTopList(this, {
            runOnMainThread {
                binding.rvTopList.layoutManager = LinearLayoutManager(this)
                binding.rvTopList.adapter = TopListAdapter(it) { listData ->
                    App.activityManager.startPlaylistActivity(this, TAG_NETEASE, listData.id.toString())
                }
            }
        }, {

        })
    }

}