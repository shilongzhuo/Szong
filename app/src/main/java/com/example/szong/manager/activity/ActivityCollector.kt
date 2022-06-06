package com.example.szong.manager.activity

import android.app.Activity

/**
 * ActivityCollector
 * 作用是存放当前存活的Activity的引用，以便可以在合适的时候finish它们
 */
object ActivityCollector {

    private val activities = ArrayList<Activity>()

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
    }

}