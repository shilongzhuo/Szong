package com.example.szong.manager.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.szong.ui.login.LoginByPhoneActivity
import com.example.szong.R
import com.example.szong.ui.artist.ArtistActivity
import com.example.szong.ui.comment.CommentActivity
import com.example.szong.ui.comment.PrivateLetterActivity
import com.example.szong.ui.localmusic.LocalMusicActivity
import com.example.szong.ui.login.LoginActivity3
import com.example.szong.ui.login.LoginByUidActivity
import com.example.szong.ui.player.PlayerActivity
import com.example.szong.ui.playlist.PlayHistoryActivity
import com.example.szong.ui.playlist.SongPlaylistActivity
import com.example.szong.ui.setting.SettingsActivity
import com.example.szong.ui.user.UserActivity
import com.example.szong.util.app.loge

object ActivityManager{

     fun startLoginActivity(activity: Activity) {
        val intent = Intent(activity, LoginActivity3::class.java)
        activity.startActivityForResult(intent, 0)
    }


    fun startUserActivity(context: Context, uid: Long) {
        val intent = Intent(context, UserActivity::class.java)
        intent.putExtra(UserActivity.EXTRA_LONG_USER_ID, uid)
        context.startActivity(intent)
    }

    fun startLocalMusicActivity(context: Context) {
        context.startActivity(Intent(context, LocalMusicActivity::class.java))
    }

    /**
     * 启动评论 activity
     */
    fun startCommentActivity(activity: Activity, source: Int, id: String) {
        val intent = Intent(activity, CommentActivity::class.java)
        intent.putExtra(CommentActivity.EXTRA_INT_SOURCE, source)
        intent.putExtra(CommentActivity.EXTRA_STRING_ID, id)
        activity.startActivity(intent)
        activity.overridePendingTransition(
            R.anim.anim_slide_enter_bottom,
            R.anim.anim_no_anim
        )
    }

    fun startUserActivity(activity: Activity, userId: Long) {
        val intent = Intent(activity, UserActivity::class.java)
        intent.putExtra(UserActivity.EXTRA_LONG_USER_ID, userId)
        activity.startActivity(intent)
    }

    fun startLoginByPhoneActivity(activity: Activity) {
        val intent = Intent(activity, LoginByPhoneActivity::class.java)
        activity.startActivityForResult(intent, 0)
//        activity.overridePendingTransition(
//            R.anim.anim_slide_enter_bottom,
//            R.anim.anim_no_anim
//        )
    }
    fun startLoginByUidActivity(activity: Activity) {
        val intent = Intent(activity, LoginByUidActivity::class.java)
        activity.startActivityForResult(intent, 0)
    }

   fun startSettingsActivity(activity: Activity) {
        val intent = Intent(activity, SettingsActivity::class.java)
        activity.startActivity(intent)
    }

    fun startPrivateLetterActivity(activity: Activity) {
        val intent = Intent(activity, PrivateLetterActivity::class.java)
        activity.startActivity(intent)
    }

    fun startPlayerActivity(activity: Activity) {
        loge("ActivityManager.startPlayActivity", "PLAY")
        val intent = Intent(activity, PlayerActivity::class.java)
        activity.startActivity(intent)
        activity.overridePendingTransition(
            R.anim.anim_slide_enter_bottom,
            R.anim.anim_no_anim
        )
    }



    fun startPlayHistoryActivity(activity: Activity) {
        val intent = Intent(activity, PlayHistoryActivity::class.java)
        activity.startActivity(intent)
    }

    fun startArtistActivity(activity: Activity, artistId: Long) {
        val intent = Intent(activity, ArtistActivity::class.java)
        intent.putExtra(ArtistActivity.EXTRA_LONG_ARTIST_ID, artistId)
        activity.startActivity(intent)
    }

    fun startPlaylistActivity(context: Context, tag: Int, id: String? = null) {
        val intent = Intent(context, SongPlaylistActivity::class.java)
        intent.putExtra(SongPlaylistActivity.EXTRA_TAG, tag)
        intent.putExtra(SongPlaylistActivity.EXTRA_ID, id)
        context.startActivity(intent)
    }

}