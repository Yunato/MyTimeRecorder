package io.github.yunato.myrecordtimer.model.dao.calendars.calendar

import android.content.Context
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.CalendarList
import io.github.yunato.myrecordtimer.R.string.app_name
import io.github.yunato.myrecordtimer.R.string.time_zone
import io.github.yunato.myrecordtimer.model.dao.calendars.DaoPreference.Companion.IDENTIFIER_REMOTE_ID
import java.io.IOException

class RemoteCalendarDao(context: Context, credential: GoogleAccountCredential) : CalendarDao(context) {

    /** For Debug  */
    private val className = Thread.currentThread().stackTrace[1].className
    private val methodName = Thread.currentThread().stackTrace[1].methodName

    init {
        if (mService == null) {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()
            mService = com.google.api.services.calendar.Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName(context.resources.getString(app_name))
                .build()
        }
    }

    /**
     * Verify whether local calendar exist or no
     */
    fun checkExistCalendar(): Boolean {
        val calendarId = myPreferences.getValue(IDENTIFIER_REMOTE_ID)
        return calendarId != null
    }

    /**
     * Create new remote calendar
     * @return ID of calendar
     */
    fun createCalendar() {
        deleteCalendar()

        val calendar = com.google.api.services.calendar.model.Calendar()
        calendar.summary = context.resources.getString(app_name)
        calendar.timeZone = context.resources.getString(time_zone)

        val calendarId = try {
            val createdCalendar = mService?.calendars()?.insert(calendar)?.execute()
            Log.d(className + methodName, "Create Remote calendar")
            createdCalendar?.id
        } catch (e: IOException) {
            Log.e(className + methodName, "SecurityException", e)
            val nothing = null
            nothing
        }
        myPreferences.setValue(IDENTIFIER_REMOTE_ID,
            calendarId ?: throw IllegalStateException("Not create remote calendar"))
    }

    @Throws(IOException::class)
    fun deleteCalendar() {
        var pageToken: String? = null
        do {
            val calendarList = mService?.calendarList()?.list()?.setPageToken(pageToken)?.execute()
            val entries = calendarList?.items

            for (entry in entries ?: throw IllegalStateException("Calendar List is Null")) {
                if (context.resources.getString(app_name) == entry.summary) {
                    mService?.calendars()?.delete(entry.id)?.execute()
                }
            }
            pageToken = calendarList.nextPageToken
        } while (pageToken != null)
    }

    /**
     * Get calendar information
     */
    fun logCalendarInfo() {
        var pageToken: String? = null
        Log.d(className + methodName, "Remote Calendar List")
        while (true) {
            val calendarList: CalendarList? = mService?.calendarList()?.list()?.setPageToken(pageToken)?.execute()

            if (calendarList != null) {
                val entries = calendarList.items
                for (entry in entries) {
                    val id = entry.id
                    val name = entry.summary
                    val role = entry.accessRole
                    val description = entry.description
                    Log.d(className + methodName, "$id $name")
                    Log.d(className + methodName, "$role $description")
                }
                pageToken = calendarList.nextPageToken
            }

            if (pageToken == null) {
                break
            }
        }
    }

    companion object {
        private var mService: Calendar? = null
    }
}
