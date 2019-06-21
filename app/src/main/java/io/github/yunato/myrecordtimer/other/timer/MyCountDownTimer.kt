package io.github.yunato.myrecordtimer.other.timer

import android.os.CountDownTimer

/**
 * @param millisInFuture start time of countdown
 * @param countDownInterval interval[ms]
 */
class MyCountDownTimer(millisInFuture: Long,
                       countDownInterval: Long = 100) : CountDownTimer(millisInFuture, countDownInterval) {
    private var mListener: OnProgressListener? = null

    fun setOnProgressListener(listener: OnProgressListener) {
        mListener = listener
    }

    override fun onFinish() {
        mListener?.onProgress(0)
    }

    override fun onTick(millisUntilFinished: Long) {
        mListener?.onProgress(millisUntilFinished)
    }

    interface OnProgressListener {
        fun onProgress(time: Long)
    }
}
