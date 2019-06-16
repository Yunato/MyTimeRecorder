package io.github.yunato.myrecordtimer.model.timer

import android.os.CountDownTimer
import java.text.SimpleDateFormat
import java.util.*

/**
 * @param millisInFuture start time of countdown
 * @param countDownInterval interval[ms]
 */
class MyCountDownTimer(millisInFuture: Long,
                       countDownInterval: Long = 100) : CountDownTimer(millisInFuture, countDownInterval) {
    private var mListener: OnProgressListener? = null

    private val dataFormat: SimpleDateFormat = SimpleDateFormat("hh:mm:ss", Locale.JAPAN)

    fun setOnProgressListener(listener: OnProgressListener) {
        mListener = listener
    }

    override fun onFinish() {
        mListener?.onProgress(dataFormat.format(0))
    }

    override fun onTick(millisUntilFinished: Long) {
        mListener?.onProgress(dataFormat.format(millisUntilFinished))
    }

    interface OnProgressListener {
        fun onProgress(timeText: String)
    }
}
