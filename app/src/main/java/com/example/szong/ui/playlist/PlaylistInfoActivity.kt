package com.example.szong.ui.playlist


import com.example.szong.databinding.ActivityPlaylistInfoBinding
import com.example.szong.ui.base.BaseActivity

class PlaylistInfoActivity : BaseActivity() {

    private lateinit var binding: ActivityPlaylistInfoBinding

    override fun initBinding() {
        binding = ActivityPlaylistInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}