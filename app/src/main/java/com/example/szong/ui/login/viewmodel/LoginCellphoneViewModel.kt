package com.example.szong.ui.login.viewmodel

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import com.example.szong.api.user.auth.netease.UserDetailData
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.util.data.EMPTY
import com.example.szong.util.net.MagicHttp
import com.example.szong.util.net.status.ErrorCode
import com.example.szong.util.security.SkySecure
import com.google.gson.Gson
import okhttp3.FormBody
@Keep
class LoginCellphoneViewModel : ViewModel() {

    /**
     * 手机号登录
     */
    fun loginByCellphone(
        api: String,
        phone: String,
        password: String,
        success: (UserDetailData) -> Unit,
        failure: (Int) -> Unit
    ) {
        val passwordMD5 = SkySecure.getMD5(password)
        val requestBody = FormBody.Builder()
            .add("phone", phone)
            .add("countrycode", "86")
            .add("md5_password", passwordMD5)
            .build()
        MagicHttp.OkHttpManager().newPost("${api}/login/cellphone", requestBody, {
            try {
                val userDetail = Gson().fromJson(it, UserDetailData::class.java)
                if (userDetail.code != 200) {
                    failure.invoke(userDetail.code)
                } else {
                    // 更新 User 信息
                    NeteaseUser.apply {
                        cookie = userDetail.cookie ?: String.EMPTY
                        uid = userDetail.profile.userId
                        vipType = userDetail.profile.vipType
                    }
                    success.invoke(userDetail)
                }
            } catch (e: Exception) {
                failure.invoke(ErrorCode.ERROR_MAGIC_HTTP)
            }
        }, {
            failure.invoke(ErrorCode.ERROR_MAGIC_HTTP)
        })
    }

}