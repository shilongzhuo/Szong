package com.example.szong.ui.login

import android.content.Intent
import android.graphics.Typeface
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.szong.App
import com.example.szong.databinding.ActivityLogin3Binding
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.ui.base.BaseActivity
import com.example.szong.util.ui.opration.getStatusBarHeight
import com.example.szong.widget.toast

/**
 * LoginActivity3 界面，取代原来的 LoginActivity2
 */
class LoginActivity3 : BaseActivity() {

    private lateinit var binding: ActivityLogin3Binding

    override fun initBinding() {
        binding = ActivityLogin3Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
//        if (MyApp.mmkv.decodeBool(Config.USER_NETEASE_CLOUD_MUSIC_API_ENABLE, false)) {
//            binding.btnLoginByPhone.visibility = View.VISIBLE
//        } else {
//            binding.btnLoginByPhone.visibility = View.GONE
//        }

        (binding.btnCancel.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = getStatusBarHeight(window, this@LoginActivity3)
        }

        binding.lottieBackground.repeatCount = -1
        binding.lottieBackground.playAnimation()
        binding.lottieBackground.speed = 1f

        try {
            val typeface = Typeface.createFromAsset(assets, "fonts/Moriafly-Regular.ttf")
            binding.tvLogo.typeface = typeface
            binding.tvVersion.typeface = typeface
        } catch (e: Exception) {
            toast("LOGIN ERROR")
        }
    }

    override fun initListener() {
        toast("LOGINING")
        binding.apply{
            // 取消
            btnCancel.setOnClickListener { finish() }

            // 手机号登录
            btnLoginByPhone.setOnClickListener {
                App.activityManager.startLoginByPhoneActivity(this@LoginActivity3)
            }
            // UID 登录
            btnLoginByUid.setOnClickListener {
                App.activityManager.startLoginByUidActivity(this@LoginActivity3)
            }

            //二维码登录
            btnLoginByQRCode.setOnClickListener {
                if (NeteaseUser.neteaseCloudMusicApi.isNotEmpty()) {
                    startActivityForResult(Intent(this@LoginActivity3, LoginByQRCodeActivity::class.java), 0)
                } else {
                    toast("请先配置网易云API再使用二维码登录")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.lottieBackground.cancelAnimation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> if (resultCode == RESULT_OK) {
                finish()
            }
        }
    }

}