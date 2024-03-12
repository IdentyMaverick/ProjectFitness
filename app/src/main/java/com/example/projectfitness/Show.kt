package com.example.projectfitness

import android.content.Context
import android.content.SharedPreferences

private const val pref_keys = "MyAppPrefs"
private const val page_shown_keys = "PageShown"
class Show {
    fun setPageShownFlag(context: Context, isShown : Boolean){
        val pref : SharedPreferences = context.getSharedPreferences(pref_keys, Context.MODE_PRIVATE)
        pref.edit().putBoolean(page_shown_keys,isShown).apply()
    }
    fun isPageAlreadyShown(context: Context) : Boolean{
        val prefs : SharedPreferences = context.getSharedPreferences(pref_keys, Context.MODE_PRIVATE)
        return prefs.getBoolean(page_shown_keys,false)
    }
}