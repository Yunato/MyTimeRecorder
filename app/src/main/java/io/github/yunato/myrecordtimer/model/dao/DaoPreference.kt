package io.github.yunato.myrecordtimer.model.dao

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class DaoPreference internal constructor(context: Context) {

    private val preferences: SharedPreferences by lazy{
        context.getSharedPreferences(IDENTIFIER_PREF, MODE_PRIVATE)
    }

    internal fun getValue(key: String): String? {
        return preferences.getString(key, null)
    }

    internal fun setValue(key: String, defValue: String) {
        val e = preferences.edit()
        e.putString(key, defValue).apply()
    }

    companion object {
        @JvmStatic val IDENTIFIER_PREF = "MY_PREFERENCE"
        @JvmStatic val PREF_ACCOUNT_NAME = "accountName"
        @JvmStatic internal val IDENTIFIER_LOCAL_ID = "LOCAL_CALENDAR_ID"
        @JvmStatic internal val IDENTIFIER_REMOTE_ID = "REMOTE_CALENDAR_ID"
    }
}
