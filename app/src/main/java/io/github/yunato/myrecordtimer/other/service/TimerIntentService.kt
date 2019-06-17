package io.github.yunato.myrecordtimer.other.service

import android.app.IntentService
import android.content.Context
import android.content.Intent

private const val ACTION_COUNT_UP = "io.github.yunato.myrecordtimer.other.service.action.COUNT_UP"
private const val ACTION_COUNT_DOWN = "io.github.yunato.myrecordtimer.other.service.action.COUNT_DOWN"

private const val EXTRA_PARAM1 = "io.github.yunato.myrecordtimer.other.service.extra.PARAM1"
private const val EXTRA_PARAM2 = "io.github.yunato.myrecordtimer.other.service.extra.PARAM2"

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
        TODO("Handle action count up")
    }

    private fun handleActionCountDown(param1: String, param2: String) {
        Thread.sleep(5000)
    }

    companion object {
        @JvmStatic
        fun startActionCountUp(context: Context, param1: String, param2: String) {
            val intent = Intent(context, TimerIntentService::class.java).apply {
                action = ACTION_COUNT_UP
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionCountDown(context: Context, param1: String, param2: String) {
            val intent = Intent(context, TimerIntentService::class.java).apply {
                action = ACTION_COUNT_DOWN
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }
}
