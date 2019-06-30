package io.github.yunato.myrecordtimer.other.task

import android.content.Intent
import android.os.AsyncTask
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import io.github.yunato.myrecordtimer.model.dao.calendars.LocalDao
import io.github.yunato.myrecordtimer.model.dao.calendars.RemoteDao
import io.github.yunato.myrecordtimer.model.dao.sqlite.DatabaseOpenHelper
import io.github.yunato.myrecordtimer.model.dao.sqlite.RecordDBAdapter
import io.github.yunato.myrecordtimer.model.entity.OperationRecord

class MakeRequestTask(private val localDao: LocalDao,
                      private val remoteDao: RemoteDao,
                      private val dbAdapter: RecordDBAdapter,
                      private val errorListener: OnShowErrorDialog,
                      private val authListener: OnShowAuthDialog) : AsyncTask<Unit, Unit, Unit>() {
    private var mLastError: Exception? = null

    override fun doInBackground(vararg params: Unit?) {
        remoteDao.setAccountName(null)
        while (true) {
            try {
                remoteDao.createCalendar()
            } catch (e: java.lang.Exception) {
                mLastError = e
                cancel(true)
            }
            remoteDao.getAllEventItems()
            dbAdapter.getOperations().apply {
                for(operationRecord: OperationRecord in this){
                    when(operationRecord.operation){
                        DatabaseOpenHelper.OPE_ADD -> {
                            val record = localDao.getEventFromId(operationRecord.localEventId)
                            val result = remoteDao.insertEventItem(record)
                            if(result != "-1"){
                                dbAdapter.deleteOperationRecord(operationRecord.id)
                                dbAdapter.addRelation(operationRecord.localEventId, result)
                            }
                        }
                        DatabaseOpenHelper.OPE_DELETE -> {
                            val remoteId = dbAdapter.getRelation(operationRecord.localEventId)
                            if(remoteId != "-1"){
                                remoteDao.deleteEventItem(remoteId)
                                dbAdapter.deleteOperationRecord(operationRecord.id)
                            }
                        }
                    }
                }
            }
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
