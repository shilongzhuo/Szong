package com.example.szong.ui.activity

import android.content.Intent
import com.example.szong.databinding.ActivitySplashBinding
import com.example.szong.ui.base.BaseActivity
import com.example.szong.ui.main.MainActivity

/**
 * 启动页 Activity
 */
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun initBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}