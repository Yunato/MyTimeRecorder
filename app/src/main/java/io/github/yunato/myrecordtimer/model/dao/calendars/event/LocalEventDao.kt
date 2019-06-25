package io.github.yunato.myrecordtimer.model.dao.calendars.event

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract.Events
import android.util.Log
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.dao.calendars.DaoPreference.Companion.IDENTIFIER_LOCAL_ID
import io.github.yunato.myrecordtimer.model.entity.Record
import java.util.*

class LocalEventDao(context: Context) : EventDao(context) {

    /** For Debug */
    private val className = Thread.currentThread().stackTrace[1].className
    private val methodName = Thread.currentThread().stackTrace[1].methodName

    private fun getEventCursor( selection: String?,
                                selectionArgs: Array<String?>?,
                                sortOrder: String?): Cursor{
        val cr = context.contentResolver
        val uri = Events.CONTENT_URI
        val projection =
            EVENTS_PROJECTION

        return try{
            cr.query(uri, projection, selection, selectionArgs, sortOrder)
        } catch (e: SecurityException) {
            throw SecurityException("No permission")
        }
    }

    override fun getAllEventItems(): List<Record>{
        val eventItems = mutableListOf<Record>()
        val selection = "${Events.CALENDAR_ID} = ?"
        val selectionArgs = arrayOf(myPreferences.getValue(IDENTIFIER_LOCAL_ID))
        val cur = getEventCursor(selection, selectionArgs, null)
        Log.d(className + methodName, "Events List of Local Calendar")
        while (cur.moveToNext()){
            val id = cur.getLong(EVENTS_PROJECTION_IDX_ID)
            val calendar_id = cur.getString(EVENTS_PROJECTION_IDX_CALENDAR_ID)
            val title = cur.getString(EVENTS_PROJECTION_IDX_TITLE)
            val description = cur.getString(EVENTS_PROJECTION_IDX_DESCRIPTION)
            val start = cur.getLong(EVENTS_PROJECTION_IDX_DTSTART)
            val end = cur.getLong(EVENTS_PROJECTION_IDX_DTEND)
            Log.d(className + methodName, "$id $calendar_id $title")
            Log.d(className + methodName, "$description $start $end")
            eventItems += Record(id.toString(), start, end, title, description, -1)
        }
        cur.close()
        return eventItems
    }

    fun getEventItemsOnDay(year: Int, month: Int, dayOfMonth: Int): List<Record>{
        val start: Long = Calendar.getInstance().run{
            set(year, month, dayOfMonth + 1, 0, 0, 0)
            timeInMillis
        }
        val end: Long = Calendar.getInstance().run{
            set(year, month, dayOfMonth, 0, 0, 0)
            timeInMillis
        }
        val selection = "${Events.CALENDAR_ID} = ? AND ${Events.DTSTART} < ? AND ${Events.DTEND} >= ?"
        val selectionArgs = arrayOf(myPreferences.getValue(IDENTIFIER_LOCAL_ID), start.toString(), end.toString())

        val eventItems = mutableListOf<Record>()
        val cur: Cursor = getEventCursor(selection, selectionArgs, null)
        Log.d(className + methodName, "Events List of Local Calendar")
        while (cur.moveToNext()){
            val id = cur.getLong(EVENTS_PROJECTION_IDX_ID)
            val calendar_id = cur.getString(EVENTS_PROJECTION_IDX_CALENDAR_ID)
            val title = cur.getString(EVENTS_PROJECTION_IDX_TITLE)
            val description = cur.getString(EVENTS_PROJECTION_IDX_DESCRIPTION)
            val start_time = cur.getLong(EVENTS_PROJECTION_IDX_DTSTART)
            val end_time = cur.getLong(EVENTS_PROJECTION_IDX_DTEND)
            Log.d(className + methodName, "$id $calendar_id $title")
            Log.d(className + methodName, "$description $start_time $end_time")
            eventItems += Record(id.toString(), start_time, end_time, title, description, -1)
        }
        cur.close()
        return eventItems
    }

    fun getEventFromId(id: Long): Record{
        val selection = "${Events.CALENDAR_ID} = ? AND ${Events._ID} = ?"
        val selectionArgs = arrayOf(myPreferences.getValue(IDENTIFIER_LOCAL_ID), id.toString())

        val cur: Cursor = getEventCursor(selection, selectionArgs, null)
        Log.d(className + methodName, "Events List of Local Calendar")
        val record: Record = if(cur.columnCount != 0){
            cur.moveToNext()
            val event_id = cur.getLong(EVENTS_PROJECTION_IDX_ID)
            val calendar_id = cur.getString(EVENTS_PROJECTION_IDX_CALENDAR_ID)
            val title = cur.getString(EVENTS_PROJECTION_IDX_TITLE)
            val description = cur.getString(EVENTS_PROJECTION_IDX_DESCRIPTION)
            val start_time = cur.getLong(EVENTS_PROJECTION_IDX_DTSTART)
            val end_time = cur.getLong(EVENTS_PROJECTION_IDX_DTEND)
            Log.d(className + methodName, "$event_id $calendar_id $title")
            Log.d(className + methodName, "$description $start_time $end_time")
            Record(id.toString(), start_time, end_time, title, description, -1)
        }else{
            Record("", 0, 0, context.resources.getString(R.string.edit_text_title_no),
                context.resources.getString(R.string.edit_text_memo_no), -1)
        }
        cur.close()
        return record
    }

    /**
     * Add event to calendar
     * @param event event's information
     * @return eventId
     */
    private fun insertEventItem(event: Record): String{
        val cr = context.contentResolver
        val values = ContentValues()
        values.put(Events.CALENDAR_ID, myPreferences.getValue(IDENTIFIER_LOCAL_ID))
        values.put(Events.TITLE, event.title)
        values.put(Events.DESCRIPTION, event.memo)
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        values.put(Events.DTSTART, event.start)
        values.put(Events.DTEND, event.end)

        return try {
            val uri = cr.insert(Events.CONTENT_URI, values) ?: throw SecurityException()
            uri.lastPathSegment
        } catch (e: SecurityException) {
            Log.e(className + methodName, "SecurityException", e)
            throw SecurityException("Not insert")
        }
    }

    override fun insertEventItems(eventItems: List<Record>): List<String> {
        val eventIds = mutableListOf<String>()
        for (eventItem: Record in eventItems){
            eventIds.add(insertEventItem(eventItem))
        }
        return eventIds
    }

    fun deleteEventItem(eventId: Long){
        val cr = context.contentResolver
        val uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId)
        try {
            cr.delete(uri, null, null)
        } catch (e: SecurityException) {
            Log.e(className + methodName, "SecurityException", e)
        }
    }

    companion object {
        private val EVENTS_PROJECTION = arrayOf(
            Events._ID,
            Events.CALENDAR_ID,
            Events.TITLE,
            Events.DESCRIPTION,
            Events.DTSTART,
            Events.DTEND
        )

        @JvmStatic private val EVENTS_PROJECTION_IDX_ID = 0
        @JvmStatic private val EVENTS_PROJECTION_IDX_CALENDAR_ID = 1
        @JvmStatic private val EVENTS_PROJECTION_IDX_TITLE = 2
        @JvmStatic private val EVENTS_PROJECTION_IDX_DESCRIPTION = 3
        @JvmStatic private val EVENTS_PROJECTION_IDX_DTSTART = 4
        @JvmStatic private val EVENTS_PROJECTION_IDX_DTEND = 5
    }
}
