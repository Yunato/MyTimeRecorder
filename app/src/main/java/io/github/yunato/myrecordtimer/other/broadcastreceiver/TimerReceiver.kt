package io.github.yunato.myrecordtimer.other.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message

class TimerReceiver : BroadcastReceiver() {
    private var handler = Handler()
    private var isSet = false

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        val time = bundle.getLong(KEY_TIME_SEC)

        if(isSet){
            val msg = Message()
            val sendBundle = Bundle()
            sendBundle.putLong(KEY_TIME_SEC, time)
            msg.data = sendBundle
            handler.sendMessage(msg)
        }
    }

    fun registerHandler(handler: Handler) {
        this.handler = handler
        isSet = true
    }

    companion object {
        const val KEY_TIME_SEC = "io.github.yunato.myrecordtimer.other.broadcastreceiver.KEY_TIME_TEXT"
        const val ACTION_UPDATE = "io.github.yunato.myrecordtimer.other.broadcastreceiver.UPDATE"
    }
}

