package com.example.szong.ui.player

import coil.load
import coil.size.ViewSizeResolver
import coil.transform.BlurTransformation
import com.example.szong.App
import com.example.szong.R
import com.example.szong.databinding.ActivitySongCoverBinding
import com.example.szong.ui.base.BaseActivity

class SongCoverActivity : BaseActivity() {

    private lateinit var binding: ActivitySongCoverBinding

    override fun initBinding() {
        binding = ActivitySongCoverBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        with(binding) {
            App.musicController.value?.getPlayerCover()?.value?.let {
                // 设置 背景 图片
                binding.ivBackground.load(it) {
                    size(ViewSizeResolver(binding.ivBackground))
                    transformations(BlurTransformation(this@SongCoverActivity, 25f, 10f))
                }
                photoView.setImageBitmap(it)
            }
        }
    }

    override fun initListener() {
        with(binding) {
            photoView.setOnPhotoTapListener { _, _, _ ->
                finish()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_alpha_exit,
        )
    }

}