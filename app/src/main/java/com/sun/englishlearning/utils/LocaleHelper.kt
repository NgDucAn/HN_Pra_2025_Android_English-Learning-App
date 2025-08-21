package com.sun.englishlearning.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import java.util.Locale

object LocaleHelper {
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    private const val PREF_NAME = "app_prefs"

    fun onAttach(context: Context): Context {
        val lang = getPersistedData(context, getDefaultLanguage())
        return setLocale(context, lang)
    }

    fun setLocale(context: Context, language: String): Context {
        persist(context, language)
        return updateResources(context, language)
    }

    fun getLanguage(context: Context): String {
        return getPersistedData(context, getDefaultLanguage())
    }

    private fun getDefaultLanguage(): String {
        val sysLang = Locale.getDefault().language
        return if (sysLang == "vi" || sysLang == "en") sysLang else "en"
    }

    @SuppressLint("ApplySharedPref")
    private fun persist(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(SELECTED_LANGUAGE, language).commit()
    }

    private fun getPersistedData(context: Context, defaultLang: String): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_LANGUAGE, defaultLang) ?: defaultLang
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = res.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            res.updateConfiguration(config, res.displayMetrics)
            context
        }
    }
}
