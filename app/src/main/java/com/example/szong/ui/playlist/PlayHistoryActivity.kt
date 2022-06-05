package com.example.szong.ui.playlist

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.szong.api.music.play.playhistory.local.PlayHistoryAPI
import com.example.szong.databinding.ActivityPlayHistoryBinding
import com.example.szong.ui.base.BaseActivity
import com.example.szong.ui.diolog.SongMenuDialog
import com.example.szong.ui.playlist.adapter.SongAdapter
import com.example.szong.widget.toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PlayHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityPlayHistoryBinding

    override fun initBinding() {
        binding = ActivityPlayHistoryBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        binding.apply {
            rvPlayHistory.layoutManager = LinearLayoutManager(this@PlayHistoryActivity)
            rvPlayHistory.adapter = SongAdapter(){
                SongMenuDialog(this@PlayHistoryActivity, this@PlayHistoryActivity, it) {
                    toast("不支持删除")
                }
            }.apply {
                GlobalScope.launch {
                    submitList(PlayHistoryAPI.readPlayHistory().toList())
                }
            }
        }
    }

}