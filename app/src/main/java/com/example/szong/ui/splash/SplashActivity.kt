package com.example.szong.ui.splash

//import android.content.Intent
//import com.example.szong.databinding.ActivitySplashBinding
//import com.example.szong.ui.base.BaseActivity
//import com.example.szong.ui.main.MainActivity
//import android.os.Handler
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.szong.R
import com.example.szong.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var appname: TextView
    private lateinit var lottie:LottieAnimationView
    private lateinit var appimage:LottieAnimationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        appname = findViewById(R.id.appname)
        lottie = findViewById(R.id.lottie)
//        appimage = findViewById(R.id.appimage)
        appname.animate().translationY(-600f).setDuration(2700).setStartDelay(0)
//        lottie.animate().translationX(2000f).setDuration(2000).setStartDelay(0)
        Handler().postDelayed(object : Runnable {
            override fun run(){
                val intent = Intent(applicationContext,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        },3000)
    }
}
//class SplashActivity : BaseActivity() {
//
//    private lateinit var binding: ActivitySplashBinding
//
//    override fun initBinding() {
//        binding = ActivitySplashBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        binding.appname.animate().translationY(-600f).setDuration(2700).setStartDelay(0)
////        lottie.animate().translationX(2000f).setDuration(2000).setStartDelay(0)
//        Handler().postDelayed(object : Runnable {
//            override fun run(){
//                val intent = Intent(applicationContext,MainActivity::class.java)
//                startActivity(intent)
//            }
//        },3000)
//        finish()
//    }

