package io.github.yunato.myrecordtimer.other.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PowerBroadCastReceiver : BroadcastReceiver() {
    var isReceived: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == Intent.ACTION_SCREEN_OFF){
            isReceived = true
        }else if(intent.action == Intent.ACTION_SCREEN_ON){
            isReceived = true
        }
    }
}
