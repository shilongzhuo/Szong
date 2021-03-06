package com.example.szong.service.media.music

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.*
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import coil.imageLoader
import coil.request.ImageRequest

import com.example.szong.App
import com.example.szong.App.Companion.context
import com.example.szong.App.Companion.mmkv
import com.example.szong.R
import com.example.szong.config.AppConfig
import com.example.szong.api.music.play.playhistory.local.PlayHistoryAPI
import com.example.szong.api.music.song.fm.netease.PersonalFMAPI
import com.example.szong.data.music.LyricEntry
import com.example.szong.data.music.standard.StandardSongData
import com.example.szong.data.music.standard.parse
import com.example.szong.service.media.base.BaseMediaService
import com.example.szong.service.media.device.BecomingNoisyReceiver
import com.example.szong.ui.main.MainActivity
import com.example.szong.ui.player.PlayerActivity
import com.example.szong.util.app.loge
import com.example.szong.util.app.runOnMainThread
import com.example.szong.util.data.next
import com.example.szong.util.data.previous
import com.example.szong.util.net.status.InternetState
import com.example.szong.util.ui.opration.asDrawable
import com.example.szong.util.ui.opration.dp
import com.example.szong.ui.player.LyricUtil
import com.example.szong.widget.toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MusicService : BaseMediaService() {

    /* ??????????????? */
    private val musicController by lazy { MusicController() }

    /* ???????????? */
    private var mode: Int = mmkv.decodeInt(AppConfig.PLAY_MODE, MODE_CIRCLE)

    /* ???????????? */
    private var notificationManager: NotificationManager? = null

    /* ???????????????????????? */
    private var isAudioFocus = mmkv.decodeBool(AppConfig.ALLOW_AUDIO_FOCUS, true)

    /* ???????????? */
    private var mediaSession: MediaSessionCompat? = null

    /* ?????????????????? */
    private var mediaSessionCallback: MediaSessionCompat.Callback? = null

    /* ?????????????????????0f ???????????? */
    private var speed = 1F

    /* ???????????? */
    private var pitch = 1F

    /* ???????????? */
    private var pitchLevel = 0

    /* ?????????????????????????????????????????? */
    private val pitchUnit = 0.05f

    /* ???????????? */
    private lateinit var audioManager: AudioManager

    /* AudioAttributes */
    private lateinit var audioAttributes: AudioAttributes

    /* AudioFocusRequest */
    private lateinit var audioFocusRequest: AudioFocusRequest

    /* ????????????????????? */
    private var currentStatusBarTag = ""

    /* ????????????????????????????????? */
    private var currentRight = 0

    /* ?????????????????????????????? */
    private var currentCustom = 0

    private var timingOffMode = true

    /* ?????????????????? */
    private val lyricEntryList: ArrayList<LyricEntry> = ArrayList()

    /* Live */
    private var liveLyricEntryList = MutableLiveData<ArrayList<LyricEntry>>(ArrayList())

    /* Handler */
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == MSG_STATUS_BAR_LYRIC ) {
                if (lyricEntryList.isNotEmpty()) {
                    if (getCurrentLineLyricEntry()?.text ?: "" != currentStatusBarTag) {
                        currentStatusBarTag = getCurrentLineLyricEntry()?.text ?: ""
                        updateNotification(true)
                    }
                    if (musicController.isPlaying().value == true) {
                        sendEmptyMessageDelayed(MSG_STATUS_BAR_LYRIC, 100L)
                    }
                }
            }
        }
    }

    /** ??? Transient ???????????????????????????
     * ????????????
     * 1. ????????????????????????????????????????????????????????????
     * 2. ?????????????????????????????????????????????????????????
     * */
    private var isPausedByTransientLossOfFocus = false

    override fun onCreate() {
        // ?????????????????????
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager // ????????????
        // ????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Dso Music ????????????"
            val descriptionText = "?????????????????????????????????"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = descriptionText
            notificationManager?.createNotificationChannel(channel)
        }
        super.onCreate()
        updateNotification(false)
    }

    override fun initAudioFocus() {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener { focusChange ->
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_GAIN -> {
                            if (musicController.isPlaying().value != true && isPausedByTransientLossOfFocus) {
                                mediaSessionCallback?.onPlay()
                            }
                        }
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> mediaSessionCallback?.onPlay()
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> mediaSessionCallback?.onPlay()
                        // ?????????????????????
                        AudioManager.AUDIOFOCUS_LOSS -> {
                            audioManager.abandonAudioFocusRequest(audioFocusRequest)
                            mediaSessionCallback?.onPause()
                        }
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                            isPausedByTransientLossOfFocus = musicController.isPlaying().value ?: false
                            mediaSessionCallback?.onPause()
                        }
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaSessionCallback?.onPause()
                    }
                }.build()
            if (isAudioFocus) {
                audioManager.requestAudioFocus(audioFocusRequest)
            }
        }
    }

    override fun initMediaSession() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        val myNoisyAudioStreamReceiver = BecomingNoisyReceiver()

        // ????????????????????????Service ?????????????????? Callback ????????? MediaPlayer
        mediaSessionCallback = object : MediaSessionCompat.Callback() {

            override fun onPlay() {
                // ????????????????????????????????????????????????????????????????????? onPause
                registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
                // ??????????????????
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (isAudioFocus) {
                        audioManager.requestAudioFocus(audioFocusRequest)
                    }
                }

                musicController.mediaPlayer.start()
                musicController.isPlaying().value = musicController.mediaPlayer.isPlaying
                musicController.sendMusicBroadcast()
                updateMediaSession()
                updateNotification()
            }

            override fun onPause() {
                musicController.mediaPlayer.pause()
                musicController.isPlaying().value = musicController.mediaPlayer.isPlaying
                musicController.sendMusicBroadcast()
                updateMediaSession()
                updateNotification()
            }

            override fun onSkipToNext() {
                musicController.playNext()
            }

            override fun onSkipToPrevious() {
                musicController.playPrevious()
            }

            override fun onStop() {
                // ????????????
                unregisterReceiver(myNoisyAudioStreamReceiver)
            }

            override fun onSeekTo(pos: Long) {
                if (musicController.isPrepared) {
                    musicController.mediaPlayer.seekTo(pos.toInt())
                    updateMediaSession()
                }
            }

        }
        // ????????? MediaSession
        mediaSession = MediaSessionCompat(this, "mbr").apply {
            // ?????????????????????????????????
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            // ?????? Callback
            setCallback(mediaSessionCallback, Handler(Looper.getMainLooper()))
            // ??? MediaSession ?????? active???????????????????????????????????????
            isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getIntExtra("int_code", 0)) {
            CODE_PREVIOUS -> musicController.playPrevious()
            CODE_PLAY -> {
                if (musicController.isPlaying().value == true) {
                    musicController.pause()
                } else {
                    musicController.play()
                }
            }
            CODE_NEXT -> musicController.playNext()
        }
        // ???????????????
        return START_NOT_STICKY
    }

    /**
     * ??????
     */
    override fun onBind(p0: Intent?): IBinder = musicController

    /**
     * ??????
     */
    override fun onUnbind(intent: Intent?): Boolean = super.onUnbind(intent)

    override fun onDestroy() {
        super.onDestroy()
        // ?????? mediaSession
        mediaSession?.let {
            it.setCallback(null)
            it.release()
        }
        musicController.mediaPlayer.release()
    }

    /**
     * inner class Music Controller
     */
    inner class MusicController : Binder(), MusicControllerInterface, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

        /** MediaPlayer */
        val mediaPlayer: MediaPlayer = MediaPlayer()

        /* ?????????????????????????????? */
        var statusBarLyric = mmkv.decodeBool(AppConfig.MEIZU_STATUS_BAR_LYRIC, true)

        /* ??????????????? */
        private var recover = false

        /* ??????????????????????????? */
        private var recoverProgress = 0

        private var songData = MutableLiveData<StandardSongData?>()

        private val isSongPlaying = MutableLiveData<Boolean>().also {
            it.value = mediaPlayer.isPlaying
        }

        // ????????????????????????
        var isPrepared = false

        /* Song cover bitmap*/
        private val coverBitmap = MutableLiveData<Bitmap?>()

        /* ?????????????????? FM ?????? */
        var personFM = MutableLiveData<Boolean>().also {
            it.value = mmkv.decodeBool(AppConfig.PERSON_FM_MODE, false)
        }

        override fun setPersonFM(open: Boolean) {
            if (open) {
                personFM.value = true
                mmkv.encode(AppConfig.PERSON_FM_MODE, true)

                mode = MODE_CIRCLE
                PlayQueue.normal()
                // ?????????????????????
                mmkv.encode(AppConfig.PLAY_MODE, mode)
                sendMusicBroadcast()
                // ?????? FM
                PersonalFMAPI.get({
                    runOnMainThread {
                        PlayQueue.setNormal(it)
                        playMusic(it[0])
                    }
                }, {
                    toast("?????? FM ??????????????????")
                })
            } else {
                personFM.value = false
                mmkv.encode(AppConfig.PERSON_FM_MODE, false)
            }
        }

        override fun setPlaylist(songListData: ArrayList<StandardSongData>) {
            PlayQueue.setNormal(songListData)
            if (mode == MODE_RANDOM && !recover) {
                PlayQueue.random()
            }
        }

        override fun getPlaylist(): ArrayList<StandardSongData>? = PlayQueue.currentQueue.value

        override fun playMusic(song: StandardSongData, playNext: Boolean) {
            isPrepared = false
            songData.value = song
            // ????????????????????????
            mmkv.encode(AppConfig.SERVICE_CURRENT_SONG, song)
            Log.e(TAG, "onDestroy: ??????????????????????????? mmkv???${song.name}")

            // MediaPlayer ??????
            mediaPlayer.reset()
            // ?????????
            mediaPlayer.apply {
                ServiceSongUrl.getUrlProxy(song) {
                    runOnMainThread {
                        if (it == null || it is String && it.isEmpty()) {
                            if (playNext) {
                                toast("?????????????????????, ???????????????")
                                playNext()
                            }
                            return@runOnMainThread
                        }
                        when (it) {
                            is String -> {
                                if (!InternetState.isWifi(context) && !mmkv.decodeBool(
                                        AppConfig.PLAY_ON_MOBILE,
                                        false
                                    )
                                ) {
                                    toast("?????????????????????????????????????????????????????????????????????????????????")
                                    return@runOnMainThread
                                } else {
                                    try {
                                        setDataSource(it)
                                    } catch (e: Exception) {
                                        onError(mediaPlayer, -1, 0)
                                        return@runOnMainThread
                                    }
                                }
                            }
                            is Uri -> {
                                try {
                                    setDataSource(applicationContext, it)
                                } catch (e: Exception) {
                                    onError(mediaPlayer, -1, 0)
                                    return@runOnMainThread
                                }
                            }
                            else -> {
                                return@runOnMainThread
                            }
                        }
                        setOnPreparedListener(this@MusicController) // ???????????????????????????
                        setOnCompletionListener(this@MusicController) // ????????????????????????
                        setOnErrorListener(this@MusicController)
                        prepareAsync()
                    }
                }
            }

        }

        fun sendMusicBroadcast() {
            // Service ??????
            val intent = Intent("com.szong.music.MUSIC_BROADCAST")
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }

        override fun onPrepared(p0: MediaPlayer?) {
            Log.i(TAG, "onPrepared")
            isPrepared = true
            this.play()
            if (recover) {
                recover = false
                this.pause()
                this.setProgress(0)
                // this.setProgress(recoverProgress)
            }
            sendMusicBroadcast()

            songData.value?.let { standardSongData ->
                // ????????????
                ServiceSongUrl.getLyric(standardSongData) {
                    val mainLyricText = it.lyric
                    val secondLyricText = it.secondLyric
                    lyricEntryList.clear()
                    App.coroutineScope.launch {
                        loge(arrayOf(mainLyricText, secondLyricText).toString())
                        val entryList = LyricUtil.parseLrc(arrayOf(mainLyricText, secondLyricText))
                        if (entryList != null && entryList.isNotEmpty()) {
                            lyricEntryList.addAll(entryList)
                        }
                        lyricEntryList.sort()
                        runOnMainThread {
                            liveLyricEntryList.value = lyricEntryList
                        }
                    }
                }
                // ????????????
                SongPicture.getPlayerActivityCoverBitmap(
                    this@MusicService.applicationContext,
                    standardSongData,
                    240.dp()
                ) { bitmap ->
                    runOnMainThread {
                        coverBitmap.value = bitmap
                        updateNotification()
                    }
                }
            }
            // ?????????????????????
            getPlayingSongData().value?.let {
                PlayHistoryAPI.addPlayHistory(it)
            }
        }

        override fun changePlayState() {
            isSongPlaying.value?.let {
                if (it) {
                    mediaSessionCallback?.onPause()
                } else {
                    mediaSessionCallback?.onPlay()
                }
                isSongPlaying.value = mediaPlayer.isPlaying
            }
            sendMusicBroadcast()
            updateNotification()
        }

        override fun play() {
            if (isPrepared) {
                mediaSessionCallback?.onPlay()
            }
        }

        override fun pause() {
            if (isPrepared) {
                mediaSessionCallback?.onPause()
                if (isAudioFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioManager.abandonAudioFocusRequest(audioFocusRequest)
                }
            }
        }

        override fun addToNextPlay(standardSongData: StandardSongData) {
            if (standardSongData == songData.value) {
                return
            }
            if (PlayQueue.currentQueue.value?.contains(standardSongData) == true) {
                PlayQueue.currentQueue.value?.remove(standardSongData)
            }
            val currentPosition = PlayQueue.currentQueue.value?.indexOf(songData.value) ?: -1
            PlayQueue.currentQueue.value?.add(currentPosition + 1, standardSongData)
        }

        override fun setAudioFocus(status: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (status != isAudioFocus) {
                    if (status) {
                        audioManager.requestAudioFocus(audioFocusRequest)
                    } else {
                        audioManager.abandonAudioFocusRequest(audioFocusRequest)
                    }
                    isAudioFocus = status
                    mmkv.encode(AppConfig.ALLOW_AUDIO_FOCUS, isAudioFocus)
                }
            }
        }

        override fun stopMusicService() {
            stopSelf(-1)
        }

        override fun getPlayerCover(): MutableLiveData<Bitmap?> = coverBitmap

        override fun getLyricEntryList(): MutableLiveData<ArrayList<LyricEntry>> = liveLyricEntryList

        override suspend fun getSongCover(size: Int?): Bitmap {
            return suspendCoroutine {
                if (size == null) {
                    coverBitmap.value?.let { bitmap ->
                        it.resume(bitmap)
                    }
                } else {
                    Log.e(TAG, "getSongCover: Coil ??????????????????")
                    val request = ImageRequest.Builder(this@MusicService)
                        .size(size)
                        .data(coverBitmap.value)
                        .error(R.drawable.ic_song_cover)
                        .target(
                            onStart = {
                                // Handle the placeholder drawable.
                            },
                            onSuccess = { result ->
                                Log.e(TAG, "getSongCover: Coil ??????????????????")
                                it.resume(result.toBitmap())
                            },
                            onError = { _ ->
                                Log.e(TAG, "getSongCover: Coil ??????????????????")
                                ContextCompat.getDrawable(this@MusicService, R.drawable.ic_song_cover)?.let { it1 ->
                                    it.resume(it1.toBitmap(size, size))
                                }
                            }
                        )
                        .build()
                    this@MusicService.imageLoader.enqueue(request)
                }
            }
        }

        override fun setRecover(value: Boolean) {
            recover = value
        }

        override fun setRecoverProgress(value: Int) {
            recoverProgress = value
        }

        override fun isPlaying(): MutableLiveData<Boolean> = isSongPlaying

        override fun getDuration(): Int = if (isPrepared) {
            mediaPlayer.duration
        } else {
            0
        }

        override fun getProgress(): Int = if (isPrepared) {
            mediaPlayer.currentPosition
        } else {
            0
        }

        override fun setProgress(newProgress: Int) {
            mediaSessionCallback?.onSeekTo(newProgress.toLong())
        }

        override fun getPlayingSongData(): MutableLiveData<StandardSongData?> = songData

        override fun changePlayMode() {
            when (mode) {
                MODE_CIRCLE -> mode = MODE_REPEAT_ONE
                MODE_REPEAT_ONE -> {
                    mode = MODE_RANDOM
                    PlayQueue.random()
                }
                MODE_RANDOM -> {
                    mode = MODE_CIRCLE
                    PlayQueue.normal()
                }
            }
            // ?????????????????????
            mmkv.encode(AppConfig.PLAY_MODE, mode)
            sendMusicBroadcast()
        }

        override fun getPlayMode(): Int = mode

        override fun playPrevious() {
            PlayQueue.currentQueue.value?.previous(songData.value)?.let {
                playMusic(it)
            }
        }

        override fun playNext() {
            if (personFM.value == true) {
                getPlayingSongData().value?.let {
                    val index = PlayQueue.currentQueue.value?.indexOf(it)
                    if (index == PlayQueue.currentQueue.value?.lastIndex) {
                        PersonalFMAPI.get({ list ->
                            runOnMainThread {
                                PlayQueue.setNormal(list)
                                playMusic(list[0])
                            }
                        }, {
                            toast("???????????? FM ??????")
                        })
                        return
                    }
                }
            }
            PlayQueue.currentQueue.value?.next(songData.value)?.let {
                playMusic(it)
            }
        }

        override fun getNowPosition(): Int {
            return PlayQueue.currentQueue.value?.indexOf(songData.value) ?: -1
        }

        override fun getAudioSessionId(): Int {
            return mediaPlayer?.audioSessionId ?: 0
        }

        override fun sendBroadcast() {
            sendMusicBroadcast()
        }

        override fun setSpeed(speed: Float) {
            this@MusicService.speed = speed
            setPlaybackParams()
        }

        override fun getSpeed(): Float = speed

        override fun getPitchLevel(): Int = pitchLevel

        override fun increasePitchLevel() {
            pitchLevel++
            val value = pitchUnit * (pitchLevel + 1f / pitchUnit)
            if (value < 1.5f) {
                pitch = value
                setPlaybackParams()
            } else {
                decreasePitchLevel()
            }
        }

        override fun decreasePitchLevel() {
            pitchLevel--
            val value = pitchUnit * (pitchLevel + 1f / pitchUnit)
            if (value > 0.5f) {
                pitch = value
                setPlaybackParams()
            } else {
                increasePitchLevel()
            }
        }

        private fun setPlaybackParams() {
            if (isPrepared) {
                mediaPlayer.let {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val playbackParams = it.playbackParams
                            // playbackParams.speed = speed // 0 ????????????
                            playbackParams.pitch = pitch
                            it.playbackParams = playbackParams
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        }

        override fun onCompletion(p0: MediaPlayer?) {
            autoPlayNext()
        }

        private fun autoPlayNext() {
            if (mode == MODE_REPEAT_ONE) {
                setProgress(0)
                play()
                return
            }
            playNext()
        }

        override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
            if (mmkv.decodeBool(AppConfig.SKIP_ERROR_MUSIC, true)) {
                // ???????????????
                // toast("???????????? (${p1},${p2}) ????????????????????????")
                playNext()
            } else {
                toast("???????????? (${p1},${p2})")
            }
            return true
        }

        fun getCurrentRight() = currentRight
        fun setCurrentRight(newOne: Int) {
            currentRight = newOne
        }

        fun getCurrentCustom() = currentCustom
        fun setCurrentCustom(newOne: Int) {
            currentCustom = newOne
        }

        fun getTimingOffMode() = timingOffMode
        fun setTimingOffMode(newOne: Boolean) {
            timingOffMode = newOne
        }
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getPendingIntentActivity(): PendingIntent {
        val intentMain = Intent(this, MainActivity::class.java)


        val intentPlayer = Intent(this, PlayerActivity::class.java)


        val intents = arrayOf(intentMain, intentPlayer)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivities(this, 1, intents, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivities(this, 1, intents, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    }

    private fun getPendingIntentPrevious(): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("int_code", CODE_PREVIOUS)
        return buildServicePendingIntent(this, 2, intent)
    }

    private fun getPendingIntentPlay(): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("int_code", CODE_PLAY)
        return buildServicePendingIntent(this, 3, intent)
    }

    private fun getPendingIntentNext(): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("int_code", CODE_NEXT)
        return buildServicePendingIntent(this, 4, intent)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun buildServicePendingIntent(context: Context, requestCode: Int, intent: Intent): PendingIntent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    /**
     * ????????????
     */
    private fun updateNotification(fromLyric: Boolean = false) {
        val song = musicController.getPlayingSongData().value
        GlobalScope.launch {
            val bitmap = if (mmkv.decodeBool(AppConfig.INK_SCREEN_MODE, false)) {
                R.drawable.ic_song_cover.asDrawable(App.context)?.toBitmap(128.dp(), 128.dp())
            } else {
                musicController.getSongCover(128.dp())
            }
            runOnMainThread {
                showNotification(fromLyric, song, bitmap)
            }
        }
    }

    /**
     * ????????????
     */
    private fun showNotification(fromLyric: Boolean = false, song: StandardSongData?, bitmap: Bitmap?) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_music_launcher_foreground)
            setLargeIcon(bitmap)
            setContentTitle(song?.name)
            setContentText(song?.artists?.parse())
         //   setContentIntent(getPendingIntentActivity())
            addAction(R.drawable.ic_round_skip_previous_24, "Previous", getPendingIntentPrevious())
            addAction(getPlayIcon(), "play", getPendingIntentPlay())
            addAction(R.drawable.ic_round_skip_next_24, "next", getPendingIntentNext())
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            setOngoing(true)
            if (getCurrentLineLyricEntry()?.text != null && fromLyric && musicController.statusBarLyric) {
                setTicker(getCurrentLineLyricEntry()?.text) // ????????????????????????????????????
            }
            // .setAutoCancel(true)
        }.build()

        notification.extras.putInt("ticker_icon", R.drawable.ic_music_launcher_foreground)
        notification.extras.putBoolean("ticker_icon_switch", false)
        notification.flags = notification.flags.or(FLAG_ALWAYS_SHOW_TICKER)
        // ??????????????? Ticker
        if (fromLyric) {
            notification.flags = notification.flags.or(FLAG_ONLY_UPDATE_TICKER)
        }
        // ????????????
        startForeground(START_FOREGROUND_ID, notification)
    }

    /**
     * ??????????????????????????????
     */
    private fun getPlayIcon(): Int {
        return if (musicController.isPlaying().value == true) {
            R.drawable.ic_round_pause_24
        } else {
            R.drawable.ic_round_play_arrow_24
        }
    }

    private fun getCurrentLineLyricEntry(): LyricEntry? {
        val progress = musicController.getProgress()
        val line = findShowLine(progress.toLong())
        if (line <= lyricEntryList.lastIndex) {
            return lyricEntryList[line]
        }
        return null
    }

    /**
     * ??????????????????????????????????????????????????????????????? <= time ????????????
     */
    private fun findShowLine(time: Long): Int {
        if (lyricEntryList.isNotEmpty()) {
            var left = 0
            var right = lyricEntryList.size
            while (left <= right) {
                val middle = (left + right) / 2
                val middleTime = lyricEntryList[middle].time
                if (time < middleTime) {
                    right = middle - 1
                } else {
                    if (middle + 1 >= lyricEntryList.size || time < lyricEntryList[middle + 1].time) {
                        return middle
                    }
                    left = middle + 1
                }
            }
        }
        return 0
    }

    /**
     * ??????????????????
     * ???????????? ????????????????????????????????????????????????????????????????????????????????? ?????????????????????
     */
    private fun updateMediaSession() {
        val song = musicController.getPlayingSongData().value
        mediaSession?.apply {
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(
                        if (musicController.isPlaying().value == true) {
                            PlaybackStateCompat.STATE_PLAYING
                        } else {
                            PlaybackStateCompat.STATE_PAUSED
                        },
                        musicController.getProgress().toLong(),
                        MEDIA_SESSION_PLAYBACK_SPEED
                    )
                    // ?????????????????????
                    .setActions(MEDIA_SESSION_ACTIONS)
                    .build()
            )
            setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song?.name)
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        song?.artists?.parse() // + " - " + song?.album
                    )
                    // ?????????????????????????????????????????????????????? MediaSession ???????????????????????????
//                    .putBitmap(
//                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
//                        musicController.getPlayerCover().value
//                    )
                    .putLong(
                        MediaMetadata.METADATA_KEY_DURATION,
                        musicController.getDuration().toLong()
                    )
                    .build()
            )
        }

    }

    /**
     * ????????????
     */
    companion object {
        private val TAG = this::class.java.simpleName

        /* Flyme ??????????????? TICKER ???????????? */
        private const val FLAG_ALWAYS_SHOW_TICKER = 0x1000000

        /* ????????? Flyme ??????????????? */
        private const val FLAG_ONLY_UPDATE_TICKER = 0x2000000

        /* MSG ??????????????? */
        private const val MSG_STATUS_BAR_LYRIC = 0

        private const val MEDIA_SESSION_PLAYBACK_SPEED = 1f
    }
}