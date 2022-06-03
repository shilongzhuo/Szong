package com.example.szong.manager.user

import EMPTY
import android.os.Parcelable
import com.example.szong.App.Companion.mmkv
import com.example.szong.config.AppConfig
import com.example.szong.api.user.auth.netease.UserDetailData
import kotlinx.parcelize.Parcelize


private const val DEFAULT_UID = 0L
private const val DEFAULT_VIP_TYPE = 0

/**
 * 网易云音乐用户
 */
object NeteaseUser {

    val szongUser: SzongUser = mmkv.decodeParcelable(AppConfig.SZONG_USER, SzongUser::class.java, SzongUser())

    /** 用户 uid */
    var uid: Long = DEFAULT_UID
        get() = mmkv.decodeLong(AppConfig.UID, DEFAULT_UID)
        set(value) {
            mmkv.encode(AppConfig.UID, value)
            field = value
        }

    /** 用户 Cookie */
    var cookie: String = String.EMPTY
        get() = mmkv.decodeString(AppConfig.CLOUD_MUSIC_COOKIE, String.EMPTY)
        set(value) {
            mmkv.encode(AppConfig.CLOUD_MUSIC_COOKIE, value)
            field = value
        }

    /**
     * 获取用户配置的 NeteaseCloudMusicApi
     */
    var neteaseCloudMusicApi: String = String.EMPTY
        get() = mmkv.decodeString(AppConfig.USER_NETEASE_CLOUD_MUSIC_API_URL, String.EMPTY)
        set(value) {
            mmkv.encode(AppConfig.USER_NETEASE_CLOUD_MUSIC_API_URL, value)
            field = value
        }

    /**
     * 用户 VIP 类型
     */
    var vipType: Int = DEFAULT_VIP_TYPE
        get() = mmkv.decodeInt(AppConfig.VIP_TYPE, DEFAULT_VIP_TYPE)
        set(value) {
            mmkv.encode(AppConfig.VIP_TYPE, value)
            field = value
        }

    /** 是否通过 uid 登录 */
    val isUidLogin: Boolean
        get() {
            val uid = mmkv.decodeLong(AppConfig.UID, DEFAULT_UID)
            return uid != DEFAULT_UID
        }

    /** 是否有 cookie */
    val hasCookie: Boolean
        get() = cookie.isNotEmpty()

    /**
     * 是否是 VIP 用户
     */
    fun isVip(): Boolean {
        return vipType != 0
    }

}

/**
 * Szong Music 用户
 */
@Parcelize
data class SzongUser(

    /** 昵称 */
    var nickname: String = String.EMPTY

): Parcelable {

    /**
     * 从网络更新用户数据
     */
    fun updateFromNet(userDetailData: UserDetailData) {
        nickname = userDetailData.profile.nickname
        save()
   }

    /**
     * 保存数据
     */
    private fun save() {
        mmkv.encode(AppConfig.SZONG_USER, this)
    }

}



