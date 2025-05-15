package com.example.project

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeUtils {

    private const val PREF_NAME = "theme_pref"       // اسم ملف الـ SharedPreferences
    private const val THEME_KEY = "is_dark_mode"     // المفتاح المستخدم لحفظ الوضع

    // 🌓 تطبق الثيم عند بدء التطبيق
    fun applyTheme(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean(THEME_KEY, false)

        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }

    // 🔄 تبديل الوضع وتخزين القيمة الجديدة
    fun toggleTheme(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean(THEME_KEY, false)

        // عكس القيمة وتخزينها
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, !isDarkMode)
            .apply()

        val newMode = if (!isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        AppCompatDelegate.setDefaultNightMode(newMode)
    }
}
