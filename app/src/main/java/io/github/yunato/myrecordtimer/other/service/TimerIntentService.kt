package io.github.yunato.myrecordtimer.other.service

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.other.broadcastreceiver.TimerReceiver
import java.util.*

private const val ACTION_COUNT_UP = "io.github.yunato.myrecordtimer.other.service.action.COUNT_UP"
private const val ACTION_COUNT_DOWN = "io.github.yunato.myrecordtimer.other.service.action.COUNT_DOWN"

private const val EXTRA_START_TIME = "io.github.yunato.myrecordtimer.other.service.extra.START_TIME"

private const val CHANNEL_ID = "io.github.yunato.myrecordtimer.other.service.extra.CHANNEL"
private const val NOTIFICATION_ID = 1

class TimerIntentService : IntentService("TimerIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        isContinue = true
        intent?.let{
            val timeSec = it.getLongExtra(EXTRA_START_TIME, 0)
            when (it.action) {
                ACTION_COUNT_UP -> {
                    handleActionCountUp(timeSec)
                }
                ACTION_COUNT_DOWN -> {
                    handleActionCountDown(timeSec)
                }
            }
        }
    }

    private fun handleActionCountUp(timeSec: Long) {
        val start = Date().time - timeSec
        while(true){
            val diffSec = (Date().time - start)
            createNotification(convertTimeFormat(diffSec / 1000L))
            sendBroadCast(diffSec)
            Thread.sleep(250)
            if(!isContinue){
                stopSelf()
                return
            }
        }
    }

    private fun handleActionCountDown(timeSec: Long) {
        val timeMilli = (timeSec + 1L) * 1000L - 1L
        val now = Date().time
        val goal = now + timeMilli
        while((goal - Date().time) / 1000L > 0){
            val diffSec = (goal - Date().time) / 1000L
            createNotification(convertTimeFormat(diffSec))
            sendBroadCast(diffSec)
            Thread.sleep(250)
            if(!isContinue){
                stopSelf()
                return
            }
        }
        sendBroadCast(0L)
    }

    private fun createNotification(text: String){
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(resources.getText(R.string.notification_title))
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        notification.flags = Notification.FLAG_ONGOING_EVENT

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun convertTimeFormat(timeSec: Long): String {
        val sec = timeSec % 60
        val min = (timeSec / 60) % 60
        val hr = timeSec / 60 / 60
        return String.format("%02d:%02d:%02d", hr, min, sec)
    }

    private fun sendBroadCast(time: Long){
        val intent = Intent()
        intent.putExtra(TimerReceiver.KEY_TIME_SEC, time)
        intent.action = TimerReceiver.ACTION_UPDATE
        baseContext.sendBroadcast(intent)
    }

    companion object {
        @JvmStatic var isContinue: Boolean = true

        @JvmStatic
        fun startActionCountUp(context: Context, timeSec: Long): Intent {
            return Intent(context, TimerIntentService::class.java).apply {
                action = ACTION_COUNT_UP
                putExtra(EXTRA_START_TIME, timeSec)
            }
        }

        @JvmStatic
        fun startActionCountDown(context: Context, timeSec: Long): Intent {
            return Intent(context, TimerIntentService::class.java).apply {
                action = ACTION_COUNT_DOWN
                putExtra(EXTRA_START_TIME, timeSec)
            }
        }
    }
}
