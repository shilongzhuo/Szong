package com.example.szong.ui.player


import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.content.res.Configuration
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.updateLayoutParams
import androidx.palette.graphics.Palette
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.BlurTransformation
import com.example.szong.App
import com.example.szong.R
import com.example.szong.api.music.song.favorite.local.MyFavoriteAPI
import com.example.szong.config.AppConfig
import com.example.szong.data.music.standard.SOURCE_LOCAL
import com.example.szong.data.music.standard.SOURCE_NETEASE
import com.example.szong.data.music.standard.parseArtist
import com.example.szong.databinding.ActivityPlayerBinding
import com.example.szong.service.media.base.BaseMediaService
import com.example.szong.service.media.device.VolumeManager
import com.example.szong.ui.base.SlideBackActivity
import com.example.szong.ui.diolog.*
import com.example.szong.ui.player.viewmodel.PlayerViewModel
import com.example.szong.util.app.runOnMainThread
import com.example.szong.util.app.singleClick
import com.example.szong.util.ui.animation.AnimationUtil
import com.example.szong.util.ui.opration.asColor
import com.example.szong.util.ui.opration.asDrawable
import com.example.szong.util.ui.opration.colorAlpha
import com.example.szong.util.ui.opration.colorMix
import com.example.szong.util.ui.theme.DarkThemeUtil
import com.example.szong.widget.toast

class PlayerActivity : SlideBackActivity() {

    private lateinit var binding: ActivityPlayerBinding

    // ?????????????????????
    private var isLandScape = false

    // ?????????????????????
    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver

    // ViewModel ?????????????????????
    private val playViewModel: PlayerViewModel by viewModels()

