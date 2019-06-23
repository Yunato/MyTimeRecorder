package io.github.yunato.myrecordtimer.model.dao.calendar

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import android.provider.CalendarContract.Calendars
import android.util.Log
import io.github.yunato.myrecordtimer.R.string.*
import io.github.yunato.myrecordtimer.model.dao.DaoPreference.Companion.IDENTIFIER_LOCAL_ID

class LocalCalendarDao(context: Context) : CalendarDao(context) {

    /** For Debug  */
    private val className = Thread.currentThread().stackTrace[1].className
    private val methodName = Thread.currentThread().stackTrace[1].methodName

    private fun getCalendarCursor( selection: String?,
                                   selectionArgs: Array<String>?,
                                   sortOrder: String?): Cursor {
        val cr = context.contentResolver
        val uri = Calendars.CONTENT_URI
        val projection = CALENDAR_PROJECTION

        return try {
                cr.query(uri, projection, selection, selectionArgs, sortOrder)
            } catch (e: SecurityException) {
                throw SecurityException("No permission")
            }
    }

    /**
     * Verify whether local calendar exist or no
     */
    fun checkExistCalendar(): Boolean {
        val calendarId = myPreferences.getValue(IDENTIFIER_LOCAL_ID)
        return calendarId != null
    }

    /**
     * Create new local calendar
     * @return ID of calendar
     */
    fun createCalendar() {
        deleteCalendar()

        val accountName = context.resources.getString(account_name_local)
        val cr = context.contentResolver
        var calUri = Calendars.CONTENT_URI
        calUri = calUri.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(Calendars.ACCOUNT_NAME, accountName)
            .appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
            .build()

        val values = ContentValues()
        values.put(Calendars.NAME, context.resources.getString(calendar_name) + "." + accountName)
        values.put(Calendars.ACCOUNT_NAME, accountName)
        values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER)
        values.put(Calendars.OWNER_ACCOUNT, true)
        values.put(Calendars.CALENDAR_TIME_ZONE, context.resources.getString(time_zone))
        values.put(Calendars.VISIBLE, 0)
        values.put(Calendars.SYNC_EVENTS, 1)
        values.put(Calendars.CALENDAR_DISPLAY_NAME, context.resources.getString(app_name))

        val calendarId = try {
            val uri = cr.insert(calUri, values)
            uri?.lastPathSegment
        } catch (e: SecurityException) {
            Log.e(className + methodName, "SecurityException", e)
            val nothing = null
            nothing
        }
        myPreferences.setValue(IDENTIFIER_LOCAL_ID,
            calendarId ?: throw IllegalStateException("Not create local calendar"))
    }

    private fun deleteCalendar() {
        val cr = context.contentResolver
        val uri = Calendars.CONTENT_URI
        val where = Calendars.NAME + "=?"
        val accountName = context.resources.getString(account_name_local)
        val selectionArgs = arrayOf(context.resources.getString(calendar_name) + "." + accountName)
        try {
            cr.delete(uri, where, selectionArgs)
        } catch (e: SecurityException) {
            Log.e(className + methodName, "SecurityException", e)
        }
    }

    /**
     * Get calendar information
     */
    fun logCalendarInfo() {
        val cur = getCalendarCursor(null, null, null)
        Log.d(className + methodName, "Local Calendar List")
        while (cur.moveToNext()) {
            val id = cur.getLong(CALENDAR_PROJECTION_IDX_ID)
            val name = cur.getString(CALENDAR_PROJECTION_IDX_NAME)
            val accountName = cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_NAME)
            val accountType = cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_TYPE)
            val calendarDisplayName = cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_DISPLAY_NAME)
            val calendarAccessLevel = cur.getInt(CALENDAR_PROJECTION_IDX_CALENDAR_ACCESS_LEVEL)
            val calendarTimeZone = cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_TIME_ZONE)
            val visible = cur.getInt(CALENDAR_PROJECTION_IDX_VISIBLE)
            val syncEvents = cur.getInt(CALENDAR_PROJECTION_IDX_SYNC_EVENTS)
            Log.d(className + methodName, "$id $name $accountName")
            Log.d(className + methodName, "$accountType $calendarDisplayName $calendarAccessLevel")
            Log.d(className + methodName, "$calendarTimeZone $visible $syncEvents")
            // TODO: Delete
        }
        cur.close()
    }

    companion object {
        private val CALENDAR_PROJECTION = arrayOf(
            Calendars._ID,
            Calendars.NAME,
            Calendars.ACCOUNT_NAME,
            Calendars.ACCOUNT_TYPE,
            Calendars.CALENDAR_COLOR,
            Calendars.CALENDAR_DISPLAY_NAME,
            Calendars.CALENDAR_ACCESS_LEVEL,
            Calendars.CALENDAR_TIME_ZONE,
            Calendars.VISIBLE,
            Calendars.SYNC_EVENTS,
            Calendars.OWNER_ACCOUNT
        )

        @JvmStatic private val CALENDAR_PROJECTION_IDX_ID = 0
        @JvmStatic private val CALENDAR_PROJECTION_IDX_NAME = 1
        @JvmStatic private val CALENDAR_PROJECTION_IDX_ACCOUNT_NAME = 2
        @JvmStatic private val CALENDAR_PROJECTION_IDX_ACCOUNT_TYPE = 3
        @JvmStatic private val CALENDAR_PROJECTION_IDX_CALENDAR_DISPLAY_NAME = 4
        @JvmStatic private val CALENDAR_PROJECTION_IDX_CALENDAR_ACCESS_LEVEL = 5
        @JvmStatic private val CALENDAR_PROJECTION_IDX_CALENDAR_TIME_ZONE = 6
        @JvmStatic private val CALENDAR_PROJECTION_IDX_VISIBLE = 7
        @JvmStatic private val CALENDAR_PROJECTION_IDX_SYNC_EVENTS = 8
    }
}
