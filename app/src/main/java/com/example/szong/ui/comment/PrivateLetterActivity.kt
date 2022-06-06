package com.example.szong.ui.comment

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.szong.App
import com.example.szong.databinding.ActivityPrivateLetterBinding
import com.example.szong.ui.base.BaseActivity
import com.example.szong.ui.comment.adapter.PrivateLetterAdapter
import com.example.szong.widget.toast


class PrivateLetterActivity : BaseActivity() {
    private lateinit var binding: ActivityPrivateLetterBinding

    override fun initBinding() {
        binding = ActivityPrivateLetterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        binding.rvPrivateLetter.layoutManager = LinearLayoutManager(this)
        App.cloudMusicManager.getPrivateLetter({
            runOnUiThread {
                binding.rvPrivateLetter.adapter = PrivateLetterAdapter(it.msgs)
            }
        }, {
            toast("获取失败")
        })

    }

}