package com.example.szong

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.szong.config.Config
import com.example.szong.database.room.AppDatabase
import com.example.szong.manager.ActivityManager
import com.example.szong.util.net.ChineseIPData
import com.example.szong.util.theme.DarkThemeUtil
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // 全局 context
        context = applicationContext
        // MMKV 初始化
        MMKV.initialize(context)
        mmkv = MMKV.defaultMMKV()

        // activity 管理
        activityManager = ActivityManager()
        // 初始化数据库
        appDatabase = AppDatabase.getDatabase(this)
        // 安全检查
        checkSecure()

        if (mmkv.decodeBool(Config.DARK_THEME, false)) {
            DarkThemeUtil.setDarkTheme(true)
        }

      /**  realIP = "0.0.0.0"
        coroutineScope.launch {
            val lastIP = "LAST_IP"
            val lastIPExpiredTime = "LAST_IP_TIME" // 过期时间
            val ip = mmkv.decodeString(lastIP, "")
            val now = System.currentTimeMillis()
            val expiredTime = mmkv.decodeLong(lastIPExpiredTime, now)
            if (ip == null || ip.isEmpty() || expiredTime < now) {
                Log.i(TAG, "ip is expired.")
                realIP = ChineseIPData.getRandomIP(this@App)
                mmkv.encode(lastIP, realIP)
                mmkv.encode(lastIPExpiredTime, now + 24 * 60 * 60 * 1000)
            } else{
                realIP = ip
            }
        }*/

    }
    /**
     * 安全检查
     */
    private fun checkSecure() {
       /** if (Secure.isSecure()) {
            // 初始化友盟
            // UMConfigure.init(context, UM_APP_KEY, "", UMConfigure.DEVICE_TYPE_PHONE, "")
            // 选用 AUTO 页面采集模式
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
            // 开启音乐服务
            startMusicService()
        } else {
            Secure.killMyself()
        }*/
    }

    /**
     *  启动音乐服务
    private fun startMusicService() {
        // 通过 Service 播放音乐，混合启动
        val intent = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        // 绑定服务
        bindService(intent, musicServiceConnection, BIND_AUTO_CREATE)
    }
*/

    companion object {

        private val TAG = this::class.java.simpleName

       // const val UM_APP_KEY = "5fb38e09257f6b73c0961382"

        lateinit var mmkv: MMKV

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        lateinit var activityManager: ActivityManager

        val coroutineScope = CoroutineScope(EmptyCoroutineContext)

        /** 数据库 */
        lateinit var appDatabase: AppDatabase

        lateinit var realIP: String
    }


}