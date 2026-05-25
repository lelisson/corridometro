package com.corridometro.data.settings

import android.content.Context
import android.content.SharedPreferences

class AppSettings(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var darkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS, value).apply()

    companion object {
        private const val PREFS_NAME = "corridometro_app_settings"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
    }
}
