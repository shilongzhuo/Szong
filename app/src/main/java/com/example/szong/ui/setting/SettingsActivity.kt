package com.example.szong.ui.setting

import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.view.View
import com.example.szong.App
import com.example.szong.App.Companion.mmkv
import com.example.szong.config.AppConfig
import com.example.szong.databinding.ActivitySettingsBinding
import com.example.szong.ui.base.BaseActivity
import com.example.szong.ui.cloudapi.NeteaseCloudMusicApiActivity
import com.example.szong.util.app.BroadcastUtil
import com.example.szong.util.app.runOnMainThread
import com.example.szong.util.cache.ACache
import com.example.szong.util.cache.CommonCacheInterceptor
import com.example.szong.util.image.CoilUtil
import com.example.szong.util.image.ImageCacheManager
import com.example.szong.util.ui.theme.DarkThemeUtil
import com.example.szong.widget.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

/**
 * 设置 Activity
 */
class SettingsActivity : BaseActivity() {

    companion object {
        const val ACTION = "com.example.szong.SETTINGS_CHANGE"
    }

    private lateinit var binding: ActivitySettingsBinding

    override fun initBinding() {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initData() {
        thread {
            val size = ImageCacheManager.getImageCacheSize()
            val httpCacheSize = CommonCacheInterceptor.getCacheSize()
            runOnMainThread {
                binding.valueViewImageCache.setValue(size)
                binding.valueHttpCache.setValue(httpCacheSize)
            }
        }

    }

    override fun initView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            binding.itemAudioFocus.visibility = View.GONE
        }
        // 按钮
        binding.apply {
            switcherPlaylistScrollAnimation.setChecked(mmkv.decodeBool(AppConfig.PLAYLIST_SCROLL_ANIMATION, true))
            switcherDarkTheme.setChecked(mmkv.decodeBool(AppConfig.DARK_THEME, false))
            switcherSentenceRecommend.setChecked(mmkv.decodeBool(AppConfig.SENTENCE_RECOMMEND, true))
            switcherPlayOnMobile.setChecked(mmkv.decodeBool(AppConfig.PLAY_ON_MOBILE, false))
            switcherPauseSongAfterUnplugHeadset.setChecked(
                mmkv.decodeBool(
                    AppConfig.PAUSE_SONG_AFTER_UNPLUG_HEADSET,
                    true
                )
            )
            switcherSkipErrorMusic.setChecked(mmkv.decodeBool(AppConfig.SKIP_ERROR_MUSIC, true))
            switcherFilterRecord.setChecked(mmkv.decodeBool(AppConfig.FILTER_RECORD, true))
            switcherLocalMusicParseLyric.setChecked(
                mmkv.decodeBool(
                    AppConfig.PARSE_INTERNET_LYRIC_LOCAL_MUSIC,
                    true
                )
            )
            switcherSmartFilter.setChecked(mmkv.decodeBool(AppConfig.SMART_FILTER, true))
            switcherAudioFocus.setChecked(mmkv.decodeBool(AppConfig.ALLOW_AUDIO_FOCUS, true))
            switcherSingleColumnPlaylist.setChecked(mmkv.decodeBool(AppConfig.SINGLE_COLUMN_USER_PLAYLIST, false))
            switcherStatusBarLyric.setChecked(mmkv.decodeBool(AppConfig.MEIZU_STATUS_BAR_LYRIC, true))
            switcherInkScreenMode.setChecked(mmkv.decodeBool(AppConfig.INK_SCREEN_MODE, false))
            switcherAutoChangeResource.setChecked(mmkv.decodeBool(AppConfig.AUTO_CHANGE_RESOURCE, false))
        }

    }

    override fun initListener() {
        binding.apply {
            itemCleanBackground.setOnClickListener {
                ACache.get(this@SettingsActivity).remove(AppConfig.APP_THEME_BACKGROUND)
                toast("清除成功")
            }

            switcherPlaylistScrollAnimation.setOnCheckedChangeListener { mmkv.encode(
                AppConfig.PLAYLIST_SCROLL_ANIMATION,
                it
            ) }

            switcherDarkTheme.setOnCheckedChangeListener {
                mmkv.encode(AppConfig.DARK_THEME, it)
                DarkThemeUtil.setDarkTheme(it)
            }

            switcherSentenceRecommend.setOnCheckedChangeListener {
                mmkv.encode(AppConfig.SENTENCE_RECOMMEND, it)
            }

            switcherFilterRecord.setOnCheckedChangeListener { mmkv.encode(AppConfig.FILTER_RECORD, it) }

            switcherLocalMusicParseLyric.setOnCheckedChangeListener { mmkv.encode(
                AppConfig.PARSE_INTERNET_LYRIC_LOCAL_MUSIC,
                it
            ) }

            switcherSkipErrorMusic.setOnCheckedChangeListener { mmkv.encode(AppConfig.SKIP_ERROR_MUSIC, it) }

            switcherPlayOnMobile.setOnCheckedChangeListener { mmkv.encode(AppConfig.PLAY_ON_MOBILE, it) }

            switcherPauseSongAfterUnplugHeadset.setOnCheckedChangeListener { mmkv.encode(
                AppConfig.PAUSE_SONG_AFTER_UNPLUG_HEADSET,
                it
            ) }

            switcherSmartFilter.setOnCheckedChangeListener { mmkv.encode(AppConfig.SMART_FILTER, it) }

            switcherAudioFocus.setOnCheckedChangeListener {
                App.musicController.value?.setAudioFocus(it)
            }

            itemCustomBackground.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, null)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivityForResult(intent, 2)
            }

            switcherSingleColumnPlaylist.setOnCheckedChangeListener { mmkv.encode(AppConfig.SINGLE_COLUMN_USER_PLAYLIST, it) }

            switcherStatusBarLyric.setOnCheckedChangeListener {
                App.musicController.value?.statusBarLyric = it
                mmkv.encode(AppConfig.MEIZU_STATUS_BAR_LYRIC, it)
            }

            itemClearImageCache.setOnClickListener {
                ImageCacheManager.clearImageCache {
                    toast("清除图片缓存成功")
                    thread {
                        val size = ImageCacheManager.getImageCacheSize()
                        runOnMainThread {
                            binding.valueViewImageCache.setValue(size)
                        }
                    }
                }
            }
            itemClearHttpCache.setOnClickListener {
                GlobalScope.launch {
                    CommonCacheInterceptor.clearCache()
                    withContext(Dispatchers.Main){
                        toast("清除歌单缓存成功")
                        val size = CommonCacheInterceptor.getCacheSize()
                        binding.valueHttpCache.setValue(size)
                    }
                }
            }
            switcherInkScreenMode.setOnCheckedChangeListener {
                mmkv.encode(AppConfig.INK_SCREEN_MODE, it)
            }


            itemNeteaseCloudMusicApi.setOnClickListener {
                startActivity(Intent(this@SettingsActivity, NeteaseCloudMusicApiActivity::class.java))
            }

            switcherAutoChangeResource.setOnCheckedChangeListener { mmkv.encode(AppConfig.AUTO_CHANGE_RESOURCE, it) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            // 从相册返回的数据
            // 得到图片的全路径
            val path = data?.data.toString()
            path.let {
                toast("设置成功")
                CoilUtil.load(this, it) { bitmap ->
                    thread {
                        ACache.get(this).put(AppConfig.APP_THEME_BACKGROUND, bitmap)
                    }
                }
            }

        }
    }

    override fun onPause() {
        super.onPause()
        BroadcastUtil.send(this, ACTION)
    }

}