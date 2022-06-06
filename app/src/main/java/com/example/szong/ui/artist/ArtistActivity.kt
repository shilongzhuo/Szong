package com.example.szong.ui.artist

import com.example.szong.App
import com.example.szong.databinding.ActivityArtistBinding
import com.example.szong.ui.base.BaseActivity
import com.example.szong.util.app.runOnMainThread

class ArtistActivity : BaseActivity() {

    companion object {
        const val EXTRA_LONG_ARTIST_ID = "long_artist_id"
        private const val DEFAULT_ARTIST_ID = 0L
    }

    private lateinit var binding: ActivityArtistBinding

    override fun initBinding() {
        binding = ActivityArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        val artistId = intent.getLongExtra(EXTRA_LONG_ARTIST_ID, DEFAULT_ARTIST_ID)
        App.cloudMusicManager.getArtists(artistId) {
            runOnMainThread {
                binding.titleBar.setTitleBarText(it.artist.name)
                val description = it.artist.briefDesc
                binding.tvDescription.text = if (!description.isNullOrEmpty()) {
                    description
                } else {
                    "暂无介绍"
                }
            }
        }
    }

}