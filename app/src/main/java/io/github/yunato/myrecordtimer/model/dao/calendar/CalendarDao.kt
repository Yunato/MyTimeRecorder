package io.github.yunato.myrecordtimer.model.dao.calendar

import android.content.Context
import io.github.yunato.myrecordtimer.model.dao.DaoPreference

abstract class CalendarDao(val context: Context) {
    val myPreferences: DaoPreference by lazy {
        DaoPreference(context)
    }
}
