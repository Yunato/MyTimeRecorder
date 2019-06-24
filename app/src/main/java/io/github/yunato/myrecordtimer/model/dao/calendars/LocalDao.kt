package io.github.yunato.myrecordtimer.model.dao.calendars

import android.content.Context
import io.github.yunato.myrecordtimer.model.dao.calendars.calendar.LocalCalendarDao
import io.github.yunato.myrecordtimer.model.dao.calendars.event.LocalEventDao
import io.github.yunato.myrecordtimer.model.entity.Record

class LocalDao private constructor(context: Context){

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

    fun insertEventItem(eventItems: List<Record>): List<String>{
        return if(calendarDao.checkExistCalendar()) return eventDao.insertEventItems(eventItems)
        else listOf()
    }

    fun deleteEventItem(eventId: Long){
        if(calendarDao.checkExistCalendar()) eventDao.deleteEventItem(eventId)
    }

    companion object {
        private var instance: LocalDao? = null

        fun getInstance(context: Context?): LocalDao = instance
            ?: synchronized(this) {
            instance
                ?: LocalDao(
                    context ?: throw IllegalArgumentException("context is null")
                )
        }.also { instance = it }
    }
}
