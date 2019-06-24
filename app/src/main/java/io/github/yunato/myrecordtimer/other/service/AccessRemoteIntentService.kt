package io.github.yunato.myrecordtimer.other.service

import android.app.IntentService
import android.content.Context
import android.content.Intent

class AccessRemoteIntentService : IntentService("AccessRemoteIntentService") {

    override fun onHandleIntent(intent: Intent?) = accessRemote()

    private fun accessRemote() {
    }

    companion object {

        @JvmStatic
        fun startAccessRemote(context: Context): Intent {
            return Intent(context, AccessRemoteIntentService::class.java)
        }
    }
}
