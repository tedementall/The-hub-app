package com.example.thehub.ui.utils

import android.app.Activity
import android.content.Intent
import com.example.thehub.R
import com.example.thehub.ui.home.HomeActivity
import com.example.thehub.ui.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

object BottomNavHelper {

    fun setup(activity: Activity, bottom: BottomNavigationView, selected: Int) {
        bottom.selectedItemId = selected
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    if (activity !is HomeActivity) {
                        activity.startActivity(Intent(activity, HomeActivity::class.java))
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        activity.finish()
                    }
                    true
                }
                R.id.menu_search -> { /* TODO */ true }
                R.id.menu_cart   -> { /* TODO */ true }
                R.id.menu_profile-> { /* TODO */ true }
                R.id.menu_settings -> {
                    if (activity !is SettingsActivity) {
                        activity.startActivity(Intent(activity, SettingsActivity::class.java))
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        activity.finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}
