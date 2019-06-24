package io.github.yunato.myrecordtimer.model.dao.calendars.calendar

import android.content.Context
import io.github.yunato.myrecordtimer.model.dao.calendars.DaoPreference

abstract class CalendarDao(val context: Context) {
    val myPreferences: DaoPreference by lazy {
        DaoPreference(context)
    }
}
