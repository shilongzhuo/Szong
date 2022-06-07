package com.example.szong.ui.cloudapi

import com.example.szong.App
import com.example.szong.config.AppConfig
import com.example.szong.databinding.ActivityNeteaseCloudMusicApiBinding
import com.example.szong.ui.base.BaseActivity
import com.example.szong.ui.setting.SettingsActivity
import com.example.szong.util.app.BroadcastUtil
import com.example.szong.util.net.openUrlByBrowser


class NeteaseCloudMusicApiActivity : BaseActivity() {

    companion object {
        private const val URL_NETEASE_CLOUD_MUSIC_API = "https://github.com/Binaryify/NeteaseCloudMusicApi"
    }

    private lateinit var binding: ActivityNeteaseCloudMusicApiBinding

    override fun initBinding() {
        binding = ActivityNeteaseCloudMusicApiBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        with(binding) {
            switcherEnableService.setChecked(App.mmkv.decodeBool(AppConfig.USER_NETEASE_CLOUD_MUSIC_API_ENABLE, false))
            etService.setText(App.mmkv.decodeString(AppConfig.USER_NETEASE_CLOUD_MUSIC_API_URL, ""))
        }
    }

    override fun initListener() {
        with(binding) {
            switcherEnableService.setOnCheckedChangeListener {
                App.mmkv.encode(AppConfig.USER_NETEASE_CLOUD_MUSIC_API_ENABLE, it)
            }
//            itemNeteaseCloudMusicApiGithub.setOnClickListener {
//                openUrlByBrowser(this@NeteaseCloudMusicApiActivity, URL_NETEASE_CLOUD_MUSIC_API)
//            }
        }
    }

    override fun onPause() {
        super.onPause()
        App.mmkv.encode(AppConfig.USER_NETEASE_CLOUD_MUSIC_API_URL, binding.etService.text.toString())
        BroadcastUtil.send(this, SettingsActivity.ACTION)
    }

}