package io.github.yunato.myrecordtimer.model.dao.calendars

import android.content.Context
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.dao.calendars.calendar.LocalCalendarDao
import io.github.yunato.myrecordtimer.model.dao.calendars.event.LocalEventDao
import io.github.yunato.myrecordtimer.model.entity.Record

class LocalDao private constructor(val context: Context){

    private val calendarDao: LocalCalendarDao by lazy{
        LocalCalendarDao(context)
    }
    private val eventDao: LocalEventDao by lazy {
        LocalEventDao(context)
    }

    fun createCalendar(){
        if(!calendarDao.checkExistCalendar()) calendarDao.createCalendar()
    }

    fun logCalendarInfo(){
        if(calendarDao.checkExistCalendar()) calendarDao.logCalendarInfo()
    }

    fun getAllEventItems(): List<Record>{
        return if(calendarDao.checkExistCalendar()) eventDao.getAllEventItems()
        else listOf()
    }

    fun getEventItemsOnDay(year: Int, month: Int, dayOfMonth: Int): List<Record>{
        return if(calendarDao.checkExistCalendar()) return eventDao.getEventItemsOnDay(year, month, dayOfMonth)
        else listOf()
    }

    fun getEventFromId(id: Long): Record{
        return if(calendarDao.checkExistCalendar()) return eventDao.getEventFromId(id)
        else Record("", 0, 0, context.resources.getString(R.string.edit_text_title_no),
            context.resources.getString(R.string.edit_text_memo_no), -1)
    }

    fun insertEventItems(eventItems: List<Record>): List<String>{
        return if(calendarDao.checkExistCalendar()) return eventDao.insertEventItems(eventItems)
        else listOf()
    }

    fun deleteEventItem(eventId: Long){
        if(calendarDao.checkExistCalendar()) eventDao.deleteEventItem(eventId)
    }

    companion object {
        fun getInstance(context: Context?): LocalDao = LocalDao(context ?: throw IllegalArgumentException("context is null"))
    }
}
