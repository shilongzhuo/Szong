package com.example.szong.ui.login

import android.content.Intent
import com.example.szong.App
import com.example.szong.databinding.ActivityLoginByUidBinding
import com.example.szong.ui.base.BaseActivity
import com.example.szong.util.app.loge
import com.example.szong.widget.toast

import java.util.regex.Pattern

/**
 * 通过网易云 UID 登录
 */
class LoginByUidActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginByUidBinding

    override fun initBinding() {
        binding = ActivityLoginByUidBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initListener() {
        // 点击登录按钮
        binding.btnLogin.setOnClickListener {
            // 获取输入
            var netease = binding.etUid.text.toString()

            // 判断是否直接是网易云分享用户链接
            if (netease != "") {
                val index = netease.indexOf("id=")
                if (index != -1) {
                    netease = netease.subSequence(index + 3, netease.length).toString()
                }
                netease = keepDigital(netease)
                loge("数字：${netease}")
                if (netease != "") {
                    App.cloudMusicManager.loginByUid(netease) {
                        // 发送广播
                        val intent = Intent("com.example.szong.LOGIN")
                        intent.setPackage(packageName)
                        sendBroadcast(intent)
                        // 通知 Login 关闭
                        setResult(RESULT_OK, Intent())
                        finish()
                    }
                } else {
                    toast("错误的 UID")
                }
            } else {
                toast("请输入 UID")
            }

        }

        /**
        // 帮助
        binding.tvHelp.setOnClickListener {
            App.activityManager.startWebActivity(this, "https://moriafly.xyz/foyou/uidlogin.html")
        }
        */
    }

    /**
     * 只保留数字
     */
    private fun keepDigital(oldString: String): String {
        val newString = StringBuffer()
        val matcher = Pattern.compile("\\d").matcher(oldString)
        while (matcher.find()) {
            newString.append(matcher.group())
        }
        return newString.toString()
    }

}