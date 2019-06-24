package io.github.yunato.myrecordtimer.model.dao

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import io.github.yunato.myrecordtimer.model.dao.calendar.RemoteCalendarDao
import io.github.yunato.myrecordtimer.model.dao.event.RemoteEventDao
import io.github.yunato.myrecordtimer.model.entity.Record

class RemoteDao private constructor(context: Context) {

    private val mCredential: GoogleAccountCredential by lazy{
        val pref = context.getSharedPreferences(DaoPreference.IDENTIFIER_PREF, MODE_PRIVATE)
        GoogleAccountCredential.usingOAuth2(context.applicationContext, SCOPES)
            .setBackOff(ExponentialBackOff()).setSelectedAccountName(pref.getString(DaoPreference.PREF_ACCOUNT_NAME, null))
    }
    private val calendarDao: RemoteCalendarDao by lazy{
        RemoteCalendarDao(context, mCredential)
    }
    private val eventDao: RemoteEventDao by lazy {
        RemoteEventDao(context, mCredential)
    }

    fun getChooseAccountIntent(): Intent = mCredential.newChooseAccountIntent()

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

    fun insertEventItem(eventItems: List<Record>): List<String>{
        return if(calendarDao.checkExistCalendar()) return eventDao.insertEventItems(eventItems)
        else listOf()
    }

    fun deleteEventItem(eventId: Long){
        if(calendarDao.checkExistCalendar()) eventDao.deleteEventItem(eventId)
    }

    companion object {
        private var instance: RemoteDao? = null
        private val SCOPES = mutableListOf(CalendarScopes.CALENDAR)

        fun getInstance(context: Context?): RemoteDao = instance ?: synchronized(this) {
            instance ?: RemoteDao(context ?: throw IllegalArgumentException("context is null"))
        }.also { instance = it }
    }
}
