package io.github.yunato.myrecordtimer.model.dao.calendars

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import io.github.yunato.myrecordtimer.model.dao.calendars.calendar.RemoteCalendarDao
import io.github.yunato.myrecordtimer.model.dao.calendars.event.RemoteEventDao
import io.github.yunato.myrecordtimer.model.entity.Record

class RemoteDao private constructor(val context: Context) {

    private val mCredential: GoogleAccountCredential by lazy{
        val pref = context.getSharedPreferences(DaoPreference.IDENTIFIER_PREF, MODE_PRIVATE)
        GoogleAccountCredential.usingOAuth2(context.applicationContext,
            SCOPES
        )
            .setBackOff(ExponentialBackOff()).setSelectedAccountName(pref.getString(DaoPreference.PREF_ACCOUNT_NAME, null))
    }
    private val calendarDao: RemoteCalendarDao by lazy{
        RemoteCalendarDao(context, mCredential)
    }
    private val eventDao: RemoteEventDao by lazy {
        RemoteEventDao(context, mCredential)
    }

    fun getChooseAccountIntent(): Intent = mCredential.newChooseAccountIntent()

    fun setAccountName(accountName: String?): Boolean{
        val setting = context.getSharedPreferences(DaoPreference.IDENTIFIER_PREF, MODE_PRIVATE)
        var registeredName = setting.getString(DaoPreference.PREF_ACCOUNT_NAME, null)
        if(accountName == null && registeredName == null) return false
        if(accountName != null) {
            val editor = setting?.edit()
            editor?.putString(DaoPreference.PREF_ACCOUNT_NAME, accountName)
            editor?.apply()
            if (registeredName == null) registeredName = accountName
        }
        mCredential.selectedAccountName = registeredName
        return true
    }

    fun getAccountName(): String?{
        return context.getSharedPreferences(DaoPreference.IDENTIFIER_PREF, MODE_PRIVATE)?.getString(
            DaoPreference.PREF_ACCOUNT_NAME, null)
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

    fun insertEventItem(eventItem: Record): String{
        return if(calendarDao.checkExistCalendar()) return eventDao.insertEventItem(eventItem)
        else "-1"
    }

    fun insertEventItems(eventItems: List<Record>): List<String>{
        return if(calendarDao.checkExistCalendar()) return eventDao.insertEventItems(eventItems)
        else listOf()
    }

    fun deleteEventItem(eventId: String){
        if(calendarDao.checkExistCalendar()) eventDao.deleteEventItem(eventId)
    }

    companion object {
        private val SCOPES = mutableListOf(CalendarScopes.CALENDAR)

        fun getInstance(context: Context?): RemoteDao =
            RemoteDao(
                context ?: throw IllegalArgumentException("context is null")
            )
    }
}
