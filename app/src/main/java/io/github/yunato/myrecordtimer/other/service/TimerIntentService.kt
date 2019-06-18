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

private const val EXTRA_PARAM1 = "io.github.yunato.myrecordtimer.other.service.extra.PARAM1"
private const val EXTRA_PARAM2 = "io.github.yunato.myrecordtimer.other.service.extra.PARAM2"

private const val CHANNEL_ID = "io.github.yunato.myrecordtimer.other.service.extra.CHANNEL"
private const val NOTIFICATION_ID = 1

class TimerIntentService : IntentService("TimerIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_COUNT_UP -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionCountUp(param1, param2)
            }
            ACTION_COUNT_DOWN -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionCountDown(param1, param2)
            }
        }
    }

    private fun handleActionCountUp(param1: String, param2: String) {
        val start = Date().time
        while(true){
            createNotification(convertTimeFormat(Date().time - start))
            Thread.sleep(250)
        }
    }

    private fun handleActionCountDown(param1: String, param2: String) {
        val sec = 3 * 60L
        val time = (sec + 1L) * 1000L - 1L
        val now = Date().time
        val goal = now + time
        while((goal - Date().time) / 1000L > 0){
            val diff = (goal - Date().time) / 1000L
            val countText = convertTimeFormat(diff)
            createNotification(countText)
            sendBroadCast(diff)
            Thread.sleep(250)
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

    private fun convertTimeFormat(time: Long): String {
        val sec = time % 60
        val min = (time / 60) % 60
        val hr = time / 60 / 60
        return String.format("%02d:%02d:%02d", hr, min, sec)
    }

    private fun sendBroadCast(time: Long){
        val intent = Intent()
        intent.putExtra(TimerReceiver.KEY_TIME_SEC, time)
        intent.action = TimerReceiver.ACTION_UPDATE
        baseContext.sendBroadcast(intent)
    }

    companion object {
        @JvmStatic
        fun startActionCountUp(context: Context, param1: String, param2: String): Intent {
            return Intent(context, TimerIntentService::class.java).apply {
                action = ACTION_COUNT_UP
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
        }

        @JvmStatic
        fun startActionCountDown(context: Context, param1: String, param2: String): Intent {
            return Intent(context, TimerIntentService::class.java).apply {
                action = ACTION_COUNT_DOWN
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
        }
    }
}
