package io.github.yunato.myrecordtimer.model.dao.event

import android.content.Context
import io.github.yunato.myrecordtimer.model.dao.DaoPreference
import io.github.yunato.myrecordtimer.model.entity.Record

abstract class EventDao(val context: Context) {
    val myPreferences: DaoPreference by lazy {
        DaoPreference(context)
    }

    abstract fun insertEventItems(eventItems: List<Record>): List<String>

    abstract fun getAllEventItems(): List<Record>
}
