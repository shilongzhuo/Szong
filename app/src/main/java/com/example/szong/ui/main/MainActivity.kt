package com.example.szong.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.szong.App
import com.example.szong.R
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.viewpager2.widget.ViewPager2
import com.example.szong.config.Config
import com.example.szong.databinding.ActivityMainBinding
import com.example.szong.ui.base.BaseActivity
import com.example.szong.ui.main.viewmodel.MainViewModel
import com.example.szong.util.cache.ACache
import com.example.szong.util.runOnMainThread
import com.example.szong.util.view.ViewPager2Util
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.android.material.tabs.TabLayoutMediator
import dp
import eightbitlab.com.blurview.RenderScriptBlur
import kotlin.concurrent.thread

class MainActivity : BaseActivity() {

    companion object {
        private const val ACTION_LOGIN = "com.example.szong.LOGIN"
    }

    private lateinit var binding: ActivityMainBinding

    /* 登录广播接受 */
    private lateinit var loginReceiver: LoginReceiver

    /* 设置改变广播接收 */
    private lateinit var settingsChangeReceiver: SettingsChangeReceiver

    private val mainViewModel: MainViewModel by viewModels()

    override fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            mainViewModel.statusBarHeight.value =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    insets.getInsets(WindowInsets.Type.navigationBars()).top
                } else {
                    insets.systemWindowInsetTop
                }

            mainViewModel.navigationBarHeight.value =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    insets.getInsets(WindowInsets.Type.navigationBars()).bottom
                } else {
                    insets.systemWindowInsetBottom
                }
            insets
        }
        binding.composeViewMenu.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppCompatTheme {
                    MainMenu(this@MainActivity, mainViewModel)
                }
            }
        }
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initData() {
        // Intent 过滤器
       /** var intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_LOGIN)
        loginReceiver = LoginReceiver()
        registerReceiver(loginReceiver, intentFilter)

        intentFilter = IntentFilter()
        intentFilter.addAction(SettingsActivity.ACTION)
        settingsChangeReceiver = SettingsChangeReceiver()
        registerReceiver(settingsChangeReceiver, intentFilter)
       */
    }

    override fun initView() {
        thread {
            ACache.get(this).getAsBitmap(Config.APP_THEME_BACKGROUND)?.let {
                runOnMainThread {
                    binding.ivTheme.setImageBitmap(it)
                }
            }
        }

        /**
         * 音乐播放栏的毛玻璃特效
         */
        val radius = 20f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        binding.blurViewPlay.setupWith(decorView.findViewById(R.id.clTheme))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)

        binding.viewPager2.offscreenPageLimit = 2
        binding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> MyFragment()
                    else -> HomeFragment()
                }
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.my)
                else -> getString(R.string.find)
            }
        }.attach()

        val select = App.mmkv.decodeInt(Config.SELECT_FRAGMENT, 0)
        binding.viewPager2.setCurrentItem(select, false)

        ViewPager2Util.changeToNeverMode(binding.viewPager2)
    }


    override fun initListener() {
        with(binding) {
            // 搜索按钮
          /**  ivSearch.setOnClickListener {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                overridePendingTransition(
                    R.anim.anim_alpha_enter,
                    R.anim.anim_no_anim
                )*/
            }
            // 设置按钮
            binding.ivSettings.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }

            binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    App.mmkv.encode(Config.SELECT_FRAGMENT, position)
                }
            })
        }


    override fun initObserver() {
        mainViewModel.statusBarHeight.observe(this) {
            (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply {
                height = 56.dp() + it
            }
            (binding.viewPager2.layoutParams as ConstraintLayout.LayoutParams).apply {
                topMargin = 56.dp() + it
            }
        }
        mainViewModel.navigationBarHeight.observe(this) {
            binding.miniPlayer.root.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = it
            }
            binding.blurViewPlay.updateLayoutParams<ConstraintLayout.LayoutParams> {
                height = 64.dp() + it
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑广播接收
        unregisterReceiver(loginReceiver)
        unregisterReceiver(settingsChangeReceiver)

    }

    inner class LoginReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // 通知 viewModel
            mainViewModel.setUserId()
        }
    }

    inner class SettingsChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mainViewModel.updateUI()
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            binding.drawerLayout.close()
        } else {
            super.onBackPressed()
        }
    }

    private var startX = 0
    private var startY = 0
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x.toInt()
                startY = ev.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x.toInt()
                val endY = ev.y.toInt()
                val disX = kotlin.math.abs(endX - startX)
                val disY = kotlin.math.abs(endY - startY)
                if (disX < disY) {
                    // 禁止 ViewPager2
                    binding.viewPager2.isUserInputEnabled = false
                }
            }
            MotionEvent.ACTION_UP -> {
                startX = 0
                startY = 0
                // 恢复
                binding.viewPager2.isUserInputEnabled = true
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}