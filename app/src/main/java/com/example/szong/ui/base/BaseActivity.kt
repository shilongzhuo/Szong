package com.example.szong.ui.base


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.szong.R
import com.example.szong.databinding.MiniPlayerBinding
import com.example.szong.util.setStatusBarIconColor
import com.example.szong.util.theme.DarkThemeUtil
import com.example.szong.manager.ActivityCollector

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


    private fun initMiniPlayer() {
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
