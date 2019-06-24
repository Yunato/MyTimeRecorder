package io.github.yunato.myrecordtimer.other.task

import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import io.github.yunato.myrecordtimer.model.dao.calendars.RemoteDao

class MakeRequestTask(val dao: RemoteDao, private val errorListener: OnShowErrorDialog,
                      private val authListener: OnShowAuthDialog) : AsyncTask<Unit, Unit, Unit>() {
    var mLastError: Exception? = null

    override fun doInBackground(vararg params: Unit?) {
        dao.setAccountName(null)
        while (true) {
            try {
                dao.createCalendar()
            } catch (e: java.lang.Exception) {
                mLastError = e
                cancel(true)
            }
            Log.d("TEST", "OK")
            Thread.sleep(500)
        }
    }

    override fun onCancelled() {
        if (mLastError == null) return

        if (mLastError is GooglePlayServicesAvailabilityIOException) {
            val exception = mLastError as GooglePlayServicesAvailabilityIOException?
            errorListener.onShowErrorDialog(exception?.connectionStatusCode ?: throw IllegalStateException("exception is null"))
        } else if (mLastError is UserRecoverableAuthIOException) {
            val exception = mLastError as UserRecoverableAuthIOException?
            authListener.onShowAuthDialog(exception?.intent ?: throw IllegalStateException("exception is null"))
        }
    }

    interface OnShowErrorDialog{
        fun onShowErrorDialog(statusCode: Int)
    }

    interface OnShowAuthDialog{
        fun onShowAuthDialog(intent: Intent)
    }
}
