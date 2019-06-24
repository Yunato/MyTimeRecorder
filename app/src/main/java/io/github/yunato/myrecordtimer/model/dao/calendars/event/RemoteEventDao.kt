package io.github.yunato.myrecordtimer.model.dao.calendars.event

import android.content.Context
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.Events
import io.github.yunato.myrecordtimer.R.string.app_name
import io.github.yunato.myrecordtimer.R.string.time_zone
import io.github.yunato.myrecordtimer.model.dao.calendars.DaoPreference.Companion.IDENTIFIER_REMOTE_ID
import io.github.yunato.myrecordtimer.model.entity.Record
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class RemoteEventDao(context: Context, credential: GoogleAccountCredential) : EventDao(context) {

    /** For Debug */
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

    override fun getAllEventItems(): List<Record> {
        val calendarId = myPreferences.getValue(IDENTIFIER_REMOTE_ID)
        val eventItems = mutableListOf<Record>()
        var pageToken: String? = null
        Log.d(className + methodName, "Events List of Remote Calendar")
        while (true) {
            val events: Events? = mService?.events()?.list(calendarId)?.setPageToken(pageToken)?.execute()

            if (events != null) {
                val eventList = events.items
                for (event in eventList) {
                    val id = event.id
                    val title = event.summary
                    val memo = event.description
                    val start = event.start.dateTime.value
                    val end = event.end.dateTime.value
                    Log.d(className + methodName, "$id $title $memo")
                    Log.d(className + methodName, "$start $end")
                    eventItems.add(Record(id, start, end, title, memo, -1))
                }
                pageToken = events.nextPageToken
            }

            if (pageToken == null) {
                break
            }
        }
        return eventItems
    }

    fun insertEventItem(eventInfo: Record): String {
        var event = Event()
        event.summary = eventInfo.title
        event.description = eventInfo.memo
        event.start = convertTimeToRFC3339(eventInfo.start)
        event.end = convertTimeToRFC3339(eventInfo.end)

        val calendarId = myPreferences.getValue(IDENTIFIER_REMOTE_ID)
        event = mService?.events()?.insert(calendarId, event)?.execute() ?: throw IOException("Not insert record")
        return event.id
    }

    private fun convertTimeToRFC3339(time: Long): EventDateTime {
        val date = Date()
        date.time = time
        val dateTime = DateTime(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.JAPAN).format(date)
        )
        return EventDateTime().setDateTime(dateTime).setTimeZone(context.resources.getString(time_zone))
    }

    override fun insertEventItems(eventItems: List<Record>): List<String> {
        val eventIds = mutableListOf<String>()
        for (eventItem: Record in eventItems){
            eventIds.add(insertEventItem(eventItem))
        }
        return eventIds
    }

    fun deleteEventItem(eventId: Long) {
        val calendarId = myPreferences.getValue(IDENTIFIER_REMOTE_ID)
        mService?.events()?.delete(calendarId, eventId.toString())?.execute()
    }

    companion object {
        private var mService: Calendar? = null
    }
}
