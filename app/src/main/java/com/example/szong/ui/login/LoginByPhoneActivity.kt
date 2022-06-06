package com.example.szong.ui.login

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.example.szong.R
import com.example.szong.databinding.ActivityLoginByPhoneBinding
import com.example.szong.manager.activity.ActivityCollector
import com.example.szong.manager.user.NeteaseUser
import com.example.szong.ui.base.BaseActivity
import com.example.szong.ui.cloudapi.NeteaseCloudMusicApiActivity
import com.example.szong.ui.login.viewmodel.LoginCellphoneViewModel
import com.example.szong.util.app.runOnMainThread
import com.example.szong.util.data.md5
import com.example.szong.util.security.SkySecure
import com.example.szong.widget.toast


class LoginByPhoneActivity : BaseActivity() {

    private val loginCellphoneViewModel: LoginCellphoneViewModel by viewModels()

    lateinit var binding: ActivityLoginByPhoneBinding

    override fun initBinding() {
        if (getString(R.string.app_name).md5() == SkySecure.getAppNameMd5()) {
            binding = ActivityLoginByPhoneBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } else {
            ActivityCollector.finishAll()
        }
    }

    override fun initListener() {

        binding.itemNeteaseCloudMusicApi.setOnClickListener {
            startActivity(Intent(this, NeteaseCloudMusicApiActivity::class.java))
        }

        binding.btnLoginByPhone.setOnClickListener {
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            if (phone == "" || password == "") {
                toast("请输入手机号或密码")
            } else {
                binding.btnLoginByPhone.visibility = View.GONE
                binding.llLoading.visibility = View.VISIBLE
                binding.lottieLoading.repeatCount = -1
                binding.lottieLoading.playAnimation()
                loginCellphoneViewModel.loginByCellphone(NeteaseUser.neteaseCloudMusicApi, phone, password, {
                    // 发送广播
                    val intent = Intent("com.example.szong.LOGIN")
                    intent.setPackage(packageName)
                    sendBroadcast(intent)
                    // 通知 Login 关闭
                    setResult(RESULT_OK, Intent())
                    finish()
                }, { code ->
                    runOnMainThread {
                        binding.btnLoginByPhone.visibility = View.VISIBLE
                        binding.llLoading.visibility = View.GONE
                        binding.lottieLoading.cancelAnimation()
                        if (code == 250) {
                            toast("错误代码：250\n当前登录失败，请稍后再试")
                        } else {
                            toast("登录失败，请检查服务、用户名或密码")
                        }
                    }
                })
            }
        }
    }

}