    // Looper + Handler
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == MSG_PROGRESS) {
                if (App.musicController.value?.isPlaying()?.value == true) {
                    playViewModel.refreshProgress()
                }
            }
        }
    }

    // CD ????????????
    private val objectAnimator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(binding.includePlayerCover.root, ANIMATION_PROPERTY_NAME, 0f, 360f).apply {
            interpolator = LinearInterpolator()
            duration = DURATION_CD
            repeatCount = ANIMATION_REPEAT_COUNTS
            start()
        }
    }

    private var previousBitmap: Bitmap? = null

    override fun initBinding() {
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            playViewModel.systemWindowInsetTop.value = insets.systemWindowInsetTop
            playViewModel.systemWindowInsetLeft.value = insets.systemWindowInsetLeft
            playViewModel.systemWindowInsetRight.value = insets.systemWindowInsetRight
            playViewModel.systemWindowInsetBottom.value = insets.systemWindowInsetBottom
            insets
        }
        setContentView(binding.root)
    }

    override fun initView() {
        if (DarkThemeUtil.isDarkTheme(this)) {
            playViewModel.normalColor.value = Color.WHITE
        } else {
            playViewModel.normalColor.value = Color.rgb(50, 50, 50)
        }
        if (App.mmkv.decodeBool(AppConfig.NETEASE_GOOD_COMMENTS, true)) {
            binding.ivComment.visibility = View.VISIBLE
        } else {
            binding.ivComment.visibility = View.GONE
        }
        // window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // ?????? SlideBackLayout
        bindSlide(this, binding.clBase)
        // ????????????

        //???????????????????????????
        val appConfiguration = this.resources.configuration
        if (appConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandScape = true
//            // ?????????????????????
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//                window.insetsController?.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE)
//            }
        }


        binding.apply {
            // ???????????????
            ttvDuration.setAlignRight()
            // ????????????????????????
            ivTranslation.visibility = View.GONE
            // ?????????????????????
            seekBarVolume.max = VolumeManager.maxVolume
            seekBarVolume.progress = VolumeManager.getCurrentVolume()

            lyricView.setLabel("???????????????")
            lyricView.setTimelineTextColor(ContextCompat.getColor(this@PlayerActivity, R.color.colorTextForeground))
        }
        if (isLandScape) {
            slideBackEnabled = false
        }

        PlayerViewModel.fragmentManager = supportFragmentManager
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        binding.apply {
            // ????????????
            ivBack.setOnClickListener { finish() }
            // ?????? / ??????????????????
            ivPlay.setOnClickListener {
                toast("PLAY")
                playViewModel.changePlayState()
            }
            // ?????????
            ivLast.setOnClickListener { playViewModel.playLast() }
            // ?????????
            ivNext.setOnClickListener { playViewModel.playNext() }
            // ??????????????????
            ivMode.setOnClickListener {
                singleClick {
                    playViewModel.changePlayMode()
                }
            }
            // ????????????
            ivSleepTimer.setOnClickListener {
                TimingOffDialog(this@PlayerActivity).show()
            }
            // ??????
            ivComment.setOnClickListener {
                App.musicController.value?.getPlayingSongData()?.value?.let {
                    if (it.source != SOURCE_LOCAL) {
                        App.activityManager.startCommentActivity(
                            this@PlayerActivity,
                            it.source ?: SOURCE_NETEASE,
                            it.id ?: ""
                        )
                    } else {
                        toast("????????????")
                    }
                }
            }

            if (!isLandScape) {
                includePlayerCover.root.setOnLongClickListener {
                    startActivity(Intent(this@PlayerActivity, SongCoverActivity::class.java))
                    overridePendingTransition(
                        R.anim.anim_alpha_enter,
                        R.anim.anim_no_anim,
                    )
                    return@setOnLongClickListener true
                }
            }
            // ????????????
            ivLike.setOnClickListener {
                playViewModel.likeMusic {
                    runOnMainThread {
                        playViewModel.heart.value = it
                    }
                }
            }
            // lyricView
            lyricView.setDraggable(true, object : OnPlayClickListener {
                override fun onPlayClick(time: Long): Boolean {
                    playViewModel.setProgress(time.toInt())
                    return true
                }
            })
            lyricView.setOnSingerClickListener(object : OnSingleClickListener {
                override fun onClick() {
                    if (!isLandScape) {
                        AnimationUtil.fadeIn(binding.clCd)
                        AnimationUtil.fadeIn(binding.clMenu)
                        binding.clLyric.visibility = View.INVISIBLE
                        slideBackEnabled = true
                    }
                }
            })

            if (!isLandScape) {
                includePlayerCover.root.setOnClickListener {
                    if (slideBackEnabled) {
                        AnimationUtil.fadeOut(binding.clCd, true)
                        AnimationUtil.fadeOut(binding.clMenu, true)
                        binding.clLyric.visibility = View.VISIBLE
                        slideBackEnabled = false
                    }
                }
                clCd.setOnClickListener {
                    if (slideBackEnabled) {
                        AnimationUtil.fadeOut(binding.clCd, true)
                        AnimationUtil.fadeOut(binding.clMenu, true)
                        binding.clLyric.visibility = View.VISIBLE
                        slideBackEnabled = false
                    }
                }
            }
            lyricView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    slideBackEnabled = false
                }
                return@setOnTouchListener false
            }
            edgeTransparentView.setOnClickListener {
                if (!isLandScape) {
                    AnimationUtil.fadeIn(binding.clCd)
                    AnimationUtil.fadeIn(binding.clMenu)
                    binding.clLyric.visibility = View.INVISIBLE
                    slideBackEnabled = true
                }
            }

            // ?????????
            tvArtist.setOnClickListener {
                // ??????
                App.musicController.value?.getPlayingSongData()?.value?.let { standardSongData ->
                    if (standardSongData.source == SOURCE_NETEASE) {
                        standardSongData.artists?.let {
                            it[0].artistId?.let { artistId ->
                                App.activityManager.startArtistActivity(this@PlayerActivity, artistId)
                            }
                        }
                    } else {
                        toast("???????????????")
                    }
                }
            }
            // ????????????
            ivTranslation.setOnClickListener {
                playViewModel.setLyricTranslation(playViewModel.lyricTranslation.value != true)
            }
            // ??????????????????????????????
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // ?????????????????????
                    if (fromUser) {
                        playViewModel.setProgress(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
            // ??????????????????
            seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // ?????????????????????
                    if (fromUser) {
                        playViewModel.currentVolume.value = progress
                        VolumeManager.setStreamVolume(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
        }
    }

    override fun initShowDialogListener() {
        binding.apply {
            // ?????????
            ivEqualizer.setOnClickListener {
                singleClick {
                    SoundEffectDialog(this@PlayerActivity, this@PlayerActivity).show()
                }
            }
            // ????????????
            ivMore.setOnClickListener {
                singleClick {
                    PlayerMenuMoreDialog(this@PlayerActivity).show()
                }
            }
            // ????????????
            ivList.setOnClickListener {
                singleClick {
                    PlaylistDialog().show(supportFragmentManager, null)
                }
            }
        }
    }

    override fun initBroadcastReceiver() {
        // Intent ????????????????????? "com.dirror.foyou.MUSIC_BROADCAST" ????????????
        val intentFilter = IntentFilter()
        intentFilter.addAction(MUSIC_BROADCAST_ACTION)
        musicBroadcastReceiver = MusicBroadcastReceiver()
        // ???????????????
        registerReceiver(musicBroadcastReceiver, intentFilter)
    }


    override fun initObserver() {
        App.musicController.observe(this@PlayerActivity, { nullableController ->
            nullableController?.let { controller ->
                // ?????????????????????
                controller.getPlayingSongData().observe(this@PlayerActivity, {
                    objectAnimator.cancel()
                    objectAnimator.start()

                    it?.let {
                        binding.tvName.text = it.name
                        binding.tvArtist.text = it.artists?.let { artists ->
                            parseArtist(artists)
                        }
                        // ????????????
                        playViewModel.updateLyric()
                        // ???????????????
                        MyFavoriteAPI.isExist(it) { exist ->
                            runOnMainThread {
                                playViewModel.heart.value = exist
                            }
                        }
                    }
                })
                // ????????????
                controller.getPlayerCover().observe(this@PlayerActivity, { bitmap ->
                    runOnMainThread {
                        // ?????? CD ??????
                        binding.includePlayerCover.ivCover.load(bitmap) {
                            placeholder(previousBitmap?.toDrawable(resources))
                            size(ViewSizeResolver(binding.includePlayerCover.ivCover))
                            crossfade(500)
                        }
                        binding.ivLyricsBackground.load(bitmap) {
                            size(ViewSizeResolver(binding.includePlayerCover.ivCover))
                            // TODO sampling 5?
                            transformations(BlurTransformation(this@PlayerActivity, 15F, 15F))
                            crossfade(500)
                        }
                        previousBitmap = bitmap

                        // ????????????
                        bitmap?.let {
                            Palette.from(bitmap)
                                .clearFilters()
                                .generate { palette ->
                                    palette?.let {
                                        val muteColor = if (DarkThemeUtil.isDarkTheme(this@PlayerActivity)) {
                                            palette.getLightMutedColor(PlayerViewModel.DEFAULT_COLOR)
                                        } else {
                                            palette.getDarkMutedColor(PlayerViewModel.DEFAULT_COLOR)
                                        }
                                        val vibrantColor = palette.getVibrantColor(PlayerViewModel.DEFAULT_COLOR)
                                        playViewModel.normalColor.value =  if (DarkThemeUtil.isDarkTheme(this@PlayerActivity)) {
                                            muteColor.colorMix(vibrantColor, Color.WHITE, Color.WHITE, Color.WHITE)
                                        } else {
                                            muteColor.colorMix(vibrantColor, Color.BLACK)
                                        }
                                        playViewModel.color.value =  if (DarkThemeUtil.isDarkTheme(this@PlayerActivity)) {
                                            vibrantColor // muteColor.colorMix(vibrantColor)
                                        } else {
                                            vibrantColor // muteColor.colorMix(vibrantColor)
                                        }
                                    }
                                }
                        }
                    }

                })
                controller.isPlaying().observe(this@PlayerActivity, {
                    if (it) {
                        binding.ivPlay.contentDescription = getString(R.string.pause_music)
                        binding.ivPlay.setImageResource(R.drawable.ic_player_playing)
                        handler.sendEmptyMessageDelayed(MSG_PROGRESS, DELAY_MILLIS)
                        startRotateAlways()
                        // binding.diffuseView.start()
                    } else {
                        binding.ivPlay.contentDescription = getString(R.string.play_music)
                        binding.ivPlay.setImageResource(R.drawable.ic_player_paused)
                        handler.removeMessages(MSG_PROGRESS)
                        pauseRotateAlways()
                        // binding.diffuseView.stop()
                    }
                })
                controller.personFM.observe(this, {
                    with(binding) {
                        if (it) {
                            ivMode.isClickable = false
                            ivLast.isClickable = false
                            ivList.isClickable = false
                            tvTag?.text = getString(R.string.personal_fm)
                        } else {
                            ivMode.isClickable = true
                            ivLast.isClickable = true
                            ivList.isClickable = true
                            tvTag?.text = ""
                        }
                    }
                })
            }
        })
        playViewModel.apply {
            // ?????????????????????
            playMode.observe(this@PlayerActivity, {
                binding.ivMode.contentDescription = this.getModeContentDescription(it)
                when (it) {
                    BaseMediaService.MODE_CIRCLE -> binding.ivMode.setImageResource(R.drawable.ic_player_circle)
                    BaseMediaService.MODE_REPEAT_ONE -> binding.ivMode.setImageResource(R.drawable.ic_player_repeat_one)
                    BaseMediaService.MODE_RANDOM -> binding.ivMode.setImageResource(R.drawable.ic_player_random)
                }
            })
            // ??????????????????
            duration.observe(this@PlayerActivity, {
                binding.seekBar.max = it
                binding.ttvDuration.setText(it)
            })
            // ???????????????
            progress.observe(this@PlayerActivity, {
                binding.seekBar.progress = it
                binding.ttvProgress.setText(it)
                handler.sendEmptyMessageDelayed(MSG_PROGRESS, DELAY_MILLIS)
                // ????????????????????????
                binding.lyricView.updateTime(it.toLong())
            })
            // ????????????
            lyricTranslation.observe(this@PlayerActivity, {
                if (it == true) {
                    binding.ivTranslation.alpha = 1F
                } else {
                    binding.ivTranslation.alpha = 0.3F
                }
            })

            // ????????????
            lyricViewData.observe(this@PlayerActivity, {
                // ??????????????????
                binding.ivTranslation.visibility = if (it.secondLyric.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                if (playViewModel.lyricTranslation.value == true) {
                    binding.lyricView.loadLyric(it.lyric, it.secondLyric)
                } else {
                    binding.lyricView.loadLyric(it.lyric)
                }
            })
            // ????????????
            currentVolume.observe(this@PlayerActivity, {
                binding.seekBarVolume.progress = it
            })
            // ????????????
            color.observe(this@PlayerActivity, {
                binding.apply {
                    val darkMode = DarkThemeUtil.isDarkTheme(this@PlayerActivity)
                    val secondColor = if (darkMode) {
                        Color.rgb(45, 45, 45)
                    } else {
                        Color.WHITE
                    }
                    val mixColor = if (darkMode) {
                        it.colorMix(secondColor, secondColor, secondColor)
                    } else {
                        it.colorMix(Color.WHITE, Color.WHITE, Color.WHITE)
                    }

                }

            })

            normalColor.observe(this@PlayerActivity, {
                binding.lyricView.apply {
                    setCurrentColor(it)
                    setTimeTextColor(it)
                    setTimelineColor(it.colorAlpha(0.25f))
                    setTimelineTextColor(it)
                    setNormalColor(it.colorAlpha(0.35f))
                }
                with(binding) {
                    ttvProgress.textColor = it
                    ttvDuration.textColor = it
                    tvTag?.setTextColor(it)

                    ivPlay.setColorFilter(it)
                    ivLast.setColorFilter(it)
                    ivNext.setColorFilter(it)
                    tvName.setTextColor(it)
                    tvArtist.setTextColor(it)
                    ivBack.setColorFilter(it)

                    ivEqualizer.setColorFilter(it)
                    ivSleepTimer.setColorFilter(it)
                    ivLike.setColorFilter(it)
                    ivComment.setColorFilter(it)
                    ivMore.setColorFilter(it)

                    ivMode.setColorFilter(it)
                    ivList.setColorFilter(it)

                    seekBar.thumb.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
                    seekBar.progressDrawable.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
                    seekBarVolume.thumb.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
                    seekBarVolume.progressDrawable.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)

                    ivVolume.setColorFilter(it)
                    if (playViewModel.heart.value == true) {
                        playViewModel.heart.value = true
                    }
                }
            })

            heart.observe(this@PlayerActivity, {
                if (it) {
                    binding.ivLike.setImageDrawable(R.drawable.ic_player_heart.asDrawable(this@PlayerActivity))
                    binding.ivLike.setColorFilter(R.color.colorAppThemeColor.asColor(this@PlayerActivity))
                } else {
                    binding.ivLike.setImageDrawable(R.drawable.ic_player_heart_outline.asDrawable(this@PlayerActivity))
                    playViewModel.normalColor.value?.let { it1 -> binding.ivLike.setColorFilter(it1) }
                }
            })
            systemWindowInsetTop.observe(this@PlayerActivity, { top ->
                // ?????????????????????
                binding.titleBar?.let {
                    (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        topMargin = top
                    }
                }

                binding.llBase?.let {
                    it.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        topMargin = top
                    }
                }
            })
            systemWindowInsetBottom.observe(this@PlayerActivity, { bottom ->
                if (isLandScape) {
                    binding.llBase?.let {
                        it.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            bottomMargin = bottom
                        }
                    }
                } else {
                    binding.clBottom.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomMargin = bottom
                    }
                }

            })
        }
    }

    /**
     * ??????????????????
     */
    private fun startRotateAlways() {
        objectAnimator.resume()
    }

    /**
     * ??????????????????
     */
    private fun pauseRotateAlways() {
        playViewModel.rotation = binding.includePlayerCover.root.rotation
        objectAnimator.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // ??????????????????????????????
        unregisterReceiver(musicBroadcastReceiver)
        // ?????? Handler ??????????????????????????????????????????
        handler.removeCallbacksAndMessages(null)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_slide_exit_bottom
        )
    }

    /**
     * ????????? - ?????????????????????
     */
    inner class MusicBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // ViewModel ????????????
            playViewModel.refresh()
        }
    }

    /**
     * ????????????????????????
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                playViewModel.addVolume()
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                playViewModel.reduceVolume()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        private const val MUSIC_BROADCAST_ACTION = "com.example.szong.MUSIC_BROADCAST"
        private const val DELAY_MILLIS = 500L

        // Handle ?????????????????????
        private const val MSG_PROGRESS = 0

        // ??????????????????
        private const val DURATION_CD = 32_000L
        private const val ANIMATION_REPEAT_COUNTS = -1
        private const val ANIMATION_PROPERTY_NAME = "rotation"
    }

}