package com.example.szong.ui.base


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.size.ViewSizeResolver
import com.example.szong.App
import com.example.szong.R
import com.example.szong.data.music.standard.parse
import com.example.szong.databinding.MiniPlayerBinding
import com.example.szong.util.ui.theme.DarkThemeUtil
import com.example.szong.manager.activity.ActivityCollector
import com.example.szong.ui.diolog.PlaylistDialog
import com.example.szong.util.app.loge
import com.example.szong.util.ui.opration.setStatusBarIconColor
import com.example.szong.widget.toast

abstract class BaseActivity : AppCompatActivity() {

    var miniPlayer: MiniPlayerBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
        initBinding()
        initData()
        initView()
        initListener()
        initObserver()
        initBroadcastReceiver()
        initMiniPlayer()
    }


    override fun onStart() {
        super.onStart()
        if (DarkThemeUtil.isDarkTheme(this)) {
            setStatusBarIconColor(this, false)
        }
        initShowDialogListener()
    }

    protected open fun initBinding() {}

    protected open fun initView() {}

    protected open fun initData() {}

    protected open fun initListener() {}

    protected open fun initObserver() {}

    protected open fun initBroadcastReceiver() {}

    protected open fun initShowDialogListener() {}


    @SuppressLint("SetTextI18n")
    private fun initMiniPlayer() {
        miniPlayer?.let { mini ->
            mini.apply {
                root.setOnClickListener {
                    App.activityManager.startPlayerActivity(this@BaseActivity)
                }
                ivPlayQueue.setOnClickListener {
                    if (App.musicController.value?.personFM?.value != true) {
                        PlaylistDialog().show(supportFragmentManager, null)
                    } else {
                        toast("当前为私人 FM 模式")
                    }
                }
                ivStartOrPause.setOnClickListener { App.musicController.value?.changePlayState() }
            }
            App.musicController.observe(this, { nullableController ->
                nullableController?.apply {
                    getPlayingSongData().observe(this@BaseActivity, { songData ->
                        songData?.let {
                            mini.tvTitle.text = songData.name + " - " + songData.artists?.parse()
                        }
                    })
                    isPlaying().observe(this@BaseActivity, {
                        if (it) {
                            mini.ivStartOrPause.contentDescription = getString(R.string.pause_music)
                        } else {
                            mini.ivStartOrPause.contentDescription = getString(R.string.play_music)
                        }
                        mini.ivStartOrPause.setImageResource(getPlayStateSourceId(it))
                    })
                    getPlayerCover().observe(this@BaseActivity, { bitmap ->
                        mini.ivCover.load(bitmap) {
                            size(ViewSizeResolver(mini.ivCover))
                            error(R.drawable.ic_song_cover)
                        }
                    })
                }
            })
        }

    }


    /**
     * 获取播放状态 MiniPlayer 图标
     */
    private fun getPlayStateSourceId(playing: Boolean): Int {
        return if (playing) {
            R.drawable.ic_mini_player_pause
        } else {
            R.drawable.ic_mini_player_play
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        miniPlayer = null
        ActivityCollector.removeActivity(this)
    }

}
